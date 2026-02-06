<!DOCTYPE html>
<html>
<head>
    <#import "../common/common.macro.ftl" as netCommon>
    <title>日志中心</title>
    <@netCommon.commonStyle />
    <!-- DataTables -->
    <link rel="stylesheet"
          href="${request.contextPath}/static/adminlte/bower_components/datatables.net-bs/css/dataTables.bootstrap.min.css">
    <!-- daterangepicker -->
    <link rel="stylesheet"
          href="${request.contextPath}/static/adminlte/bower_components/bootstrap-daterangepicker/daterangepicker.css">
    <script src="https://unpkg.com/@popperjs/core@2"></script>
    <script src="https://unpkg.com/tippy.js@6"></script>
    <link rel="stylesheet" href="https://unpkg.com/tippy.js@6/dist/tippy.css" />
</head>
<body class="hold-transition skin-blue sidebar-mini dao-page-enter dao-layout-full dao-page-workspace <#if cookieMap?exists && cookieMap["dao-cloud_adminlte_settings"]?exists && "off" == cookieMap["dao-cloud_adminlte_settings"].value >sidebar-collapse</#if>">
<div class="wrapper">
    <!-- header -->
    <@netCommon.commonHeader />
    <!-- left -->
    <@netCommon.commonLeft "log" />

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Main content -->
        <section class="content">

            <!-- 搜索表单 -->
            <div class="dao-search-form">
                <div class="dao-chart-title">
                    <i class="fa fa-search"></i> 日志追踪查询
                </div>
                <div class="dao-search-grid-trace">
                    <div class="dao-form-group">
                        <label class="dao-form-label">Trace ID</label>
                        <input type="text" class="dao-form-control" id="traceId" autocomplete="on"
                               value="${topic!''}" placeholder="请输入traceId进行追踪查询">
                    </div>
                    <div>
                        <button class="dao-btn dao-btn-primary" id="searchBtn">
                            <i class="fa fa-search"></i> 查询
                        </button>
                    </div>
                </div>
                <div class="dao-search-tip">
                    <i class="fa fa-info-circle"></i> 提示：使用 DaoCloudLogger.getTraceId() 获取追踪ID，支持回车键快速查询
                </div>
            </div>

            <!-- 数据表格 -->
            <div class="dao-table-container">
                <div class="dao-chart-title">
                    <i class="fa fa-file-text"></i> 日志详情
                </div>
                <table id="data_list" class="dao-table table table-bordered table-striped" width="100%">
                    <thead>
                    <tr>
                        <th name="ip">服务IP</th>
                        <th name="log">日志信息内容</th>
                    </tr>
                    </thead>
                    <tbody></tbody>
                    <tfoot></tfoot>
                </table>
            </div>

        </section>
    </div>
    <!-- footer -->
    <@netCommon.commonFooter />
</div>

<@netCommon.commonScript />
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/bower_components/datatables.net/js/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/datatables.net-bs/js/dataTables.bootstrap.min.js"></script>
<script src="${request.contextPath}/static/plugins/jquery/jquery.validate.min.js"></script>

<script src="${request.contextPath}/static/js/log.index.1.js"></script>
<script>
</script>
</body>
<script>
    // 监听回车键
    document.getElementById('traceId').addEventListener('keypress', function (e) {
        if (e.key === 'Enter') {
            document.getElementById('searchBtn').click();
        }
    });
</script>

</html>
