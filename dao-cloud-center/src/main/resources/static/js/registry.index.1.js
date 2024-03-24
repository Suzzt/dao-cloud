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
            data: 'gateway', ordering: true, render: function (data, type, row) {
                // 网关就直接跳过
                if (row.proxy == "dao-cloud-gateway" && row.provider == "gateway") {
                    return '';
                }

                if (row.gateway == null) {
                    return '<a href="javascript:;" class="openGatewayConfigModelWindow" proxy="' + row.proxy + '" provider="' + row.provider + '" version="' + row.version + '">设置</a>'
                }

                var slideDateWindowSize = row.gateway.limitModel.slideDateWindowSize;
                var slideWindowMaxRequestCount = row.gateway.limitModel.slideWindowMaxRequestCount;
                var tokenBucketMaxSize = row.gateway.limitModel.tokenBucketMaxSize;
                var tokenBucketRefillRate = row.gateway.limitModel.tokenBucketRefillRate;
                var leakyBucketCapacity = row.gateway.limitModel.leakyBucketCapacity;
                var leakyBucketRefillRate = row.gateway.limitModel.leakyBucketRefillRate;
                var timeout = row.gateway.timeout;
                var limit_div = '';
                var limitAlgorithm_div = '';
                if (row.gateway.limitModel != null && row.gateway.limitModel.limitAlgorithm != null) {
                    var limitAlgorithm, c1, c2, a1, a2;
                    if (row.gateway.limitModel.limitAlgorithm == 1) {
                        limitAlgorithm = '计数'
                        c1 = slideDateWindowSize;
                        c2 = slideWindowMaxRequestCount;
                        a1 = "滑动时间窗口(单位是毫秒)";
                        a2 = "滑动窗口内的最大请求数";
                    } else if (row.gateway.limitModel.limitAlgorithm == 2) {
                        limitAlgorithm = '令牌'
                        c1 = tokenBucketMaxSize;
                        c2 = tokenBucketRefillRate;
                        a1 = "最大令牌数";
                        a2 = "每秒新增令牌数";
                    } else if (row.gateway.limitModel.limitAlgorithm == 3) {
                        limitAlgorithm = '漏桶'
                        c1 = leakyBucketCapacity;
                        c2 = leakyBucketRefillRate;
                        a1 = "漏桶的容量";
                        a2 = "每秒令牌填充的速度";
                    }
                    limit_div = limitAlgorithm + ':&nbsp;' +
                        '<span style="cursor: pointer;" title="' + a1 + '">(' + c1 + ')</span>&nbsp;&nbsp;' +
                        '<span style="cursor: pointer;" title="' + a2 + '">(' + c2 + ')</span>&nbsp;&nbsp;';
                    limitAlgorithm_div = '" limitAlgorithm="' + row.gateway.limitModel.limitAlgorithm;
                }

                // timeout
                var timeout_div = '';
                if (timeout != null) {
                    timeout_div = '调用超时时间:&nbsp;(' + timeout + ')</span>&nbsp;&nbsp;&nbsp;&nbsp;';
                }

                // Assignment parameters attributes
                const time_attribute = null;
                if (timeout == null) {
                    time_attributes = '<a href="javascript:;" class="openGatewayConfigModelWindow" proxy="' + row.proxy + '" provider="' + row.provider + '" version="' + row.version + limitAlgorithm_div + '" slideDateWindowSize="' + slideDateWindowSize + '"+ slideWindowMaxRequestCount="' + slideWindowMaxRequestCount + '" tokenBucketMaxSize="' + tokenBucketMaxSize + '" tokenBucketRefillRate="' + tokenBucketRefillRate + '" leakyBucketCapacity="' + leakyBucketCapacity + '" leakyBucketRefillRate="' + leakyBucketRefillRate + '">设置</a>&nbsp;&nbsp;';
                } else {
                    time_attributes = '<a href="javascript:;" class="openGatewayConfigModelWindow" proxy="' + row.proxy + '" provider="' + row.provider + '" version="' + row.version + limitAlgorithm_div + '" slideDateWindowSize="' + slideDateWindowSize + '"+ slideWindowMaxRequestCount="' + slideWindowMaxRequestCount + '" tokenBucketMaxSize="' + tokenBucketMaxSize + '" tokenBucketRefillRate="' + tokenBucketRefillRate + '" leakyBucketCapacity="' + leakyBucketCapacity + '" leakyBucketRefillRate="' + leakyBucketRefillRate + '" timeout="' + timeout + '">设置</a>&nbsp;&nbsp;';
                }

                return '<div>' +
                    limit_div +
                    timeout_div +
                    time_attributes +
                    '<a href="javascript:;" class="clear" proxy="' + row.proxy + '" provider="' + row.provider + '" version="' + row.version + '">清空</a>' +
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

    $("#data_list").on('click', '.openGatewayConfigModelWindow', function () {
        var proxy = $(this).attr("proxy");
        var provider = $(this).attr("provider");
        var version = $(this).attr("version");
        var slideDateWindowSize = $(this).attr("slideDateWindowSize");
        var slideWindowMaxRequestCount = $(this).attr("slideWindowMaxRequestCount");
        var tokenBucketMaxSize = $(this).attr("tokenBucketMaxSize");
        var tokenBucketRefillRate = $(this).attr("tokenBucketRefillRate");
        var leakyBucketCapacity = $(this).attr("leakyBucketCapacity");
        var leakyBucketRefillRate = $(this).attr("leakyBucketRefillRate");
        var limitAlgorithm = $(this).attr("limitAlgorithm");
        var timeout = $(this).attr("timeout");
        $("#openGatewayConfigModelWindow .form input[name='proxy']").val(proxy);
        $("#openGatewayConfigModelWindow .form input[name='provider']").val(provider);
        $("#openGatewayConfigModelWindow .form input[name='version']").val(version);
        $("#countLimitOptions").hide();
        $("#tokenLimitOptions").hide();
        $("#leakyLimitOptions").hide();
        if (limitAlgorithm != null) {
            // update
            switch (limitAlgorithm) {
                case '1': // 计数算法
                    $("#countLimitOptions").show();
                    break;
                case '2': // 令牌算法
                    $("#tokenLimitOptions").show();
                    break;
                case '3': // 漏桶
                    $("#leakyLimitOptions").show();
                    break;
                default:
            }
            console.log(limitAlgorithm)
            $("#openGatewayConfigModelWindow .form select[name='limitAlgorithm']").val(limitAlgorithm);
            $("#openGatewayConfigModelWindow .form input[name='slideDateWindowSize']").val(slideDateWindowSize);
            $("#openGatewayConfigModelWindow .form input[name='slideWindowMaxRequestCount']").val(slideWindowMaxRequestCount);
            $("#openGatewayConfigModelWindow .form input[name='tokenBucketMaxSize']").val(tokenBucketMaxSize);
            $("#openGatewayConfigModelWindow .form input[name='tokenBucketRefillRate']").val(tokenBucketRefillRate);
            $("#openGatewayConfigModelWindow .form input[name='leakyBucketCapacity']").val(leakyBucketCapacity);
            $("#openGatewayConfigModelWindow .form input[name='leakyBucketRefillRate']").val(leakyBucketRefillRate);
        }
        $("#openGatewayConfigModelWindow .form input[name='timeout']").val(timeout);
        $('#openGatewayConfigModelWindow').modal({backdrop: false, keyboard: false}).modal('show');
    });

    $("#openGatewayConfigModelWindow").on('hide.bs.modal', function () {
        $("#openGatewayConfigModelWindow .form")[0].reset();
        openGatewayConfigModelWindowValidate.resetForm();
        $("#openGatewayConfigModelWindow .form .form-group").removeClass("has-error");
    });


    var openGatewayConfigModelWindowValidate = $("#openGatewayConfigModelWindow .form").validate({
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
            // 创建formData对象
            var formData = {
                proxy: $(form).find('input[name="proxy"]').val(),
                provider: $(form).find('input[name="provider"]').val(),
                version: $(form).find('input[name="version"]').val(),
                timeout: $(form).find('input[name="timeout"]').val(),
                limit: {
                    limitAlgorithm: $(form).find('#limitAlgorithm').val(),
                    slideDateWindowSize: $(form).find('input[name="slideDateWindowSize"]').val(),
                    slideWindowMaxRequestCount: $(form).find('input[name="slideWindowMaxRequestCount"]').val(),
                    tokenBucketMaxSize: $(form).find('input[name="tokenBucketMaxSize"]').val(),
                    tokenBucketRefillRate: $(form).find('input[name="tokenBucketRefillRate"]').val(),
                    leakyBucketCapacity: $(form).find('input[name="leakyBucketCapacity"]').val(),
                    leakyBucketRefillRate: $(form).find('input[name="leakyBucketRefillRate"]').val()
                }
            };

            // 发送AJAX请求
            $.ajax({
                type: 'POST',
                url: base_url + "/gateway/save",
                contentType: 'application/json', // 指定发送数据的格式为JSON
                data: JSON.stringify(formData), // 将JS对象转换为JSON字符串
                dataType: 'json', // 预期服务器返回的数据类型
                success: function (data) {
                    if (data.code == "00000") {
                        $('#openGatewayConfigModelWindow').modal('hide');
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
                },
                error: function (xhr, textStatus, errorThrown) {
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

    $("#data_list").on('click', '.clear', function () {
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
                url: base_url + "/gateway/clear",
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
                            content: "清空成功",
                            icon: '1',
                            end: function (layero, index) {
                                dataTable.api().ajax.reload();
                            }
                        });
                    } else {
                        layer.open({
                            title: "系统提示",
                            btn: ["确认"],
                            content: (data.msg || "清空失败"),
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
$(document).ready(function () {
    $('[data-toggle="tooltip"]').tooltip();
});
