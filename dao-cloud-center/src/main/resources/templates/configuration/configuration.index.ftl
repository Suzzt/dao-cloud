<!DOCTYPE html>
<html>
<head>
    <#import "../common/common.macro.ftl" as netCommon>
    <title>配置文件管理</title>
    <@netCommon.commonStyle />
    <!-- DataTables -->
    <link rel="stylesheet"
          href="${request.contextPath}/static/adminlte/bower_components/datatables.net-bs/css/dataTables.bootstrap.min.css">
    <!-- 代码高亮 -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/codemirror.min.css">
</head>
<style>
    .CodeMirror { border: 1px solid #ddd; height: auto; }
    .cm-s-default .cm-property { color: #905; }
    @media (max-width: 768px) {
        .row + .row { margin-top: 10px; }
    }
    @media (min-width: 769px) {
        .row + .row { margin-top: 20px; }
    }
</style>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && cookieMap["dao-cloud_adminlte_settings"]?exists && "off" == cookieMap["dao-cloud_adminlte_settings"].value >sidebar-collapse</#if>">
<div class="wrapper">
    <@netCommon.commonHeader />
    <@netCommon.commonLeft "config" />

    <div class="content-wrapper">
        <section class="content">
            <div class="row">
                <div class="col-xs-3">
                    <input type="text" class="form-control" id="searchProxy" placeholder="Proxy(精确)">
                </div>
                <div class="col-xs-3">
                    <input type="text" class="form-control" id="searchGroupId" placeholder="Group ID(精确)">
                </div>
                <div class="col-xs-3">
                    <input type="text" class="form-control" id="searchFileName" placeholder="文件名(模糊)">
                </div>
                <div class="col-xs-3" style="display: flex; justify-content: flex-end;">
                    <button class="btn btn-info" id="searchBtn"> <i class="fa fa-search"></i>搜索</button>
                    <button class="btn btn-success ml-2" id="addConfigBtn"> <i class="fa fa-plus"></i>新建</button>
                </div>
            </div>

            <div class="row">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-body">
                            <table id="configTable" class="table table-bordered table-striped">
                                <thead>
                                <tr>
                                    <th>Proxy</th>
                                    <th>Group ID</th>
                                    <th>文件名</th>
                                    <th>文件类型</th>
                                    <th>最后修改</th>
                                    <th>操作</th>
                                </tr>
                                </thead>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </div>

    <!-- 新增配置模态框 -->
    <div class="modal fade" id="addModal" tabindex="-1" role="dialog">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title">新建配置文件</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal" id="addForm">
                        <div class="form-group">
                            <label class="col-sm-2 control-label">Proxy<span class="text-red">*</span></label>
                            <div class="col-sm-10">
                                <input type="text" class="form-control" name="proxy" required>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-2 control-label">Group ID<span class="text-red">*</span></label>
                            <div class="col-sm-10">
                                <input type="text" class="form-control" name="groupId" required>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-2 control-label">文件类型<span class="text-red">*</span></label>
                            <div class="col-sm-10">
                                <select class="form-control" name="fileType" required>
                                    <option value="YAML">YAML</option>
                                    <option value="PROPERTIES">Properties</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-2 control-label">文件名<span class="text-red">*</span></label>
                            <div class="col-sm-10">
                                <input type="text" class="form-control" name="fileName" required>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-2 control-label">配置内容<span class="text-red">*</span></label>
                            <div class="col-sm-10">
                                <textarea id="configContent" name="content"></textarea>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="submit" class="btn btn-primary">提交</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<@netCommon.commonScript />
<script src="${request.contextPath}/static/adminlte/bower_components/datatables.net/js/jquery.dataTables.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/codemirror.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/mode/yaml/yaml.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.2/mode/properties/properties.min.js"></script>

<script>
    // 初始化代码编辑器
    const editor = CodeMirror.fromTextArea(document.getElementById('configContent'), {
        lineNumbers: true,
        mode: 'yaml',
        theme: 'default',
        lineWrapping: true
    });

    // 切换文件类型时改变编辑器模式
    $('select[name="fileType"]').change(function() {
        editor.setOption('mode', this.value.toLowerCase());
    });

    // 初始化DataTable
    const table = $('#configTable').DataTable({
        ajax: {
            url: '/api/config/list',
            dataSrc: 'data'
        },
        columns: [
            { data: 'proxy' },
            { data: 'groupId' },
            { data: 'fileName' },
            { data: 'fileType' },
            { data: 'updateTime' },
            {
                data: null,
                render: function(data) {
                    return `
                    <button class="btn btn-xs btn-primary" onclick="editConfig('${data.id}')">编辑</button>
                    <button class="btn btn-xs btn-danger" onclick="deleteConfig('${data.id}')">删除</button>
                `;
                }
            }
        ]
    });

    // 搜索功能
    $('#searchBtn').click(function() {
        table.ajax.url('/api/config/list?proxy=' + $('#searchProxy').val() +
            '&groupId=' + $('#searchGroupId').val() +
            '&fileName=' + $('#searchFileName').val()).load();
    });

    // 表单提交
    $('#addForm').submit(function(e) {
        e.preventDefault();
        $.ajax({
            url: '/api/config/save',
            method: 'POST',
            data: $(this).serialize(),
            success: function() {
                $('#addModal').modal('hide');
                table.ajax.reload();
            }
        });
    });

    function editConfig(id) {
        // 加载配置详情并打开编辑模态框
        $.get('/api/config/detail/' + id, function(data) {
            const form = $('#addForm');
            form.find('[name="proxy"]').val(data.proxy);
            form.find('[name="groupId"]').val(data.groupId);
            form.find('[name="fileType"]').val(data.fileType);
            form.find('[name="fileName"]').val(data.fileName);
            editor.setValue(data.content);
            $('#addModal').modal('show');
        });
    }
</script>
</body>
</html>