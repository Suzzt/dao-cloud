$(function () {
    var dataTable = $("#data_list").dataTable({
        "paging": true,
        "deferRender": true, "processing": true, "serverSide": false, "ajax": {
            url: base_url + "/registry/pageList", type: "post", data: function (d) {
                var obj = {};
                obj.start = d.start;
                obj.length = d.length;
                obj.proxy = $('#proxy').val();
                obj.provider = $('#provider').val();
                obj.version = $('#version').val();
                return obj;
            }
        }, "searching": false, "ordering": false, //"scrollX": true,	// X轴滚动条，取消自适应
        "columns": [{data: 'proxy'}, {data: 'provider'}, {data: 'version'}, {
            data: 'number', ordering: true, render: function (data, type, row) {
                if (data != 0) {
                    return '<a href="javascript:;" class="showData" proxy="' + row.proxy + '" provider="' + row.provider + '" version="' + row.version + '">' + data + '</a>';
                } else {
                    return '0';
                }

            }
        }], "language": {
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
                "sFirst": "首页", "sPrevious": "上页", "sNext": "下页", "sLast": "末页"
            },
            "oAria": {
                "sSortAscending": ": 以升序排列此列", "sSortDescending": ": 以降序排列此列"
            }
        }
    });

    // table data
    var tableData = {};

    // msg 弹框
    $("#data_list").on('click', '.showData', function () {
        var proxy = $(this).attr("proxy");
        var provider = $(this).attr("provider");
        var version = $(this).attr("version");
        $.ajax({
            url: base_url + "/registry/server?proxy=" + proxy + "&provider=" + provider + "&version=" + version,
            method: "GET",
            dataType: "json",
            success: function (response) {
                var tableHtml = '';

                response.data.forEach(function (item) {
                    tableHtml += '<tr><td>' + item.ip + '</td><td>' + item.port + '</td></tr>';
                });
                // 使用 Layer 弹窗展示数组数据
                layer.open({
                    type: 1,
                    title: '注册服务节点列表',
                    content: $('#popup'),
                    area: ['500px', '300px'],
                    btn: ['确认'],
                    success: function (layero, index) {
                        $('#popup-list tbody').html(tableHtml);
                    }
                });
            },
            error: function () {
                // 处理请求失败的情况
            }
        });
    });

// search btn
    $('#searchBtn').on('click', function () {
        var proxyValue = $('#proxy').val();
        var providerValue = $('#provider').val();
        var versionValue = $('#version').val();
        dataTable.fnSettings().ajax.data = function (d) {
            d.proxy = proxyValue;
            d.provider = providerValue;
            d.version = versionValue;
        }
        dataTable.api().ajax.reload();
    });

// registry_remove
    $("#data_list").on('click', '.registry_remove', function () {

        var id = $(this).parent('p').attr("id");

        layer.confirm("确认删除该服务?", {
            icon: 3, title: "系统提示", btn: ["确认", "取消"]
        }, function (index) {
            layer.close(index);

            $.ajax({
                type: 'POST', url: base_url + "/registry/delete", data: {
                    "id": id
                }, dataType: "json", success: function (data) {
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
                            title: "系统提示", btn: ["确认"], content: (data.msg || "删除失败"), icon: '2'
                        });
                    }
                }
            });
        });

    });

// registry_add
    $('#registry_add').on('click', function () {
        $('#addModal').modal({backdrop: false, keyboard: false}).modal('show');
    });
    var addModalValidate = $("#addModal .form").validate({
        errorElement: 'span', errorClass: 'help-block', focusInvalid: true, rules: {
            proxy: {
                required: true, rangelength: [4, 255]
            }, provider: {
                required: true, rangelength: [4, 255]
            }, version: {
                required: true, rangelength: [2, 255]
            }
        }, messages: {
            proxy: {
                required: '请输入', rangelength: '长度限制为[4~255]'
            }, provider: {
                required: '请输入', rangelength: '长度限制为[4~255]'
            }
        }, highlight: function (element) {
            $(element).closest('.form-group').addClass('has-error');
        }, success: function (label) {
            label.closest('.form-group').removeClass('has-error');
            label.remove();
        }, errorPlacement: function (error, element) {
            element.parent('div').append(error);
        }, submitHandler: function (form) {

            // valid
            var dataJson = $("#addModal .form textarea[name='data']").val();
            if (dataJson) {
                try {
                    $.parseJSON(dataJson);
                } catch (e) {
                    layer.open({
                        icon: '2', content: "注册信息格式非法，限制为字符串数组JSON格式，如 [address,address2] <br>" + e
                    });
                    return;
                }
            }

            // post
            $.post(base_url + "/registry/add", $("#addModal .form").serialize(), function (data, status) {
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
                        title: "系统提示", btn: ["确认"], content: (data.msg || "操作失败"), icon: '2'
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

// registry_update
    $("#data_list").on('click', '.registry_update', function () {
        var id = $(this).parent('p').attr("id");
        var row = tableData['provider' + id];

        $("#updateModal .form input[name='id']").val(id);
        $("#updateModal .form input[name='proxy']").val(row.proxy);
        $("#updateModal .form input[name='provider']").val(row.provider);
        $("#updateModal .form input[name='version']").val(row.version);
        $("#updateModal .form textarea[name='data']").val(row.data);

        $('#updateModal').modal({backdrop: false, keyboard: false}).modal('show');
    });
    var updateModalValidate = $("#updateModal .form").validate({
        errorElement: 'span', errorClass: 'help-block', focusInvalid: true, rules: {
            proxy: {
                required: true, rangelength: [4, 255]
            }, key: {
                required: true, rangelength: [4, 255]
            }, version: {
                required: true, rangelength: [2, 255]
            },
        }, messages: {
            proxy: {
                required: '请输入', rangelength: '长度限制为[4~255]'
            }, key: {
                required: '请输入', rangelength: '长度限制为[4~255]'
            }, version: {
                required: '请输入', rangelength: '长度限制为[2~255]'
            }
        }, highlight: function (element) {
            $(element).closest('.form-group').addClass('has-error');
        }, success: function (label) {
            label.closest('.form-group').removeClass('has-error');
            label.remove();
        }, errorPlacement: function (error, element) {
            element.parent('div').append(error);
        }, submitHandler: function (form) {
            // valid
            var dataJson = $("#addModal .form textarea[name='data']").val();
            if (dataJson) {
                try {
                    $.parseJSON(dataJson);
                } catch (e) {
                    layer.open({
                        icon: '2', content: "注册信息格式非法，限制为字符串数组JSON格式，如 [address,address2] <br>" + e
                    });
                    return;
                }
            }

            // post
            $.post(base_url + "/registry/update", $("#updateModal .form").serialize(), function (data, status) {
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
                        title: "系统提示", btn: ["确认"], content: (data.msg || "更新失败"), icon: '2'
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


})
;


// Com Alert by Tec theme
var ComAlertTec = {
    html: function () {
        var html = '<div class="modal fade" id="ComAlertTec" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">' + '<div class="modal-dialog">' + '<div class="modal-content-tec">' + '<div class="modal-body"><div class="alert" style="color:#fff;"></div></div>' + '<div class="modal-footer">' + '<div class="text-center" >' + '<button type="button" class="btn btn-info ok" data-dismiss="modal" >确认</button>' + '</div>' + '</div>' + '</div>' + '</div>' + '</div>';
        return html;
    }, show: function (msg, callback) {
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
