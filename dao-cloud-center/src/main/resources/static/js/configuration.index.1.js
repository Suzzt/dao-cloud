var newEditor, updateEditor;

$('#addModal, #updateModal').on('shown.bs.modal', function () {
    const editorContainer = $(this).find('.editor-container')[0];
    const modalContentHeight = $(this).find('.modal-content').height();

    // 计算可用高度 = 模态框总高度 - 表单控件高度 - 安全边距
    const calculatedHeight = modalContentHeight - 600;

    // 设置动态高度（限制在200-700px之间）
    editorContainer.style.height = Math.min(Math.max(calculatedHeight, 200), 700) + 'px';

    // 刷新CodeMirror实例
    if (newEditor) newEditor.refresh();
    if (updateEditor) updateEditor.refresh();
});

// 新增模态框关闭时重置数据
$('#addModal').on('hidden.bs.modal', function() {
    // 重置表单字段
    const $form = $(this).find('form');
    $form[0].reset();

    // 清空编辑器内容
    newEditor.setValue('');

    // 强制重置文件类型为YAML
    const $fileType = $form.find('#fileType');
    $fileType.val('.yaml').trigger('change');

    // 清除验证错误提示
    $form.validate().resetForm();
});

$(function () {
    // 初始化编辑器
    function initEditors() {
        // 新增编辑器
        var addContainer = $("#addModal .editor-container");
        newEditor = CodeMirror(addContainer[0], {
            lineNumbers: true,
            gutters: ["CodeMirror-linenumbers"],
            theme: "material-darker",
            mode: "yaml",
            lint: true
        });

        // 更新编辑器
        var updateContainer = $("#updateModal .editor-container");
        updateEditor = CodeMirror(updateContainer[0], {
            lineNumbers: true,
            theme: "material-darker",
            mode: "yaml",
            gutters: ["CodeMirror-linenumbers"],
            lint: true
        });
    }
    initEditors();

    // 文件类型切换
    $('select[id="fileType"]').on('change', function() {
        const mode = $(this).val() === '.yaml' ? 'yaml' : 'properties';
        const isAddModal = $(this).closest('.modal').is('#addModal');
        const editor = isAddModal ? newEditor : updateEditor;

        editor.setOption("mode", mode);
        if (mode === 'properties') {
            editor.setOption('lint', false);
        } else {
            editor.setOption('lint', true);
        }
    });

    // 自动补全文件后缀
    $('input[name="fileName"]').on('blur', function() {
        const $input = $(this);
        const fileType = $input.closest('.modal').find('#fileType').val();
        if (!$input.val().endsWith(fileType)) {
            $input.val($input.val().replace(/\..*$/, '') + fileType);
        }
    });

    // DataTables配置
    var dataTable = $("#data_list").DataTable({
        serverSide: true,
        "deferRender": true,
        "processing": true,
        "searching": false,
        "ordering": false,
        "language": {
            "sProcessing": "处理中...",
            "sLengthMenu": "每页 _MENU_ 条记录",
            "sZeroRecords": "没有匹配结果",
            "sInfo": "第 _PAGE_ 页 ( 总共 _PAGES_ 页 ) 总记录数 _MAX_ ",
            "sInfoEmpty": "无记录",
            "sInfoFiltered": "(由 _MAX_ 项结果过滤)",
            "sInfoPostFix": "",
            "sSearch": "搜索:",
            "sUrl": "",
            "sEmptyTable": "表中数据为空",
            "sLoadingRecords": "载入中...",
            "sInfoThousands": ",",
            "oPaginate": {
                "sFirst": "首页",
                "sPrevious": "上页",
                "sNext": "下页",
                "sLast": "末页"
            },
            "oAria": {
                "sSortAscending": ": 以升序排列此列",
                "sSortDescending": ": 以降序排列此列"
            }
        },
        ajax: {
            url: base_url + "/configuration/pageList",
            type: "POST",
            data: function(d) {
                return {
                    start: d.start,
                    length: d.length,
                    proxy: $('#proxy').val(),
                    groupId: $('#groupId').val(),
                    fileName: $('#fileName').val()
                };
            }
        },
        columns: [
            { data: 'proxy' },
            { data: 'groupId' },
            {
                data: 'fileName',
                render: function(data) {
                    const icon = data.endsWith('.yaml') ? '📄' : '⚙️';
                    return `<span>${data} ${icon}</span>`;
                }
            },
            {
                data: null,
                render: function(data, type, row) {
                    return `
                        <button class="btn btn-info btn-xs config_update" 
                            data-proxy="${row.proxy}" 
                            data-groupid="${row.groupId}" 
                            data-filename="${row.fileName}">编辑</button>
                        <button class="btn btn-danger btn-xs configuration_remove" 
                            data-proxy="${row.proxy}" 
                            data-groupid="${row.groupId}" 
                            data-filename="${row.fileName}">删除</button>
                    `;
                }
            }
        ]
    });

    // 搜索功能
    $('#searchBtn').click(function() {
        dataTable.ajax.reload();
    });

    // 新增配置
    $('#configuration_add').click(function() {
        $('#addModal').modal('show');
    });

    // 保存新增
    $("#addModal form").submit(function(e) {
        e.preventDefault();
        const formData = {
            proxy: $('input[name="proxy"]', this).val(),
            groupId: $('input[name="groupId"]', this).val(),
            fileName: $('input[name="fileName"]', this).val(),
            content: newEditor.getValue()
        };

        $.post(base_url + "/configuration/save", formData, function(res) {
            if (res.code === "00000") {
                $('#addModal').modal('hide');
                dataTable.ajax.reload();
            }
            layer.msg(res.code === "00000" ? "操作成功" : res.message);
        });
    });

    // 更新配置
    $("#data_list").on('click', '.config_update', function() {
        const proxy = $(this).data('proxy');
        const groupId = $(this).data('groupid');
        const fileName = $(this).data('filename');
        const fileType = fileName.endsWith('.yaml') ? '.yaml' : '.properties';

        // 设置表单值
        $("#updateModal input[name='proxy']").val(proxy);
        $("#updateModal input[name='groupId']").val(groupId);
        $("#updateModal input[name='fileName']").val(fileName);
        $("#updateModal #fileType").val(fileType).trigger('change');

        // 加载内容
        $.get(base_url + "/configuration/property", { proxy, groupId, fileName }, function(res) {
            if (res.code === "00000") {
                updateEditor.setValue(res.data || '');
                $('#updateModal').modal('show');
            }
        });
    });

    // 保存更新
    $("#updateModal form").submit(function(e) {
        e.preventDefault();
        const formData = {
            proxy: $('input[name="proxy"]', this).val(),
            groupId: $('input[name="groupId"]', this).val(),
            fileName: $('input[name="fileName"]', this).val(),
            content: updateEditor.getValue()
        };

        $.post(base_url + "/configuration/save", formData, function(res) {
            if (res.code === "00000") {
                $('#updateModal').modal('hide');
                dataTable.ajax.reload();
            }
            layer.msg(res.code === "00000" ? "操作成功" : res.message);
        });
    });

    // 删除配置
    $("#data_list").on('click', '.configuration_remove', function() {
        const proxy = $(this).data('proxy');
        const groupId = $(this).data('groupid');
        const fileName = $(this).data('filename');

        layer.confirm("确认删除该配置？", function(index) {
            $.post(base_url + "/configuration/delete", { proxy, groupId, fileName }, function(res) {
                layer.msg(res.msg || (res.code === "00000" ? "删除成功" : "删除失败"));
                if (res.code === "00000") {
                    dataTable.ajax.reload();
                }
            });
            layer.close(index);
        });
    });
});