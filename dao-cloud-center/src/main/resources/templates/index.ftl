<!DOCTYPE html>
<html>
<head>
    <#import "common/common.macro.ftl" as netCommon>
    <title>dao-cloud</title>
    <@netCommon.commonStyle />
    <!-- daterangepicker -->
    <link rel="stylesheet"
          href="${request.contextPath}/static/adminlte/bower_components/bootstrap-daterangepicker/daterangepicker.css">
</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && cookieMap["dao-cloud_adminlte_settings"]?exists && "off" == cookieMap["dao-cloud_adminlte_settings"].value >sidebar-collapse</#if> ">
<div class="wrapper">
    <!-- header -->
    <@netCommon.commonHeader />
    <!-- left -->
    <@netCommon.commonLeft "index" />

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <#--        <!-- Content Header (Page header) &ndash;&gt;-->
        <#--        <section class="content-header">-->
        <#--            <h1>系统报表</h1>-->
        <#--        </section>-->

        <!-- Main content -->
        <section class="content">

            <!-- 报表导航 -->
            <div class="row">

                <#-- center集群节点数 -->
                <div class="col-md-6 col-sm-6 col-xs-12">
                    <div class="info-box bg-aqua">
                        <span class="info-box-icon"><i class="fa fa-navicon"></i></span>
                        <div class="info-box-content">
                            <span class="info-box-text">center集群节点数</span>
                            <span class="info-box-number">${aliveClusterNodeNum}</span>
                            <div class="progress">
                                <div class="progress-bar" style="width: 100%"></div>
                            </div>
                            <span class="progress-description">当前节点接入且存活center集群节点数量</span>
                        </div>
                    </div>
                </div>

                <#-- 网关集群节点数 -->
                <div class="col-md-6 col-sm-6 col-xs-12">
                    <div class="info-box bg-teal">
                        <span class="info-box-icon"><i class="fa fa-hourglass-half"></i></span>
                        <div class="info-box-content">
                            <span class="info-box-text">网关集群节点数</span>
                            <span class="info-box-number">${gatewayNodeNum}</span>
                            <div class="progress">
                                <div class="progress-bar" style="width: 100%"></div>
                            </div>
                            <span class="progress-description">整套网关集群节点数量</span>
                        </div>
                    </div>
                </div>

                <#-- 服务提供者数量 -->
                <div class="col-md-6 col-sm-6 col-xs-12">
                    <div class="info-box bg-green">
                        <span class="info-box-icon"><i class="fa fa-cab"></i></span>
                        <div class="info-box-content">
                            <span class="info-box-text">服务提供者数量</span>
                            <span class="info-box-number">${providerNum}</span>
                            <div class="progress">
                                <div class="progress-bar" style="width: 100%"></div>
                            </div>
                            <span class="progress-description">服务中心接入的服务提供者数量</span>
                        </div>
                    </div>
                </div>

                <#-- 服务提供者方法数量 -->
                <div class="col-md-6 col-sm-6 col-xs-12">
                    <div class="info-box bg-yellow">
                        <span class="info-box-icon"><i class="fa fa-flag-o"></i></span>
                        <div class="info-box-content">
                            <span class="info-box-text">服务方法数量</span>
                            <span class="info-box-number">${methodNum}</span>
                            <div class="progress">
                                <div class="progress-bar" style="width: 100%"></div>
                            </div>
                            <span class="progress-description">服务中心接入的注册方法可调用 </span>
                        </div>
                    </div>
                </div>

                <#-- 配置数量 -->
                <div class="col-md-6 col-sm-6 col-xs-12">
                    <div class="info-box bg-light-blue">
                        <span class="info-box-icon"><i class="fa fa-calendar"></i></span>
                        <div class="info-box-content">
                            <span class="info-box-text">配置总数</span>
                            <span class="info-box-number">${configNum}</span>
                            <div class="progress">
                                <div class="progress-bar" style="width: 100%"></div>
                            </div>
                            <span class="progress-description">配置中心保存配置总数量 </span>
                        </div>
                    </div>
                </div>

                <#-- 配置订阅 -->
                <div class="col-md-6 col-sm-6 col-xs-12">
                    <div class="info-box bg-lime-active">
                        <span class="info-box-icon"><i class="fa fa-umbrella"></i></span>
                        <div class="info-box-content">
                            <span class="info-box-text">配置订阅数</span>
                            <span class="info-box-number">${configSubscribeNum}</span>
                            <div class="progress">
                                <div class="progress-bar" style="width: 100%"></div>
                            </div>
                            <span class="progress-description">每个配置服务订阅数的总和数(不去重) </span>
                        </div>
                    </div>
                </div>
            </div>
        </section>
        <!-- /.content -->
    </div>
    <!-- footer -->
    <@netCommon.commonFooter />
</div>
<@netCommon.commonScript />
<!-- daterangepicker -->
<script src="${request.contextPath}/static/adminlte/bower_components/moment/moment.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/bootstrap-daterangepicker/daterangepicker.js"></script>
<#-- echarts -->
<script src="${request.contextPath}/static/plugins/echarts/echarts.common.min.js"></script>
<script src="${request.contextPath}/static/js/index.js"></script>
</body>
</html>
