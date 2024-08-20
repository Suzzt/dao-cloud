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
    <script src="https://unpkg.com/@popperjs/core@2"></script>
    <script src="https://unpkg.com/tippy.js@6"></script>
    <link rel="stylesheet" href="https://unpkg.com/tippy.js@6/dist/tippy.css" />
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

    #call-popup-list th, #call-popup-list td {
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
    }

    #call-popup-list th#method-column, #call-popup-list td:nth-child(1) {
        width: 80%;
    }

    #call-popup-list th#count-column, #call-popup-list td:nth-child(2) {
        width: 20%;
    }

    #call-popup {
        width: 100%;
        max-width: 600px;
    }

    #call-popup-list {
        table-layout: fixed;
        width: 100%;
    }

    .table-responsive {
        overflow-x: auto;
        -webkit-overflow-scrolling: touch;
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
                <div class="col-xs-1 pull-right">
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
                                    <th name="call">调用统计</th>
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

    <div class="modal fade" id="openGatewayConfigModelWindow" tabindex="-1" role="dialog" aria-hidden="true">
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
                                <select class="form-control" name="limitAlgorithm" id="limitAlgorithm">
                                    <option value="" selected disabled>请选择限流算法</option>
                                    <option value=1>计数</option>
                                    <option value=2>令牌</option>
                                    <option value=3>漏桶</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-3 control-label">超时时间 <font
                                        color="red">*</font></label>
                            <div class="col-sm-9"><input type="timeout" class="form-control" name="timeout"
                                                         maxlength="10"
                                                         placeholder="请输入调用超时时间(单位是秒)"></div>
                        </div>
                        <div id="countLimitOptions" style="display: none;">
                            <div class="form-group">
                                <label for="lastname" class="col-sm-3 control-label">滑动时间窗口 <font
                                            color="red">*</font></label>
                                <div class="col-sm-9"><input type="number" class="form-control"
                                                             name="slideDateWindowSize"
                                                             maxlength="10"
                                                             placeholder="请输入滑动窗口大小(单位是毫秒)"></div>
                            </div>
                            <div class="form-group">
                                <label for="lastname" class="col-sm-3 control-label">滑动窗口请求数 <font
                                            color="red">*</font></label>
                                <div class="col-sm-9"><input type="number" class="form-control"
                                                             name="slideWindowMaxRequestCount"
                                                             maxlength="10"
                                                             placeholder="请输入滑动窗口内允许的最大请求数"></div>
                            </div>
                        </div>

                        <div id="tokenLimitOptions" style="display: none;">
                            <div class="form-group">
                                <label for="lastname" class="col-sm-3 control-label">最大令牌数 <font
                                            color="red">*</font></label>
                                <div class="col-sm-9"><input type="number" class="form-control"
                                                             name="tokenBucketMaxSize"
                                                             maxlength="10"
                                                             placeholder="请输入令牌桶的最大令牌数"></div>
                            </div>
                            <div class="form-group">
                                <label for="lastname" class="col-sm-3 control-label">新增令牌数 <font
                                            color="red">*</font></label>
                                <div class="col-sm-9"><input type="number" class="form-control"
                                                             name="tokenBucketRefillRate"
                                                             maxlength="10"
                                                             placeholder="请输入每秒新增的令牌数"></div>
                            </div>
                        </div>
                        <div id="leakyLimitOptions" style="display: none;">
                            <div class="form-group">
                                <label for="lastname" class="col-sm-3 control-label">漏桶的容量 <font
                                            color="red">*</font></label>
                                <div class="col-sm-9"><input type="number" class="form-control"
                                                             name="leakyBucketCapacity"
                                                             maxlength="10"
                                                             placeholder="请输入漏桶的容量"></div>
                            </div>
                            <div class="form-group">
                                <label for="lastname" class="col-sm-3 control-label">令牌填充的速度 <font
                                            color="red">*</font></label>
                                <div class="col-sm-9"><input type="number" class="form-control"
                                                             name="leakyBucketRefillRate"
                                                             maxlength="10"
                                                             placeholder="请输入每秒令牌填充的速度"></div>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="proxy" style="display: none;">
                            </div>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="provider" style="display: none;">
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
            <table id="popup-list" class="table table-striped" >
                <thead>
                <tr>
                    <th>ip</th>
                    <th>port</th>
                    <th>负载压力</th>
                    <th>操作</th>
                </tr>
                </thead>
                <tbody></tbody>
            </table>
        </div>
    </div>
    <div id="call-popup" class="container" style="display: none;">
        <div class="table-responsive">
            <table id="call-popup-list" class="table table-striped" >
                <thead>
                <tr>
                    <th id="method-column">方法函数名</th>
                    <th id="count-column">调用次数</th>
                    <th id="count-column">操作</th>
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
<script>
    document.addEventListener('DOMContentLoaded', function() {
        const limitAlgorithmSelect = document.getElementById('limitAlgorithm');

        // 获取计数算法和令牌算法的选项区域
        const countLimitOptions = document.getElementById('countLimitOptions');
        const tokenLimitOptions = document.getElementById('tokenLimitOptions');
        const leakyLimitOptions = document.getElementById('leakyLimitOptions');

        // 监听下拉选择框的值变化
        limitAlgorithmSelect.addEventListener('change', function () {
            // 根据选择的算法，显示或隐藏选项
            switch (this.value) {
                case '1': // 计数算法
                    countLimitOptions.style.display = 'block';
                    tokenLimitOptions.style.display = 'none';
                    leakyLimitOptions.style.display = 'none';
                    break;
                case '2': // 令牌算法
                    countLimitOptions.style.display = 'none';
                    tokenLimitOptions.style.display = 'block';
                    leakyLimitOptions.style.display = 'none';
                    break;
                case '3': // 漏桶
                    countLimitOptions.style.display = 'none';
                    tokenLimitOptions.style.display = 'none';
                    leakyLimitOptions.style.display = 'block';
                    break;
                default:
                    countLimitOptions.style.display = 'none';
                    tokenLimitOptions.style.display = 'none';
                    leakyLimitOptions.style.display = 'none';
            }
        });
    });
</script>
<script>
    document.addEventListener('DOMContentLoaded', function () {
        tippy('#searchBtn', {
            content: '查询结果为交集',
            placement: 'top',
            animation: 'scale',
        });
    });
</script>

</body>
</html>
