<!DOCTYPE html>
<html>
<head>
    <#import "../common/common.macro.ftl" as netCommon>
    <title>配置中心</title>
    <@netCommon.commonStyle />
    <!-- DataTables -->
    <link rel="stylesheet"
          href="${request.contextPath}/static/adminlte/bower_components/datatables.net-bs/css/dataTables.bootstrap.min.css">
    <!-- daterangepicker -->
    <link rel="stylesheet"
          href="${request.contextPath}/static/adminlte/bower_components/bootstrap-daterangepicker/daterangepicker.css">
</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && cookieMap["dao-cloud_adminlte_settings"]?exists && "off" == cookieMap["dao-cloud_adminlte_settings"].value >sidebar-collapse</#if>">
<div class="wrapper">
    <!-- header -->
    <@netCommon.commonHeader />
    <!-- left -->
    <@netCommon.commonLeft "config" />

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1 style="">配置列表<small></small></h1>
        </section>

        <!-- Main content -->
        <section class="content">

            <div class="row">
                <div class="col-xs-3">
                    <input type="text" class="form-control" id="proxy" autocomplete="on" value="${topic!''}"
                           placeholder="请输入proxy(精确匹配)">
                </div>
                <div class="col-xs-3">
                    <input type="text" class="form-control" id="key" autocomplete="on" value="${topic!''}"
                           placeholder="请输入key(精确匹配)">
                </div>
                <div class="col-xs-3">
                    <input type="text" class="form-control" id="version" autocomplete="on" value="${topic!''}"
                           placeholder="请输入version(精确匹配)">
                </div>
                <div class="col-xs-1">
                    <button class="btn btn-block btn-info" id="searchBtn">搜索</button>
                </div>
                <div class="col-xs-2">
                    <div class="btn-group">
                        <button class="btn btn-block btn-info bg-green" id="config_add">添加</button>
                    </div>
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
                                    <th name="provider">key</th>
                                    <th name="env">version</th>
                                    <th name="content">配置内容</th>
                                    <th>操作</th>
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

    <!-- 新增.模态框 -->
    <div class="modal fade" id="addModal" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title">新增配置</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal form" role="form">
                        <div class="form-group">
                            <label for="lastname" class="col-sm-3 control-label">proxy <font
                                        color="red">*</font></label>
                            <div class="col-sm-9"><input type="text" class="form-control" name="proxy" maxlength="255"
                                                         placeholder="请输入业务标识"></div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-3 control-label">key <font
                                        color="red">*</font></label>
                            <div class="col-sm-9"><input type="text" class="form-control" name="key" maxlength="255"
                                                         placeholder="请输入注册key"></div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-3 control-label">version <font
                                        color="red">*</font></label>
                            <div class="col-sm-9"><input type="text" class="form-control" name="version" maxlength="255"
                                                         placeholder="请输入环境标识"></div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-3 control-label">配置信息 <font
                                        color="red">*</font></label>
                            <div class="col-sm-9">
                                <textarea class="textarea" name="content" maxlength="60000"
                                          placeholder="请输入注册信息；限制为字符串数组JSON格式"
                                          style="width: 100%; height: 100px; font-size: 14px; line-height: 18px; border: 1px solid #dddddd; padding: 10px;"></textarea>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-offset-3 col-sm-9">
                                <button type="submit" class="btn btn-primary">保存</button>
                                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- 更新.模态框 -->
    <div class="modal fade" id="updateModal" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title">更新配置内容</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal form" role="form">
                        <div class="form-group">
                            <label for="lastname" class="col-sm-3 control-label">proxy <font
                                        color="red">*</font></label>
                            <div class="col-sm-9"><input type="text" class="form-control" name="proxy" maxlength="255"
                                                         placeholder="请输入proxy" readonly></div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-3 control-label">key <font
                                        color="red">*</font></label>
                            <div class="col-sm-9"><input type="text" class="form-control" name="key" maxlength="255"
                                                         placeholder="请输入key" readonly></div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-3 control-label">version <font
                                        color="red">*</font></label>
                            <div class="col-sm-9"><input type="text" class="form-control" name="version" maxlength="8"
                                                         placeholder="请输入version" readonly></div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-3 control-label">配置信息 <font
                                        color="red">*</font></label>
                            <div class="col-sm-9">
                                <textarea class="textarea" name="content" maxlength="10000"
                                          placeholder="请输入注册信息；限制为字符串数组JSON格式"
                                          style="width: 100%; height: 100px; font-size: 14px; line-height: 18px; border: 1px solid #dddddd; padding: 10px;"></textarea>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-offset-3 col-sm-9">
                                <button type="submit" class="btn btn-primary">更新</button>
                                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                                <input type="hidden" name="id">
                            </div>
                        </div>
                    </form>
                </div>
            </div>
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

<script src="${request.contextPath}/static/js/config.index.1.js"></script>

</body>
</html>
