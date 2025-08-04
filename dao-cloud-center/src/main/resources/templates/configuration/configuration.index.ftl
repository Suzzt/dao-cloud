<!DOCTYPE html>
<html>
<head>
    <#import "../common/common.macro.ftl" as netCommon>
    <title>配置文件管理 - dao-cloud 分布式服务中心</title>
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
    <style>
        /* 现代化页面样式 */
        .content-wrapper {
            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
            min-height: calc(100vh - 120px);
        }
        
        .content {
            padding: 30px;
        }
        
        /* 搜索表单现代化样式 */
        .dao-search-container {
            background: white;
            border-radius: 16px;
            padding: 24px;
            margin-bottom: 24px;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
            border: 1px solid rgba(255, 255, 255, 0.2);
            animation: slideInUp 0.6s ease-out;
        }
        
        .dao-search-title {
            font-size: 18px;
            font-weight: 600;
            color: var(--text-primary);
            margin-bottom: 20px;
            padding-bottom: 12px;
            border-bottom: 2px solid var(--border-color);
            background: var(--primary-gradient);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
        }
        
        .dao-search-row {
            display: grid;
            grid-template-columns: 1fr 1fr 1fr auto;
            gap: 16px;
            align-items: end;
        }
        
        .dao-form-group {
            margin-bottom: 0;
        }
        
        .dao-form-label {
            font-size: 14px;
            font-weight: 500;
            color: var(--text-secondary);
            margin-bottom: 8px;
            display: block;
        }
        
        .dao-form-control {
            width: 100%;
            padding: 12px 16px;
            border: 2px solid var(--border-color);
            border-radius: var(--radius-md);
            font-size: 14px;
            background: var(--background-light);
            transition: all 0.3s ease;
        }
        
        .dao-form-control:focus {
            outline: none;
            border-color: var(--primary-color);
            background: white;
            box-shadow: 0 0 0 3px var(--primary-light);
        }
        
        .dao-btn-group {
            display: flex;
            gap: 8px;
        }
        
        /* 表格容器现代化样式 */
        .dao-table-container {
            background: white;
            border-radius: 16px;
            padding: 24px;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
            border: 1px solid rgba(255, 255, 255, 0.2);
            animation: slideInUp 0.8s ease-out;
        }
        
        .dao-table-title {
            font-size: 18px;
            font-weight: 600;
            color: var(--text-primary);
            margin-bottom: 20px;
            padding-bottom: 12px;
            border-bottom: 2px solid var(--border-color);
            background: var(--primary-gradient);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
        }
        
        .dao-table {
            border-radius: var(--radius-md);
            overflow: hidden;
            border: 1px solid var(--border-color);
        }
        
        .dao-table thead th {
            background: var(--primary-gradient);
            color: white;
            font-weight: 600;
            padding: 16px 12px;
            text-align: center;
            border: none;
        }
        
        .dao-table tbody td {
            padding: 12px;
            border-bottom: 1px solid #f1f3f4;
            vertical-align: middle;
        }
        
        .dao-table tbody tr:hover {
            background: rgba(102, 126, 234, 0.05);
        }
        
        /* 模态框现代化样式 */
        .dao-modal .modal-content {
            border: none;
            border-radius: var(--radius-lg);
            box-shadow: var(--shadow-xl);
            animation: scaleIn 0.3s ease-out;
        }
        
        .dao-modal .modal-header {
            background: var(--primary-gradient);
            color: white;
            border-radius: var(--radius-lg) var(--radius-lg) 0 0;
            padding: 20px 24px;
            border-bottom: none;
        }
        
        .dao-modal .modal-title {
            font-weight: 600;
            font-size: 18px;
        }
        
        .dao-modal .close {
            color: white;
            opacity: 0.8;
            text-shadow: none;
            font-size: 24px;
        }
        
        .dao-modal .close:hover {
            opacity: 1;
        }
        
        .dao-modal .modal-body {
            padding: 24px;
        }
        
        .dao-modal .form-horizontal .form-group {
            margin-bottom: 20px;
        }
        
        .dao-modal .control-label {
            font-weight: 600;
            color: var(--text-primary);
            padding-top: 12px;
        }
        
        .dao-modal .form-control {
            height: 44px;
            border: 2px solid var(--border-color);
            border-radius: var(--radius-md);
            padding: 0 16px;
            font-size: 14px;
            background: var(--background-light);
            transition: all 0.3s ease;
        }
        
        .dao-modal .form-control:focus {
            border-color: var(--primary-color);
            background: white;
            box-shadow: 0 0 0 3px var(--primary-light);
        }
        
        .dao-modal .form-control[readonly] {
            background-color: #f8f9fa;
            opacity: 0.8;
        }
        
        /* 代码编辑器容器样式 */
        .editor-container {
            border-radius: var(--radius-md) !important;
            border: 2px solid var(--border-color) !important;
            box-shadow: var(--shadow-sm);
            overflow: hidden;
        }
        
        .editor-container .CodeMirror {
            height: 550px;
            font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
            font-size: 13px;
            line-height: 1.5;
        }
        
        /* 按钮组样式 */
        .dao-modal .form-group:last-child {
            margin-top: 30px;
            padding-top: 20px;
            border-top: 1px solid var(--border-color);
        }
        
        /* 响应式设计 */
        @media (max-width: 768px) {
            .dao-search-row {
                grid-template-columns: 1fr;
                gap: 16px;
            }
            
            .dao-btn-group {
                justify-content: stretch;
            }
            
            .dao-btn-group .dao-btn {
                flex: 1;
            }
            
            .content {
                padding: 20px 15px;
            }
            
            .dao-search-container,
            .dao-table-container {
                padding: 20px;
            }
        }
        
        @media (max-width: 992px) {
            .dao-search-row {
                grid-template-columns: 1fr 1fr;
                gap: 16px;
            }
            
            .dao-search-row > div:last-child {
                grid-column: 1 / -1;
                justify-self: center;
            }
        }
        
        /* 操作按钮样式 */
        .dao-action-btn {
            padding: 6px 12px;
            font-size: 12px;
            border-radius: var(--radius-sm);
            min-height: auto;
            transition: all 0.2s ease;
        }
        
        .dao-action-btn:hover {
            transform: translateY(-1px);
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
        }
    </style>
</head>
<body class="hold-transition skin-blue sidebar-mini dao-page-enter <#if cookieMap?exists && cookieMap["dao-cloud_adminlte_settings"]?exists && "off" == cookieMap["dao-cloud_adminlte_settings"].value >sidebar-collapse</#if>">
<div class="wrapper">
    <!-- header -->
    <@netCommon.commonHeader />
    <!-- left -->
    <@netCommon.commonLeft "configuration" />

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Main content -->
        <section class="content">
            <!-- 搜索表单 -->
            <div class="dao-search-container">
                <div class="dao-search-title">
                    <i class="fa fa-search"></i> 配置文件查询
                </div>
                <div class="dao-search-row">
                    <div class="dao-form-group">
                        <label class="dao-form-label">服务代理</label>
                        <input type="text" class="dao-form-control" id="proxy" autocomplete="on" value="${topic!''}"
                               placeholder="请输入proxy (模糊匹配)">
                    </div>
                    <div class="dao-form-group">
                        <label class="dao-form-label">分组标识</label>
                        <input type="text" class="dao-form-control" id="groupId" autocomplete="on" value="${topic!''}"
                               placeholder="请输入groupId (模糊匹配)">
                    </div>
                    <div class="dao-form-group">
                        <label class="dao-form-label">文件名称</label>
                        <input type="text" class="dao-form-control" id="fileName" autocomplete="on" value="${topic!''}"
                               placeholder="请输入fileName (模糊匹配)">
                    </div>
                    <div class="dao-btn-group">
                        <button class="dao-btn dao-btn-primary" id="searchBtn">
                            <i class="fa fa-search"></i> 搜索
                        </button>
                        <button class="dao-btn dao-btn-success" id="configuration_add">
                            <i class="fa fa-plus"></i> 添加配置
                        </button>
                    </div>
                </div>
            </div>

            <!-- 数据表格 -->
            <div class="dao-table-container">
                <div class="dao-table-title">
                    <i class="fa fa-list"></i> 配置文件列表
                </div>
                <table id="data_list" class="dao-table table table-bordered table-striped" width="100%">
                    <thead>
                    <tr>
                        <th name="proxy">服务代理</th>
                        <th name="groupId">分组标识</th>
                        <th name="fileName">文件名称</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody></tbody>
                    <tfoot></tfoot>
                </table>
            </div>
        </section>
    </div>

    <!-- 新增配置模态框 -->
    <div class="modal fade dao-modal" id="addModal" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog modal-lg" style="max-width: 95%;">
            <div class="modal-content" style="min-height: 80vh;">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title">
                        <i class="fa fa-plus-circle"></i> 新增配置文件
                    </h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal form" role="form">
                        <div class="form-group">
                            <label class="col-sm-3 control-label">服务代理 <span style="color: #e74c3c;">*</span></label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="proxy" maxlength="255"
                                       placeholder="请输入服务代理名称">
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label">分组标识 <span style="color: #e74c3c;">*</span></label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="groupId" maxlength="255"
                                       placeholder="请输入分组标识">
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label">文件名称 <span style="color: #e74c3c;">*</span></label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="fileName" maxlength="255"
                                       placeholder="请输入配置文件名称">
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label">文件类型 <span style="color: #e74c3c;">*</span></label>
                            <div class="col-sm-9">
                                <select class="form-control" id="fileType">
                                    <option value=".yaml">YAML 格式</option>
                                    <option value=".properties">Properties 格式</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label">配置内容 <span style="color: #e74c3c;">*</span></label>
                            <div class="col-sm-9">
                                <div class="editor-container"></div>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-offset-3 col-sm-9">
                                <button type="submit" class="dao-btn dao-btn-primary">
                                    <i class="fa fa-save"></i> 保存配置
                                </button>
                                <button type="button" class="dao-btn dao-btn-secondary" data-dismiss="modal">
                                    <i class="fa fa-times"></i> 取消
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- 更新配置模态框 -->
    <div class="modal fade dao-modal" id="updateModal" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog modal-lg" style="max-width: 95%;">
            <div class="modal-content" style="min-height: 80vh;">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title">
                        <i class="fa fa-edit"></i> 更新配置内容
                    </h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal form" role="form">
                        <div class="form-group">
                            <label class="col-sm-3 control-label">服务代理</label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="proxy" maxlength="255" readonly>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label">分组标识</label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="groupId" maxlength="255" readonly>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label">文件名称</label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="fileName" maxlength="255" readonly>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label">文件类型</label>
                            <div class="col-sm-9">
                                <select class="form-control" id="fileType" disabled>
                                    <option value=".yaml">YAML 格式</option>
                                    <option value=".properties">Properties 格式</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label">配置内容 <span style="color: #e74c3c;">*</span></label>
                            <div class="col-sm-9">
                                <div class="editor-container"></div>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-offset-3 col-sm-9">
                                <button type="submit" class="dao-btn dao-btn-primary">
                                    <i class="fa fa-save"></i> 更新配置
                                </button>
                                <button type="button" class="dao-btn dao-btn-secondary" data-dismiss="modal">
                                    <i class="fa fa-times"></i> 取消
                                </button>
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
        // 现代化提示框
        tippy('#searchBtn', {
            content: '查询结果为多条件交集匹配',
            placement: 'top',
            animation: 'scale',
            theme: 'light-border',
            arrow: true,
            delay: [300, 100],
        });
        
        // 为表格添加现代化样式
        $('#data_list').on('draw.dt', function() {
            // 为操作按钮添加现代化样式
            $(this).find('.btn').each(function() {
                $(this).addClass('dao-action-btn');
                if ($(this).hasClass('btn-primary')) {
                    $(this).removeClass('btn-primary').addClass('dao-btn-primary');
                }
                if ($(this).hasClass('btn-warning')) {
                    $(this).removeClass('btn-warning').addClass('dao-btn-warning');
                }
                if ($(this).hasClass('btn-danger')) {
                    $(this).removeClass('btn-danger').addClass('dao-btn-danger');
                }
            });
        });
        
        // 搜索输入框回车支持
        $('.dao-form-control').keypress(function(e) {
            if (e.which === 13) {
                $('#searchBtn').click();
            }
        });
        
        // 添加加载状态支持
        function showLoading(element) {
            var loadingHtml = '<i class="fa fa-spinner fa-spin"></i> 处理中...';
            element.data('original-text', element.html());
            element.html(loadingHtml).prop('disabled', true);
        }
        
        function hideLoading(element) {
            element.html(element.data('original-text')).prop('disabled', false);
        }
        
        // 为按钮添加加载状态
        $('#searchBtn, #configuration_add').click(function() {
            var btn = $(this);
            showLoading(btn);
            setTimeout(function() {
                hideLoading(btn);
            }, 1000);
        });
        
        // 表单提交按钮加载状态
        $('.dao-modal form').submit(function() {
            var submitBtn = $(this).find('button[type="submit"]');
            showLoading(submitBtn);
        });
        
        // 模态框关闭时重置按钮状态
        $('.dao-modal').on('hidden.bs.modal', function() {
            $(this).find('button[type="submit"]').each(function() {
                if ($(this).data('original-text')) {
                    hideLoading($(this));
                }
            });
        });
        
        // 页面加载动画
        $('.dao-search-container, .dao-table-container').each(function(index) {
            $(this).css({
                'animation-delay': (index * 0.1) + 's',
                'animation-fill-mode': 'both'
            });
        });
    });
</script>
</body>
</html>