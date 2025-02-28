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
    <!-- CodeMirror -->
    <link rel="stylesheet" href="${request.contextPath}/static/codemirror/lib/codemirror.css">
    <link rel="stylesheet" href="${request.contextPath}/static/codemirror/theme/material-darker.css">
    <script src="https://unpkg.com/@popperjs/core@2"></script>
    <script src="https://unpkg.com/tippy.js@6"></script>
    <link rel="stylesheet" href="https://unpkg.com/tippy.js@6/dist/tippy.css"/>
</head>
<style>
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
    <@netCommon.commonLeft "configuration" />

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Main content -->
        <section class="content">

            <div class="row">
                <div class="col-xs-3">
                    <input type="text" class="form-control" id="proxy" autocomplete="on" value="${topic!''}"
                           placeholder="请输入proxy(模糊匹配)">
                </div>
                <div class="col-xs-3">
                    <input type="text" class="form-control" id="groupId" autocomplete="on" value="${topic!''}"
                           placeholder="请输入groupId(模糊匹配)">
                </div>
                <div class="col-xs-3">
                    <input type="text" class="form-control" id="fileName" autocomplete="on" value="${topic!''}"
                           placeholder="请输入fileName(模糊匹配)">
                </div>
                <div class="col-xs-3" style="display: flex; justify-content: flex-end;">
                    <button class="btn btn-info" id="searchBtn" style="margin-right: 5px;"><i class="fa fa-search"></i>搜索
                    </button>
                    <div class="btn-group">
                        <button class="btn btn-info bg-green" id="configuration_add"><i class="fa fa-plus"></i>添加
                        </button>
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
                                    <th name="groupId">groupId</th>
                                    <th name="fileName">fileName</th>
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
        <div class="modal-dialog modal-lg" style="max-width: 100%;">
            <div class="modal-content" style="min-height: 80vh;">
                <div class="modal-header">
                    <h4 class="modal-title">新增配置</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal form" role="form">
                        <div class="form-group">
                            <label for="lastname" class="col-sm-3 control-label">proxy <font
                                        color="red">*</font></label>
                            <div class="col-sm-9"><input type="text" class="form-control" name="proxy" maxlength="255"
                                                         placeholder="请输入proxy"></div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-3 control-label">groupId <font
                                        color="red">*</font></label>
                            <div class="col-sm-9"><input type="text" class="form-control" name="groupId" maxlength="255"
                                                         placeholder="请输入groupId"></div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-3 control-label">fileName <font
                                        color="red">*</font></label>
                            <div class="col-sm-9"><input type="text" class="form-control" name="fileName"
                                                         maxlength="255"
                                                         placeholder="请输入fileName"></div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-3 control-label">文件类型 <font
                                        color="red">*</font></label>
                            <div class="col-sm-9">
                                <select class="form-control" id="fileType">
                                    <option value=".yaml">YAML</option>
                                    <option value=".properties">Properties</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="editor-container" style="height:550px; border:1px solid #ddd"></div>
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
        <div class="modal-dialog modal-lg" style="max-width: 90%;">
            <div class="modal-content" style="min-height: 80vh;">
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
                            <label for="lastname" class="col-sm-3 control-label">groupId <font
                                        color="red">*</font></label>
                            <div class="col-sm-9"><input type="text" class="form-control" name="groupId" maxlength="255"
                                                         placeholder="请输入groupId" readonly></div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-3 control-label">fileName <font
                                        color="red">*</font></label>
                            <div class="col-sm-9"><input type="text" class="form-control" name="fileName" maxlength="8"
                                                         placeholder="请输入fileName" readonly></div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-3 control-label">文件类型 <font
                                        color="red">*</font></label>
                            <div class="col-sm-9">
                                <select class="form-control" id="fileType">
                                    <option value=".yaml">YAML</option>
                                    <option value=".properties">Properties</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-3 control-label">配置信息 <font
                                        color="red">*</font></label>
                            <div class="col-sm-9">
                                <div class="editor-container" style="height:550px; border:1px solid #ddd"></div>
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
<script src="${request.contextPath}/static/codemirror/lib/codemirror.js"></script>
<script src="${request.contextPath}/static/codemirror/mode/properties/properties.js"></script>
<script src="${request.contextPath}/static/codemirror/mode/yaml/yaml.js"></script>
<script src="${request.contextPath}/static/js/configuration.index.1.js"></script>
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