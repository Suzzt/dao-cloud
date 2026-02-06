<!DOCTYPE html>
<html>
<head>
    <#import "common/common.macro.ftl" as netCommon>
    <title>指标概况 - dao-cloud 分布式服务中心</title>
    <@netCommon.commonStyle />
    <!-- daterangepicker -->
    <link rel="stylesheet"
          href="${request.contextPath}/static/adminlte/bower_components/bootstrap-daterangepicker/daterangepicker.css">
</head>
<body class="hold-transition skin-blue sidebar-mini dao-page-enter <#if cookieMap?exists && cookieMap["dao-cloud_adminlte_settings"]?exists && "off" == cookieMap["dao-cloud_adminlte_settings"].value >sidebar-collapse</#if> ">
<div class="wrapper">
    <!-- header -->
    <@netCommon.commonHeader />
    <!-- left -->
    <@netCommon.commonLeft "index" />

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <section class="content">
            <!-- 统计卡片网格 -->
            <div class="dao-stats-grid">
                <div class="dao-stat-card">
                    <div class="dao-stat-header">
                        <div class="dao-stat-icon aqua">
                            <i class="fa fa-server"></i>
                        </div>
                        <div class="dao-stat-content">
                            <div class="dao-stat-title">center集群节点数</div>
                            <div class="dao-stat-value">${aliveClusterNodeNum}</div>
                            <div class="dao-stat-description">当前节点接入且存活center集群节点数量</div>
                        </div>
                    </div>
                </div>
                
                <div class="dao-stat-card">
                    <div class="dao-stat-header">
                        <div class="dao-stat-icon teal">
                            <i class="fa fa-network-wired"></i>
                        </div>
                        <div class="dao-stat-content">
                            <div class="dao-stat-title">网关集群节点数</div>
                            <div class="dao-stat-value">${gatewayNodeNum}</div>
                            <div class="dao-stat-description">整套网关集群节点数量</div>
                        </div>
                    </div>
                </div>
                
                <div class="dao-stat-card">
                    <div class="dao-stat-header">
                        <div class="dao-stat-icon green">
                            <i class="fa fa-cubes"></i>
                        </div>
                        <div class="dao-stat-content">
                            <div class="dao-stat-title">服务提供者数量</div>
                            <div class="dao-stat-value">${providerNum}</div>
                            <div class="dao-stat-description">服务中心接入的服务提供者数量</div>
                        </div>
                    </div>
                </div>
                
                <div class="dao-stat-card">
                    <div class="dao-stat-header">
                        <div class="dao-stat-icon yellow">
                            <i class="fa fa-code"></i>
                        </div>
                        <div class="dao-stat-content">
                            <div class="dao-stat-title">服务方法数量</div>
                            <div class="dao-stat-value">${methodNum}</div>
                            <div class="dao-stat-description">服务中心接入的注册方法可调用</div>
                        </div>
                    </div>
                </div>
                
                <div class="dao-stat-card">
                    <div class="dao-stat-header">
                        <div class="dao-stat-icon blue">
                            <i class="fa fa-file-code-o"></i>
                        </div>
                        <div class="dao-stat-content">
                            <div class="dao-stat-title">配置总数</div>
                            <div class="dao-stat-value">${configurationNum}+${configNum}</div>
                            <div class="dao-stat-description">配置文件+配置订阅总数量</div>
                        </div>
                    </div>
                </div>
                
                <div class="dao-stat-card">
                    <div class="dao-stat-header">
                        <div class="dao-stat-icon lime">
                            <i class="fa fa-users"></i>
                        </div>
                        <div class="dao-stat-content">
                            <div class="dao-stat-title">配置订阅数</div>
                            <div class="dao-stat-value">${configSubscribeNum}</div>
                            <div class="dao-stat-description">每个配置服务订阅数的总和数(不去重)</div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 服务节点柱状图 -->
            <div class="dao-chart-container">
                <div class="dao-chart-title">
                    <i class="fa fa-bar-chart"></i> 服务节点分布统计
                </div>
                <div id="barChart" style="height:400px;"></div>
            </div>

            <!-- 预留图表区域 -->
            <div class="dao-chart-container">
                <div class="dao-chart-title">
                    <i class="fa fa-line-chart"></i> 系统监控趋势
                </div>
                <div id="lineChart" style="height:400px;"></div>
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
        document.getElementById('barChart').closest('.dao-chart-container').style.display = 'none';
    } else {
        // 现代化柱状图配置
        var barChart = echarts.init(document.getElementById('barChart'));
        var barOption = {
            title: {
                show: false
            },
            tooltip: {
                trigger: 'axis',
                backgroundColor: 'rgba(255, 255, 255, 0.95)',
                borderColor: '#667eea',
                borderWidth: 1,
                textStyle: {
                    color: '#333'
                },
                extraCssText: 'box-shadow: 0 4px 12px rgba(0,0,0,0.15); border-radius: 8px;'
            },
            grid: {
                left: '3%',
                right: '4%',
                bottom: '8%',
                top: '5%',
                containLabel: true
            },
            xAxis: {
                type: 'category',
                data: ${proxyDimensionStatistics},
                axisLine: {
                    lineStyle: {
                        color: '#e1e5e9'
                    }
                },
                axisTick: {
                    show: false
                },
                axisLabel: {
                    color: '#666',
                    fontSize: 12
                }
            },
            yAxis: {
                type: 'value',
                axisLine: {
                    show: false
                },
                axisTick: {
                    show: false
                },
                axisLabel: {
                    color: '#666',
                    fontSize: 12
                },
                splitLine: {
                    lineStyle: {
                        color: '#f1f3f4',
                        type: 'dashed'
                    }
                }
            },
            series: [{
                name: '节点个数',
                type: 'bar',
                data: ${proxyMeasureStatistics},
                barMaxWidth: 60,
                itemStyle: {
                    borderRadius: [4, 4, 0, 0],
                    color: {
                        type: 'linear',
                        x: 0,
                        y: 0,
                        x2: 0,
                        y2: 1,
                        colorStops: [{
                            offset: 0, color: '#667eea'
                        }, {
                            offset: 1, color: '#764ba2'
                        }]
                    }
                },
                emphasis: {
                    itemStyle: {
                        color: {
                            type: 'linear',
                            x: 0,
                            y: 0,
                            x2: 0,
                            y2: 1,
                            colorStops: [{
                                offset: 0, color: '#5a6fd8'
                            }, {
                                offset: 1, color: '#6c4298'
                            }]
                        }
                    }
                },
                animationDelay: function (idx) {
                    return idx * 100;
                }
            }],
            animationEasing: 'elasticOut',
            animationDelayUpdate: function (idx) {
                return idx * 5;
            }
        };
        barChart.setOption(barOption);
        
        // 响应式
        window.addEventListener('resize', function() {
            barChart.resize();
        });
    }

    // 现代化折线图配置
    var lineChart = echarts.init(document.getElementById('lineChart'));
    var lineOption = {
        title: {
            show: false
        },
        tooltip: {
            trigger: 'axis',
            backgroundColor: 'rgba(255, 255, 255, 0.95)',
            borderColor: '#667eea',
            borderWidth: 1,
            textStyle: {
                color: '#333'
            },
            extraCssText: 'box-shadow: 0 4px 12px rgba(0,0,0,0.15); border-radius: 8px;'
        },
        legend: {
            data: ['服务调用量', '系统负载', '响应时间'],
            top: '5%',
            textStyle: {
                color: '#666'
            },
            itemWidth: 20,
            itemHeight: 12
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '8%',
            top: '15%',
            containLabel: true
        },
        xAxis: {
            type: 'category',
            boundaryGap: false,
            data: ['08:00', '08:05', '08:10', '08:15', '08:20', '08:25', '08:30', '08:35', '08:40', '08:45', '08:50', '08:55'],
            axisLine: {
                lineStyle: {
                    color: '#e1e5e9'
                }
            },
            axisTick: {
                show: false
            },
            axisLabel: {
                color: '#666',
                fontSize: 12
            }
        },
        yAxis: {
            type: 'value',
            axisLine: {
                show: false
            },
            axisTick: {
                show: false
            },
            axisLabel: {
                color: '#666',
                fontSize: 12
            },
            splitLine: {
                lineStyle: {
                    color: '#f1f3f4',
                    type: 'dashed'
                }
            }
        },
        series: [
            {
                name: '服务调用量',
                type: 'line',
                smooth: true,
                data: [120, 150, 110, 100, 130, 120, 160, 150, 130, 110, 120, 140],
                lineStyle: {
                    width: 3,
                    color: {
                        type: 'linear',
                        x: 0,
                        y: 0,
                        x2: 1,
                        y2: 0,
                        colorStops: [{
                            offset: 0, color: '#667eea'
                        }, {
                            offset: 1, color: '#764ba2'
                        }]
                    }
                },
                areaStyle: {
                    color: {
                        type: 'linear',
                        x: 0,
                        y: 0,
                        x2: 0,
                        y2: 1,
                        colorStops: [{
                            offset: 0, color: 'rgba(102, 126, 234, 0.3)'
                        }, {
                            offset: 1, color: 'rgba(102, 126, 234, 0.05)'
                        }]
                    }
                },
                symbol: 'circle',
                symbolSize: 6,
                itemStyle: {
                    color: '#667eea',
                    borderColor: '#fff',
                    borderWidth: 2
                }
            },
            {
                name: '系统负载',
                type: 'line',
                smooth: true,
                data: [80, 90, 100, 110, 120, 90, 80, 70, 100, 90, 80, 90],
                lineStyle: {
                    width: 3,
                    color: {
                        type: 'linear',
                        x: 0,
                        y: 0,
                        x2: 1,
                        y2: 0,
                        colorSteps: [{
                            offset: 0, color: '#28a745'
                        }, {
                            offset: 1, color: '#20c997'
                        }]
                    }
                },
                symbol: 'circle',
                symbolSize: 6,
                itemStyle: {
                    color: '#28a745',
                    borderColor: '#fff',
                    borderWidth: 2
                }
            },
            {
                name: '响应时间',
                type: 'line',
                smooth: true,
                data: [50, 60, 40, 60, 70, 50, 40, 50, 70, 60, 50, 60],
                lineStyle: {
                    width: 3,
                    color: {
                        type: 'linear',
                        x: 0,
                        y: 0,
                        x2: 1,
                        y2: 0,
                        colorStops: [{
                            offset: 0, color: '#ffc107'
                        }, {
                            offset: 1, color: '#fd7e14'
                        }]
                    }
                },
                symbol: 'circle',
                symbolSize: 6,
                itemStyle: {
                    color: '#ffc107',
                    borderColor: '#fff',
                    borderWidth: 2
                }
            }
        ],
        animationEasing: 'cubicOut',
        animationDuration: 1000,
        animationDelayUpdate: function (idx) {
            return idx * 50;
        }
    };
    lineChart.setOption(lineOption);
    
    // 响应式
    window.addEventListener('resize', function() {
        lineChart.resize();
    });

    // 自动刷新机制优化 - 增加到30秒，减少频繁刷新
    setTimeout(function () {
        window.location.reload(1);
    }, 30 * 1000);
</script>
</body>
</html>
