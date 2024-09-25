<!DOCTYPE html>
<html>
<head>
    <#import "common/common.macro.ftl" as netCommon>
    <title>指标概况</title>
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
        <section class="content">
            <div class="row">
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

                <div class="col-md-12 col-sm-12 col-xs-12">
                    <div class="box box-primary">
                        <div class="box-body">
                            <div id="barChart" style="height:400px;"></div>
                        </div>
                    </div>
                </div>

                <div class="col-md-12 col-sm-12 col-xs-12">
                    <div class="box box-success">
                        <div class="box-body">
                            <div id="lineChart" style="height:400px;"></div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </div>
    <@netCommon.commonFooter />
</div>
<@netCommon.commonScript />
<script src="${request.contextPath}/static/adminlte/bower_components/moment/moment.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/bootstrap-daterangepicker/daterangepicker.js"></script>
<script src="${request.contextPath}/static/plugins/echarts/echarts.common.min.js"></script>
<script src="${request.contextPath}/static/js/index.js"></script>
<script>
    var proxyDimensionStatistics = ${proxyDimensionStatistics};
    if (proxyDimensionStatistics == null || proxyDimensionStatistics.length === 0) {
        document.getElementById('barChart').closest('.col-md-12').style.display = 'none';
    } else {
        // 柱状图配置
        var barChart = echarts.init(document.getElementById('barChart'));
        var barOption = {
            title: {
                text: '服务节点个数'
            },
            tooltip: {},
            xAxis: {
                type: 'category',
                data: ${proxyDimensionStatistics}
            },
            yAxis: {
                type: 'value'
            },
            series: [{
                name: '节点个数',
                type: 'bar',
                data: ${proxyMeasureStatistics},
                barMaxWidth: '50%',
                itemStyle: {
                    color: '#337ab7'
                }
            }],
            grid: {
                containLabel: true,
                left: '3%',
                right: '4%',
                bottom: '3%',
                top: '15%'
            }
        };
        barChart.setOption(barOption);
    }

    // 折线图配置
    var lineChart = echarts.init(document.getElementById('lineChart'));
    var lineOption = {
        title: {
            text: '预留！看看这里后面可以放什么？'
        },
        tooltip: {
            trigger: 'axis'
        },
        legend: {
            data: ['proxy1', 'proxy2', 'proxy3']
        },
        xAxis: {
            type: 'category',
            boundaryGap: false,
            data: ['08:00', '08:05', '08:10', '08:15', '08:20', '08:25', '08:30', '08:35', '08:40', '08:45', '08:50', '08:55']
        },
        yAxis: {
            type: 'value'
        },
        series: [
            {
                name: 'proxy1',
                type: 'line',
                data: [12, 15, 11, 10, 13, 12, 6, 15, 13, 11, 12, 14],
                smooth: true,
                lineStyle: {
                    color: '#FF5733'
                }
            },
            {
                name: 'proxy2',
                type: 'line',
                data: [8, 9, 10, 11, 12, 9, 8, 7, 10, 9, 8, 9],
                smooth: true,
                lineStyle: {
                    color: '#33FF57'
                }
            },
            {
                name: 'proxy3',
                type: 'line',
                data: [5, 6, 4, 6, 7, 5, 4, 5, 7, 6, 5, 6], // 示例数据
                smooth: true,
                lineStyle: {
                    color: '#3357FF'
                }
            }
        ],
        grid: {
            containLabel: true,
            left: '3%',
            right: '4%',
            bottom: '3%',
            top: '15%'
        }
    };
    lineChart.setOption(lineOption);

    setTimeout(function () {
        window.location.reload(1);
    }, 10 * 1000);
</script>
</body>
</html>
