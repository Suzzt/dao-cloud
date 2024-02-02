<!DOCTYPE html>
<html>
<head>
    <#import "../common/common.macro.ftl" as netCommon>
    <title>服务中心</title>
    <@netCommon.commonStyle />
    <!-- DataTables -->
    <link rel="stylesheet"
          href="${request.contextPath}/static/adminlte/bower_components/datatables.net-bs/css/dataTables.bootstrap.min.css">
    <!-- daterangepicker -->
    <link rel="stylesheet"
          href="${request.contextPath}/static/adminlte/bower_components/bootstrap-daterangepicker/daterangepicker.css">
</head>
<style>
    #popup {
        width: auto !important;
        max-width: calc(100% - 40px);
    }
         /* 在小屏幕上设置 10px 的下间距 */
    @media (max-width: 768px) {
        .row + .row {
            margin-top: 10px;
        }
    }
    /* 在大屏幕上设置 20px 的下间距 */
    @media (min-width: 769px) {
        .row + .row {
            margin-top: 20px;
        }
    }
</style>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && cookieMap["dao-cloud_adminlte_settings"]?exists && "off" == cookieMap["dao-cloud_adminlte_settings"].value >sidebar-collapse</#if>">
<div class="wrapper">
    <!-- header -->
    <@netCommon.commonHeader />
    <!-- left -->
    <@netCommon.commonLeft "registry" />

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <section class="content-header">
            <h1>服务列表<small></small></h1>
        </section>

        <!-- Main content -->
        <section class="content">

            <div class="row">
                <div class="col-xs-3">
                    <input type="text" class="form-control" id="proxy" autocomplete="on" value="${topic!''}"
                           placeholder="请输入proxy(精确匹配)">
                </div>
                <div class="col-xs-3">
                    <input type="text" class="form-control" id="provider" autocomplete="on" value="${topic!''}"
                           placeholder="请输入注册provider(精确匹配)">
                </div>
                <div class="col-xs-3">
                    <input type="text" class="form-control" id="version" autocomplete="on" value="${topic!''}"
                           placeholder="请输入version(精确匹配)">
                </div>
                <div class="col-xs-1">
                    <button class="btn btn-block btn-info" id="searchBtn">搜索</button>
                </div>
            </div>

            <div class="row">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-body">
                            <table id="data_list" class="table table-bordered table-striped" width="100%">
                                <thead>
                                <tr>
                                    <th name="proxy">proxy</th>
                                    <th name="provider">provider</th>
                                    <th name="env">version</th>
                                    <th name="number">注册节点数</th>
                                </tr>
                                </thead>
                                <tbody></tbody>
                                <tfoot></tfoot>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

        </section>
    </div>
    <div id="popup" class="container" style="display: none;">
        <div class="table-responsive">
            <table id="popup-list" class="table table-striped">
                <thead>
                <tr>
                    <th>ip</th>
                    <th>port</th>
                </tr>
                </thead>
                <tbody></tbody>
            </table>
        </div>
    </div>
    <!-- footer -->
    <@netCommon.commonFooter />
</div>

<@netCommon.commonScript />
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/bower_components/datatables.net/js/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/datatables.net-bs/js/dataTables.bootstrap.min.js"></script>
<script src="${request.contextPath}/static/plugins/jquery/jquery.validate.min.js"></script>

<script src="${request.contextPath}/static/js/registry.index.1.js"></script>

</body>
</html>
