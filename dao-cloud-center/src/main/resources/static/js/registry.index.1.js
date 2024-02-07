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
        }, "searching": false, "ordering": false,
        "columns": [{data: 'proxy'}, {data: 'provider'}, {data: 'version'}, {
            data: 'number', ordering: true, render: function (data, type, row) {
                if (data != 0) {
                    return '<a href="javascript:;" class="showData" proxy="' + row.proxy + '" provider="' + row.provider + '" version="' + row.version + '">' + data + '</a>';
                } else {
                    return '0';
                }
            }
        }, {
            data: 'limit', ordering: true, render: function (data, type, row) {
                // 网关就直接跳过
                if (row.proxy == "dao-cloud-gateway" && row.provider == "gateway") {
                    return '';
                }

                if (row.limit == null) {
                    return '<a href="javascript:;" class="openLimitModelWindow" proxy="' + row.proxy + '" provider="' + row.provider + '" version="' + row.version + '">设置</a>'
                }

                var limitAlgorithm;
                if (row.limit.limitAlgorithm == 1) {
                    limitAlgorithm = '计数'
                } else if (row.limit.limitAlgorithm == 2) {
                    limitAlgorithm = '令牌'
                } else {
                    limitAlgorithm = '漏桶'
                }
                var limitNumber = row.limit.limitNumber;
                return '<div>' +
                    limitAlgorithm + '&nbsp;&nbsp;[' + limitNumber + ']&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' +
                    '<a href="javascript:;" class="openLimitModelWindow" proxy="' + row.proxy + '" provider="' + row.provider + '" version="' + row.version + '" limitAlgorithm="' + row.limit.limitAlgorithm + '" limitNumber="' + limitNumber + '">设置</a>&nbsp;&nbsp;' +
                    '<a href="javascript:;" class="clearLimit" proxy="' + row.proxy + '" provider="' + row.provider + '" version="' + row.version + '">清空</a>' +
                    '</div>';
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

    $("#data_list").on('click', '.openLimitModelWindow', function () {
        var limitNumber = $(this).attr("limitNumber");
        var limitAlgorithm = $(this).attr("limitAlgorithm");
        var proxy = $(this).attr("proxy");
        var provider = $(this).attr("provider");
        var version = $(this).attr("version");
        $("#openLimitModelWindow .form input[name='proxy']").val(proxy);
        $("#openLimitModelWindow .form input[name='key']").val(provider);
        $("#openLimitModelWindow .form input[name='version']").val(version);
        if (limitNumber != null && limitAlgorithm != null) {
            // update
            $("#openLimitModelWindow .form select[name='limitAlgorithm']").val(limitAlgorithm);
            $("#openLimitModelWindow .form input[name='limitNumber']").val(limitNumber);
        }
        $('#openLimitModelWindow').modal({backdrop: false, keyboard: false}).modal('show');
    });

    $("#openLimitModelWindow").on('hide.bs.modal', function () {
        $("#openLimitModelWindow .form")[0].reset();
        openLimitModelWindowValidate.resetForm();
        $("#openLimitModelWindow .form .form-group").removeClass("has-error");
    });

    var openLimitModelWindowValidate = $("#openLimitModelWindow .form").validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: true,
        rules: {},
        messages: {},
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
            $.post(base_url + "/gateway/limit_save", $("#openLimitModelWindow .form").serialize(), function (data, status) {
                if (data.code == "00000") {
                    $('#openLimitModelWindow').modal('hide');
                    layer.open({
                        title: "系统提示",
                        btn: ["确认"],
                        content: "网关限流设置成功",
                        icon: '1',
                        end: function (layero, index) {
                            dataTable.api().ajax.reload();
                        }
                    });
                } else {
                    layer.open({
                        title: "系统提示",
                        btn: ["确认"],
                        content: (data.msg || "网关限流设置失败"),
                        icon: '2'
                    });
                }
            });
        }
    });

    $("#data_list").on('click', '.clearLimit', function () {
        var proxy = $(this).attr("proxy");
        var provider = $(this).attr("provider");
        var version = $(this).attr("version");
        layer.confirm("确认清空该设置?", {
            icon: 3,
            title: "系统提示",
            btn: ["确认", "取消"]
        }, function (index) {
            layer.close(index);

            $.ajax({
                type: 'POST',
                url: base_url + "/gateway/limit_clear",
                data: {
                    "proxy": proxy,
                    "provider": provider,
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
