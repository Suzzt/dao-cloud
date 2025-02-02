<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nacos Configuration Management</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.datatables.net/1.10.21/css/dataTables.bootstrap4.min.css">
    <style>
        .modal-content-tec {
            background-color: #5bc0de;
        }
    </style>
</head>
<body>
<div class="container mt-5">
    <h1 class="mb-4">Nacos Configuration Management</h1>
    <div class="mb-3">
        <button id="config_add" class="btn btn-primary">Add Configuration</button>
    </div>
    <table id="data_list" class="table table-striped table-bordered">
        <thead>
        <tr>
            <th>Proxy</th>
            <th>Group ID</th>
            <th>File Name</th>
            <th>Content</th>
            <th>Actions</th>
        </tr>
        </thead>
    </table>
</div>

<!-- Add Modal -->
<div class="modal fade" id="addModal" tabindex="-1" role="dialog" aria-labelledby="addModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="addModalLabel">Add Configuration</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <form class="form">
                    <div class="form-group">
                        <label for="proxy">Proxy</label>
                        <input type="text" class="form-control" name="proxy" required>
                    </div>
                    <div class="form-group">
                        <label for="groupId">Group ID</label>
                        <input type="text" class="form-control" name="groupId" required>
                    </div>
                    <div class="form-group">
                        <label for="fileName">File Name</label>
                        <input type="text" class="form-control" name="fileName" required>
                    </div>
                    <div class="form-group">
                        <label for="content">Content</label>
                        <textarea class="form-control" name="content" required></textarea>
                    </div>
                    <button type="submit" class="btn btn-primary">Add</button>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Update Modal -->
<div class="modal fade" id="updateModal" tabindex="-1" role="dialog" aria-labelledby="updateModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="updateModalLabel">Update Configuration</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <form class="form">
                    <div class="form-group">
                        <label for="proxy">Proxy</label>
                        <input type="text" class="form-control" name="proxy" required>
                    </div>
                    <div class="form-group">
                        <label for="groupId">Group ID</label>
                        <input type="text" class="form-control" name="groupId" required>
                    </div>
                    <div class="form-group">
                        <label for="fileName">File Name</label>
                        <input type="text" class="form-control" name="fileName" required>
                    </div>
                    <div class="form-group">
                        <label for="content">Content</label>
                        <textarea class="form-control" name="content" required></textarea>
                    </div>
                    <button type="submit" class="btn btn-primary">Update</button>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Message Modal -->
<div class="modal fade" id="ComAlertTec" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content-tec">
            <div class="modal-body">
                <div class="alert" style="color:#fff;"></div>
            </div>
            <div class="modal-footer">
                <div class="text-center">
                    <button type="button" class="btn btn-info ok" data-dismiss="modal">Confirm</button>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
<script src="https://cdn.datatables.net/1.10.21/js/jquery.dataTables.min.js"></script>
<script src="https://cdn.datatables.net/1.10.21/js/dataTables.bootstrap4.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/jquery-validation@1.19.2/dist/jquery.validate.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/jquery-validation@1.19.2/dist/additional-methods.min.js"></script>
<script>
    $(function () {
        var base_url = "";  // Set your base URL here

        var dataTable = $("#data_list").DataTable({
            "deferRender": true,
            "processing": true,
            "serverSide": true,
            "ajax": {
                url: base_url + "/configuration/pageList",
                type: "post",
                data: function (d) {
                    return {
                        start: d.start,
                        length: d.length,
                        proxy: $('#proxy').val(),
                        groupId: $('#groupId').val()
                    };
                }
            },
            "searching": false,
            "ordering": false,
            "columns": [
                {data: 'proxy'},
                {data: 'groupId'},
                {data: 'fileName'},
                {data: 'content'},
                {
                    data: 'opt',
                    "render": function (data, type, row) {
                        return function () {
                            tableData['key' + row.id] = row;
                            return '<p proxy="' + row.proxy + '" groupId="' + row.groupId + '" fileName="' + row.fileName + '" content="' + row.content + '" >' +
                                '<button class="btn btn-info btn-xs config_update" type="button">编辑</button>  ' +
                                '<button class="btn btn-danger btn-xs config_remove" type="button">删除</button>  ' +
                                '</p>';
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

        var tableData = {};

        // Add Configuration
        $('#config_add').on('click', function () {
            $('#addModal').modal({backdrop: false, keyboard: false}).modal('show');
        });

        var addModalValidate = $("#addModal .form").validate({
            errorElement: 'span',
            errorClass: 'help-block',
            focusInvalid: true,
            rules: {
                proxy: { required: true, rangelength: [1, 255] },
                groupId: { required: true, rangelength: [1, 255] },
                fileName: { required: true, rangelength: [1, 255] },
                content: { required: true, rangelength: [1, 10000] }
            },
            messages: {
                proxy: { required: '请输入', rangelength: '长度限制为[1~255]' },
                groupId: { required: '请输入', rangelength: '长度限制为[1~255]' },
                fileName: { required: '请输入', rangelength: '长度限制为[1~255]' },
                content: { required: '请输入', rangelength: '长度限制为[1~10000]' }
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
                $.post(base_url + "/config/save", $("#addModal .form").serialize(), function (data, status) {
                    if (data.code == "00000") {
                        $('#addModal').modal('hide');
                        showMessage("新增成功", 1, function () {
                            dataTable.draw(false);
                        });
                    } else {
                        showMessage(data.msg || "操作失败", 2);
                    }
                });
            }
        });

        $("#addModal").on('hide.bs.modal', function () {
            $("#addModal .form")[0].reset();
            addModalValidate.resetForm();
            $("#addModal .form .form-group").removeClass("has-error");
        });

        // Update Configuration
        $("#data_list").on('click', '.config_update', function () {
            var proxy = $(this).parent('p').attr("proxy");
            var groupId = $(this).parent('p').attr("groupId");
            var fileName = $(this).parent('p').attr("fileName");
            var content = $(this).parent('p').attr("content");
            $("#updateModal .form input[name='proxy']").val(proxy);
            $("#updateModal .form input[name='groupId']").val(groupId);
            $("#updateModal .form input[name='fileName']").val(fileName);
            $("#updateModal .form textarea[name='content']").val(content);
            $('#updateModal').modal({backdrop: false, keyboard: false}).modal('show');
        });

        var updateModalValidate = $("#updateModal .form").validate({
            errorElement: 'span',
            errorClass: 'help-block',
            focusInvalid: true,
            rules: {
                proxy: { required: true, rangelength: [1, 255] },
                groupId: { required: true, rangelength: [1, 255] },
                fileName: { required: true, rangelength: [1, 255] },
                content: { required: true, rangelength: [1, 10000] }
            },
            messages: {
                proxy: { required: '请输入', rangelength: '长度限制为[1~255]' },
                groupId: { required: '请输入', rangelength: '长度限制为[1~255]' },
                fileName: { required: '请输入', rangelength: '长度限制为[1~255]' },
                content: { required: '请输入', rangelength: '长度限制为[1~10000]' }
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
                $.post(base_url + "/config/save", $("#updateModal .form").serialize(), function (data, status) {
                    if (data.code == "00000") {
                        $('#updateModal').modal('hide');
                        showMessage("更新成功", 1, function () {
                            dataTable.draw(false);
                        });
                    } else {
                        showMessage(data.msg || "操作失败", 2);
                    }
                });
            }
        });

        $("#updateModal").on('hide.bs.modal', function () {
            $("#updateModal .form")[0].reset();
            updateModalValidate.resetForm();
            $("#updateModal .form .form-group").removeClass("has-error");
        });

        // Remove Configuration
        $("#data_list").on('click', '.config_remove', function () {
            var proxy = $(this).parent('p').attr("proxy");
            var groupId = $(this).parent('p').attr("groupId");
            var fileName = $(this).parent('p').attr("fileName");
            layer.confirm("确认删除该配置?", {
                icon: 3,
                title: "系统提示",
                btn: ["确认", "取消"]
            }, function (index) {
                layer.close(index);
                $.ajax({
                    type: 'POST',
                    url: base_url + "/config/delete",
                    data: { "proxy": proxy, "groupId": groupId, "fileName": fileName },
                    dataType: "json",
                    success: function (data) {
                        if (data.code == "00000") {
                            showMessage("删除成功", 1, function () {
                                dataTable.draw(false);
                            });
                        } else {
                            showMessage(data.msg || "删除失败", 2);
                        }
                    }
                });
            });
        });

        // Show Message Modal
        function showMessage(msg, icon, callback) {
            if ($('#ComAlertTec').length == 0) {
                $('body').append(ComAlertTec.html());
            }
            $('#ComAlertTec .alert').html(msg);
            $('#ComAlertTec').modal('show');
            $('#ComAlertTec .ok').click(function () {
                $('#ComAlertTec').modal('hide');
                if (typeof callback == 'function') {
                    callback();
                }
            });
        }
    });

    var ComAlertTec = {
        html: function () {
            return '<div class="modal fade" id="ComAlertTec" tabindex="-1" role="dialog" aria-hidden="true">' +
                '<div class="modal-dialog">' +
                '<div class="modal-content-tec">' +
                '<div class="modal-body"><div class="alert" style="color:#fff;"></div></div>' +
                '<div class="modal-footer"><div class="text-center">' +
                '<button type="button" class="btn btn-info ok" data-dismiss="modal">Confirm</button>' +
                '</div></div></div></div></div>';
        }
    };
</script>
</body>
</html>