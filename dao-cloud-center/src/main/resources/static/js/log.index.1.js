$(function () {
    // Initialize DataTable without ajax initially
    var dataTable = $("#data_list").DataTable({
        "paging": true,
        "deferRender": true,
        "processing": true,
        "serverSide": false,
        "ajax": null,  // Do not fetch data on initialization
        "searching": false,
        "ordering": false,
        "columns": [
            {data: 'ip', className: 'text-center vertical-middle'},
            {
                data: 'log',
                render: function (data, type, row) {
                    return data
                        .replace(/\n/g, '<br>')          // Newline to <br>
                        .replace(/\t/g, '&nbsp;&nbsp;&nbsp;&nbsp;');  // Tab to spaces
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
            "sLoadingRecords": "载入中...",
            "oPaginate": {
                "sFirst": "首页",
                "sPrevious": "上页",
                "sNext": "下页",
                "sLast": "末页"
            }
        }
    });

    // Search button click handler
    $('#searchBtn').on('click', function () {
        var traceId = $('#traceId').val();
        if (traceId == null || traceId === "") {
            layer.open({
                title: "系统提示",
                btn: ["知道"],
                content: "请输入traceId值",
                icon: 2
            });
            return;
        }

        $.ajax({
            url: base_url+'/log/search',
            method: 'GET',
            data: { traceId: traceId },
            dataType: 'json',
            success: function (response) {
                if (response.success && response.data.length > 0) {
                    // Clear existing table data
                    dataTable.clear();

                    // Populate the table with the new data
                    response.data.forEach(function (logItem) {
                        dataTable.row.add({
                            ip: logItem.node || "N/A",
                            log: logItem.log
                        });
                    });

                    // Redraw the DataTable with the new data
                    dataTable.draw();
                } else {
                    layer.open({
                        title: "系统提示",
                        btn: ["知道"],
                        content: "没有找到相关的日志信息",
                        icon: 2
                    });
                    dataTable.clear().draw();
                }
            },
            error: function () {
                layer.open({
                    title: "系统提示",
                    btn: ["知道"],
                    content: "查询日志失败，请重试",
                    icon: 2
                });
            }
        });
    });
});
