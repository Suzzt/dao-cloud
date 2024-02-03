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
                //
                return '<a href="javascript:;" class="limitModel">设置</a>';
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

    $("#data_list").on('click', '.limitModel', function () {
        $('#limitWindow').modal({backdrop: false, keyboard: false}).modal('show');
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
