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
            <h1>服务管理<small></small></h1>
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
                                    <th name="gateway">网关设置</th>
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

    <div class="modal fade" id="openLimitModelWindow" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title">网关设置</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal form" role="form">
                        <div class="form-group">
                            <label for="lastname" class="col-sm-3 control-label">限流算法 <font
                                        color="red">*</font></label>
                            <div class="col-sm-9">
                                <select class="form-control" name="limitAlgorithm">
                                    <option value="" selected disabled>请选择限流算法</option>
                                    <option value=1>计数</option>
                                    <option value=2>令牌</option>
                                    <option value=3>漏桶</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-3 control-label">限流数量 <font
                                        color="red">*</font></label>
                            <div class="col-sm-9"><input type="number" class="form-control" name="limitNumber"
                                                         maxlength="10"
                                                         placeholder="请输入允许每秒能通过的请求数据"></div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-3 control-label">超时时间 <font
                                        color="red">*</font></label>
                            <div class="col-sm-9"><input type="timeout" class="form-control" name="timeout"
                                                         maxlength="10"
                                                         placeholder="请输入超时时间(到服务调用的时间)"></div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="proxy" style="display: none;">
                            </div>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="key" style="display: none;">
                            </div>
                            <div class="col-sm-9">
                                <input type="number" class="form-control" name="version" style="display: none;">
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-xs-offset-2 col-xs-8 col-sm-offset-4 col-sm-4 col-md-offset-3 col-md-6 col-lg-offset-4 col-lg-2">
                                <button type="submit" class="btn btn-primary btn-block">保存</button>
                            </div>
                            <div class="col-xs-2 col-sm-4 col-md-3 col-lg-2">
                                <button type="button" class="btn btn-default btn-block" data-dismiss="modal">取消
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
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
