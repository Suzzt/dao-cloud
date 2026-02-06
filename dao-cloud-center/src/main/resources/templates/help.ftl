<!DOCTYPE html>
<html>
<head>
    <#import "common/common.macro.ftl" as netCommon>
    <title>使用教程 - dao-cloud 分布式服务中心</title>
    <@netCommon.commonStyle />
</head>
<body class="hold-transition skin-blue sidebar-mini dao-page-enter dao-layout-full <#if cookieMap?exists && cookieMap["dao-cloud_adminlte_settings"]?exists && "off" == cookieMap["dao-cloud_adminlte_settings"].value >sidebar-collapse</#if> ">
<div class="wrapper">
    <!-- header -->
    <@netCommon.commonHeader />
    <!-- left -->
    <@netCommon.commonLeft "help" />

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Main content -->
        <section class="content">

            <!-- 项目介绍 -->
            <div class="dao-help-card">
                <div class="dao-help-header">
                    <i class="fa fa-info-circle"></i>
                    <h2>项目介绍</h2>
                </div>
                <div class="dao-help-content">
                    <p>
                        DaoCloud通过SpringBoot构建基于netty开发轻量级的微服务框架。麻雀虽小，五脏俱全（完全自研开发且完全开源，放心使用）；
                        本项目追求轻量、易接入、自定义协议、高可用、高性能、高扩展、易上手等特性；
                        致力于简化应用程序之间的RPC调用，并为应用程序提供方便、无代码入侵、稳定和高效的点对点远程微服务调用解决方案。对于开发人员来说，dao-cloud的提供了丰富的模型抽象和可扩展接口，为求一站式解决微服务带来的系统架构复杂度，包括路由、负载平衡、故障转移、性能监控、微服务治理等。
                    </p>
                </div>
            </div>

            <!-- 项目地址 -->
            <div class="dao-help-card">
                <div class="dao-help-header">
                    <i class="fa fa-github"></i>
                    <h2>项目地址</h2>
                </div>
                <div class="dao-help-content">
                    <div class="dao-link-group">
                        <a href="https://github.com/Suzzt/dao-cloud" target="_blank" class="dao-link-btn github">
                            <i class="fa fa-github"></i> GitHub
                        </a>
                        <a href="https://gitee.com/Suzzt0/dao-cloud" target="_blank" class="dao-link-btn gitee">
                            <i class="fa fa-git"></i> Gitee
                        </a>
                    </div>
                    <div class="dao-note" style="margin-top: 20px;">
                        <p>该项目中有个案例，所有的案例使用都在dao-cloud-example工程下有详细的示例</p>
                    </div>
                </div>
            </div>

            <!-- dao-cloud-center部署 -->
            <div class="dao-help-card">
                <div class="dao-help-header">
                    <i class="fa fa-rocket"></i>
                    <h2>dao-cloud-center部署</h2>
                </div>
                <div class="dao-help-content">
                    <p style="font-size: 16px; font-weight: 600; color: #667eea; margin-bottom: 24px;">在dao-cloud微服务框架中，很幸运，这注册中心与配置中心是一体的，即dao-cloud-center！</p>

                    <div class="dao-step">
                        <div class="dao-step-number">1</div>
                        <div class="dao-step-content">
                            <h4>引入依赖</h4>
                            <p>在你的SpringBoot的工程中引入 dao-cloud-center maven的pom依赖</p>
                            <pre class="dao-code-block"><code>&lt;dependency&gt;
    &lt;groupId&gt;org.dao&lt;/groupId&gt;
    &lt;artifactId&gt;dao-cloud-center&lt;/artifactId&gt;
    &lt;version&gt;1.0-SNAPSHOT&lt;/version&gt;
&lt;/dependency&gt;</code></pre>
                        </div>
                    </div>

                    <div class="dao-step">
                        <div class="dao-step-number">2</div>
                        <div class="dao-step-content">
                            <h4>启用注解</h4>
                            <p>然后用一个注解@EnableDaoCloudCenter搞定，加在SpringBoot的启动类上，这样单机版的dao-cloud-center就ok了</p>
                        </div>
                    </div>

                    <div class="dao-step">
                        <div class="dao-step-number">3</div>
                        <div class="dao-step-content">
                            <h4>集群配置</h4>
                            <p>如果你认为单机的dao-cloud-center不可靠，可以把dao-cloud-center部署成集群。在SpringBoot的yml中配置就行</p>
                            <pre class="dao-code-block"><code>dao-cloud:
  center:
    ############################ 集群方式 ############################
    # 配置第一个节点center必须是空，后续加入cluster中必须填写值。值可以是随便一个节点的ip地址
     cluster:
        ip: 192.168.31.254 # 这里填一个cluster其中一台机器的ip就行</code></pre>
                        </div>
                    </div>

                    <div class="dao-step">
                        <div class="dao-step-number">4</div>
                        <div class="dao-step-content">
                            <h4>其他配置</h4>
                            <pre class="dao-code-block"><code>dao-cloud:
  center:
    admin-web:
      # 是否要开启可视化管理页面
      dashboard: true
      # 设置登陆账号密码
      username: admin
      password: 123456
    ############################ 配置中心 ############################
    # 存储方式：file-system(本地文件)、mysql
    storage:
      way: file-system
      # 配置文件存储地址
      file-system-setting:
        path-prefix: /data/dao-cloud/config
      # 配置文件存放mysql的地址
      # mysql-setting:
      # url: 192.168.31.23
      # port: 3306
      # username: root
      # password: JunMo123
    ############################ 配置中心 ############################</code></pre>
                        </div>
                    </div>

                    <div class="dao-note">
                        <p>启动你的SpringBoot Application服务，恭喜你！dao-cloud-center搭建完成。
                        如果你打开了可视化管理页面的配置，控制台中会打印可以访问center web的页面。账号：admin 密码：123456</p>
                    </div>
                </div>
            </div>

            <!-- 服务暴露与消费、配置中心 -->
            <div class="dao-help-card">
                <div class="dao-help-header">
                    <i class="fa fa-code"></i>
                    <h2>服务暴露与消费、配置中心</h2>
                </div>
                <div class="dao-help-content">
                    <div class="dao-step">
                        <div class="dao-step-number">1</div>
                        <div class="dao-step-content">
                            <h4>引入依赖</h4>
                            <p>在你的SpringBoot的工程中引入 dao-cloud-spring-boot-starter maven的pom依赖，这样你就拥有了暴露与消费的能力了</p>
                            <pre class="dao-code-block"><code>&lt;dependency&gt;
    &lt;groupId&gt;org.dao&lt;/groupId&gt;
    &lt;artifactId&gt;dao-cloud-spring-boot-starter&lt;/artifactId&gt;
    &lt;version&gt;1.0-SNAPSHOT&lt;/version&gt;
&lt;/dependency&gt;</code></pre>
                        </div>
                    </div>

                    <div class="dao-step">
                        <div class="dao-step-number">2</div>
                        <div class="dao-step-content">
                            <h4>暴露你的服务接口(@DaoService)</h4>
                            <pre class="dao-code-block"><code>每个provider一定要设置自己的proxy名字! 确定唯一接口: proxy+provider+version
@DaoService 参数===provider:暴露服务的provider名称, version:发布版本, serialize:序列化选择</code></pre>
                        </div>
                    </div>

                    <div class="dao-step">
                        <div class="dao-step-number">3</div>
                        <div class="dao-step-content">
                            <h4>消费你的服务接口(@DaoReference)</h4>
                            <pre class="dao-code-block"><code>每个provider一定要设置自己的proxy名字! 确定唯一接口: proxy+provider+version
@DaoReference = 用于服务注入  provider:暴露服务的provider名称, version:发布版本, serialize:序列化选择, loadbanalce:负载路由选择, timeout:超时时间</code></pre>
                        </div>
                    </div>

                    <div class="dao-step">
                        <div class="dao-step-number">4</div>
                        <div class="dao-step-content">
                            <h4>配置中心的使用</h4>
                            <pre class="dao-code-block"><code>DaoConfig这个类提供了服务对配置信息的获取、订阅(详情看dao-cloud-example示例)
DaoConfig.getConf() 提供了获取配置信息的封装
DaoConfig.subscribe() 订阅配置,在监听到订阅的配置发生变化时,做某些事(回调)
注意: 此外你在配置中心更改配置后,配置中心会自动刷新到服务上</code></pre>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 网关中心 -->
            <div class="dao-help-card">
                <div class="dao-help-header">
                    <i class="fa fa-shield"></i>
                    <h2>网关中心</h2>
                </div>
                <div class="dao-help-content">
                    <div class="dao-step">
                        <div class="dao-step-number">1</div>
                        <div class="dao-step-content">
                            <h4>引入依赖</h4>
                            <p>在你的SpringBoot的工程中引入 dao-cloud-gateway maven的pom依赖，这样你就标记这个服务是网关服务</p>
                            <pre class="dao-code-block"><code>&lt;dependency&gt;
    &lt;groupId&gt;org.dao&lt;/groupId&gt;
    &lt;artifactId&gt;dao-cloud-gateway&lt;/artifactId&gt;
    &lt;version&gt;1.0-SNAPSHOT&lt;/version&gt;
&lt;/dependency&gt;</code></pre>
                        </div>
                    </div>

                    <div class="dao-step">
                        <div class="dao-step-number">2</div>
                        <div class="dao-step-content">
                            <h4>配置管理</h4>
                            <p>启动之后可以在注册中心对网关监控与配置</p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 日志查询 -->
            <div class="dao-help-card">
                <div class="dao-help-header">
                    <i class="fa fa-file-text"></i>
                    <h2>日志查询</h2>
                </div>
                <div class="dao-help-content">
                    <p>只要用DaoCloudLogger.getTraceId()获取到traceId值，然后去center页面中查询即可！</p>
                </div>
            </div>

        </section>
    </div>
    <@netCommon.commonFooter />
</div>
<@netCommon.commonScript />
</body>
</html>
