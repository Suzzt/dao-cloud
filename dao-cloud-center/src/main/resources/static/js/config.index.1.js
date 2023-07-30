$(function () {
    var dataTable = $("#data_list").dataTable({
        "deferRender": true,
        "processing": true,
        "serverSide": true,
        "ajax": {
            url: base_url + "/config/pageList",
            type: "post",
            data: function (d) {
                var obj = {};
                obj.page = d.start == 0 || d.start == null ? 1 : d.start;
                obj.size = d.length == 0 || d.length == null ? 10 : d.length;
                obj.proxy = $('#proxy').val();
                obj.key = $('#key').val();
                obj.version = $('#version').val();
                obj.content = $('#content').val();
                return obj;
            }
        },
        "searching": false,
        "ordering": false,
        //"scrollX": true,	// X轴滚动条，取消自适应
        "columns": [
            {data: 'proxy'},
            {data: 'key'},
            {data: 'version'},
            {data: 'content'},
            {
                data: 'opt',
                "render": function (data, type, row) {
                    return function () {
                        tableData['key' + row.id] = row;
                        var html = '<p proxy="' + row.proxy + '" key="' + row.key + '" version="' + row.version + '" content="' + row.content + '" >' +
                            '<button class="btn btn-info btn-xs config_update" type="button">编辑</button>  ' +
                            '<button class="btn btn-danger btn-xs config_remove" type="button">删除</button>  ' +
                            '</p>';
                        return html;
                    };
                }
            }
        ],
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
        }
    });

    // table data
    var tableData = {};

    // msg 弹框
    $("#data_list").on('click', '.showData', function () {
        var _id = $(this).attr('_id');
        var row = tableData['key' + _id];
        ComAlertTec.show(row.data);
    });

    // search btn
    $('#searchBtn').on('click', function () {
        dataTable.fnDraw();
    });

    // config_remove
    $("#data_list").on('click', '.config_remove', function () {

        var proxy = $(this).parent('p').attr("proxy");
        var key = $(this).parent('p').attr("key");
        var version = $(this).parent('p').attr("version");

        layer.confirm("确认删除该配置?", {
            icon: 3,
            title: "系统提示",
            btn: ["确认", "取消"]
        }, function (index) {
            layer.close(index);

            $.ajax({
                type: 'POST',
                url: base_url + "/config/delete",
                data: {
                    "proxy": proxy,
                    "key": key,
                    "version": version
                },
                dataType: "json",
                success: function (data) {
                    if (data.code == "00000") {

                        layer.open({
                            title: "系统提示",
                            btn: ["确认"],
                            content: "删除成功",
                            icon: '1',
                            end: function (layero, index) {
                                dataTable.fnDraw(false);
                            }
                        });
                    } else {
                        layer.open({
                            title: "系统提示",
                            btn: ["确认"],
                            content: (data.msg || "删除失败"),
                            icon: '2'
                        });
                    }
                }
            });
        });

    });

    // config_add
    $('#config_add').on('click', function () {
        $('#addModal').modal({backdrop: false, keyboard: false}).modal('show');
    });
    var addModalValidate = $("#addModal .form").validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: true,
        rules: {
            proxy: {
                required: true,
                rangelength: [1, 255]
            },
            key: {
                required: true,
                rangelength: [1, 255]
            },
            version: {
                required: true,
                rangelength: [1, 8]
            },
            content: {
                required: true,
                rangelength: [1, 10000]
            }
        },
        messages: {
            proxy: {
                required: '请输入',
                rangelength: '长度限制为[1~255]'
            },
            key: {
                required: '请输入',
                rangelength: '长度限制为[1~255]'
            },
            version: {
                required: '请输入',
                rangelength: '长度限制为[1~8]'
            },
            content: {
                required: '请输入',
                rangelength: '长度限制为[1~10000]'
            }
        },
        highlight: function (element) {
            $(element).closest('.form-group').addClass('has-error');
        },
        success: function (label) {
            label.closest('.form-group').removeClass('has-error');
            label.remove();
        },
        errorPlacement: function (error, element) {
            element.parent('div').append(error);
        },
        submitHandler: function (form) {

            // valid
            var dataJson = $("#addModal .form textarea[name='data']").val();
            if (dataJson) {
                try {
                    $.parseJSON(dataJson);
                } catch (e) {
                    layer.open({
                        icon: '2',
                        content: "注册信息格式非法必须是json <br>" + e
                    });
                    return;
                }
            }

            // post
            $.post(base_url + "/config/save", $("#addModal .form").serialize(), function (data, status) {
                if (data.code == "00000") {
                    $('#addModal').modal('hide');

                    layer.open({
                        title: "系统提示",
                        btn: ["确认"],
                        content: "新增成功",
                        icon: '1',
                        end: function (layero, index) {
                            dataTable.fnDraw(false);
                        }
                    });
                } else {
                    layer.open({
                        title: "系统提示",
                        btn: ["确认"],
                        content: (data.msg || "操作失败"),
                        icon: '2'
                    });
                }
            });
        }
    });
    $("#addModal").on('hide.bs.modal', function () {
        $("#addModal .form")[0].reset();
        addModalValidate.resetForm();
        $("#addModal .form .form-group").removeClass("has-error");
    });

    // config_update
    $("#data_list").on('click', '.config_update', function () {
        var proxy = $(this).parent('p').attr("proxy");
        var key = $(this).parent('p').attr("key");
        var version = $(this).parent('p').attr("version");
        var content = $(this).parent('p').attr("content");
        $("#updateModal .form input[name='proxy']").val(proxy);
        $("#updateModal .form input[name='key']").val(key);
        $("#updateModal .form input[name='version']").val(version);
        $("#updateModal .form textarea[name='content']").val(content);
        $('#updateModal').modal({backdrop: false, keyboard: false}).modal('show');
    });
    var updateModalValidate = $("#updateModal .form").validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: true,
        rules: {
            proxy: {
                required: true,
                rangelength: [1, 255]

            },
            key: {
                required: true,
                rangelength: [1, 255]
            },
            version: {
                required: true,
                rangelength: [1, 8]
            },
            content: {
                required: true,
                rangelength: [1, 10000]
            }
        },
        messages: {
            proxy: {
                required: '请输入',
                rangelength: '长度限制为[1~255]'
            },
            key: {
                required: '请输入',
                rangelength: '长度限制为[1~255]'
            },
            version: {
                required: '请输入',
                rangelength: '长度限制为[1~8]'
            },
            content: {
                required: '请输入',
                rangelength: '长度限制为[1~10000]'
            }
        },
        highlight: function (element) {
            $(element).closest('.form-group').addClass('has-error');
        },
        success: function (label) {
            label.closest('.form-group').removeClass('has-error');
            label.remove();
        },
        errorPlacement: function (error, element) {
            element.parent('div').append(error);
        },
        submitHandler: function (form) {
            // valid
            var dataJson = $("#addModal .form textarea[name='data']").val();
            if (dataJson) {
                try {
                    $.parseJSON(dataJson);
                } catch (e) {
                    layer.open({
                        icon: '2',
                        content: "注册信息格式非法必须是json <br>" + e
                    });
                    return;
                }
            }

            // post
            $.post(base_url + "/config/save", $("#updateModal .form").serialize(), function (data, status) {
                if (data.code == "00000") {
                    $('#updateModal').modal('hide');

                    layer.open({
                        title: "系统提示",
                        btn: ["确认"],
                        content: "更新成功",
                        icon: '1',
                        end: function (layero, index) {
                            dataTable.fnDraw(false);
                        }
                    });
                } else {
                    layer.open({
                        title: "系统提示",
                        btn: ["确认"],
                        content: (data.msg || "更新失败"),
                        icon: '2'
                    });
                }
            });
        }
    });
    $("#updateModal").on('hide.bs.modal', function () {
        $("#updateModal .form")[0].reset();
        updateModalValidate.resetForm();
        $("#updateModal .form .form-group").removeClass("has-error");
    });


});


// Com Alert by Tec theme
var ComAlertTec = {
    html: function () {
        var html =
            '<div class="modal fade" id="ComAlertTec" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">' +
            '<div class="modal-dialog">' +
            '<div class="modal-content-tec">' +
            '<div class="modal-body"><div class="alert" style="color:#fff;"></div></div>' +
            '<div class="modal-footer">' +
            '<div class="text-center" >' +
            '<button type="button" class="btn btn-info ok" data-dismiss="modal" >确认</button>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '</div>';
        return html;
    },
    show: function (msg, callback) {
        // dom init
        if ($('#ComAlertTec').length == 0) {
            $('body').append(ComAlertTec.html());
        }

        // init com alert
        $('#ComAlertTec .alert').html(msg);
        $('#ComAlertTec').modal('show');

        $('#ComAlertTec .ok').click(function () {
            $('#ComAlertTec').modal('hide');
            if (typeof callback == 'function') {
                callback();
            }
        });
    }
};