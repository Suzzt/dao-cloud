<!DOCTYPE html>
<html>
<head>
    <#import "common/common.macro.ftl" as netCommon>
    <title>使用教程</title>
    <@netCommon.commonStyle />
</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && cookieMap["dao-cloud_adminlte_settings"]?exists && "off" == cookieMap["dao-cloud_adminlte_settings"].value >sidebar-collapse</#if> ">
<div class="wrapper">
    <!-- header -->
    <@netCommon.commonHeader />
    <!-- left -->
    <@netCommon.commonLeft "help" />

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Main content -->
        <section class="content">
            <div class="callout callout-info">
                <h1 style="font-weight: bold;">项目介绍</h1>
                <p>
                    DaoCloud通过SpringBoot构建基于netty开发轻量级的微服务框架.麻雀虽小,五脏俱全(完全自研开发且完全开源,放心使用); 本项目追求轻量、易接入、自定义协议、高可用、高性能、高扩展、易上手等特性; 致力于简化应用程序之间的RPC调用，并为应用程序提供方便、无代码入侵、稳定和高效的点对点远程微服务调用解决方案。对于开发人员来说,dao-cloud的提供了丰富的模型抽象和可扩展接口,为求一站式解决微服务带来的系统架构复杂度,包括路由、负载平衡、故障转移、性能监控、微服务治理等;
                </p>
                <h1 style="font-weight: bold;">项目地址</h1>
                <p>
                    <a target="_blank" href="https://github.com/Suzzt/dao-cloud" style="display: inline; font-size: 18px; font-weight: bold; color: red;">github</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <a target="_blank" href="https://gitee.com/Suzzt0/dao-cloud" style="display: inline; font-size: 18px; font-weight: bold; color: red;">gitee</a>&nbsp;&nbsp;&nbsp;&nbsp;
                </p>
                <p>该项目中有个案例, 所有的案例使用都在dao-cloud-example工程下有详细的示例</p>
                <h1 style="font-weight: bold;">dao-cloud-center部署</h1>
                <h3>在dao-cloud微服务框架中, 很幸运, 这注册中心与配置中心是一体的, 即dao-cloud-center !</h3>
                <p>
                    1. 在你的SpringBoot的工程中引入 dao-cloud-center maven的pom依赖
                    <br>
                <p>
                <pre>
&lt;dependency&gt;
    &lt;groupId&gt;org.junmo&lt;/groupId&gt;
    &lt;artifactId&gt;dao-cloud-center&lt;/artifactId&gt;
    &lt;version&gt;1.0-SNAPSHOT&lt;/version&gt;
&lt;/dependency&gt;</pre>
                </p>
                    2. 然后用一个注解@EnableDaoCloudCenter搞定, 加在SpringBoot的启动类上, 这样单机版的dao-cloud-center就ok了
                </p>
                </p>
                    3. 如果你认为单机的dao-cloud-center不可靠, 可以把dao-cloud-center部署成集群。 在SpringBoot的yml中配置就行
                    <br>
                <pre>
dao-cloud:
  center:
    ############################ 集群方式 ############################
    # 配置第一个节点center必须是空，后续加入cluster中必须填写值。值可以是随便一个节点的ip地址
     cluster:
        ip: 192.168.31.254 # 这里填一个cluster其中一台机器的ip就行</pre>
                </p>
                <p>
                    4. 其他配置
                    <br>
                <pre>
dao-cloud:
  center:
    # 是否要开启可视化管理页面
    dashboard: true
    ############################ 配置中心 ############################
    config:
      persistence: file-system # file-system(本地文件)、mysql
      # 配置文件存储地址, 不填的话, 默认就是/data/dao-cloud/config, 请自己评估配置文件存放位置
      file-system-setting:
        path-prefix: /data/dao-cloud/config
      # 配置文件存放mysql的地址
      # mysql-setting:
      # url: 192.168.31.23
      # port: 3306
      # username: root
      # password: JunMo123
    ############################ 配置中心 ############################</pre>
                </p>
                <p>
                    启动你的SpringBoot Application服务,恭喜你！dao-cloud-center搭建完成
                    如果你打开了你的可视化管理页面, 控制台中会打印可以访问center web的页面。账号：root 密码：123456
                </p>
                <h1 style="font-weight: bold;">服务暴露与消费</h1>
                <p>
                    1. 在你的SpringBoot的工程中引入 dao-cloud-spring-boot-starter maven的pom依赖, 这样你就拥有了暴露与消费的能力了
                <pre>
&lt;dependency&gt;
    &lt;groupId&gt;org.junmo&lt;/groupId&gt;
    &lt;artifactId&gt;dao-cloud-spring-boot-starter&lt;/artifactId&gt;
    &lt;version&gt;1.0-SNAPSHOT&lt;/version&gt;
&lt;/dependency&gt;</pre>
                </p>
                <p>
                    2. 暴露你的服务接口(@DaoService)
                    <pre>
每个provider一定要设置自己的proxy名字! 确定唯一接口: proxy+provider+version
@DaoService 参数===provider:暴露服务的provider名称, version:发布版本, serialize:序列化选择</pre>
                </p>
                <p>
                    3. 消费你的服务接口(@DaoReference)
                <pre>
每个provider一定要设置自己的proxy名字! 确定唯一接口: proxy+provider+version
@DaoReference = 用于服务注入  provider:暴露服务的provider名称, version:发布版本, serialize:序列化选择, loadbanalce:负载路由选择, timeout:超时时间</pre>
                </p>
                <p>
                    4. 配置中心的使用
                <pre>
DaoConfig这个类提供了服务对配置信息的获取、订阅(详情看dao-cloud-example示例)
DaoConfig.getConf() 提供了获取配置信息的封装
DaoConfig.subscribe() 订阅配置,在监听到订阅的配置发生变化时,做某些事(回调)
注意: 此外你在配置中心更改配置后,配置中心会自动刷新到服务上 </pre>
                </p>
            </div>
        </section>
    </div>
    <@netCommon.commonFooter />
</div>
<@netCommon.commonScript />
</body>
</html>
