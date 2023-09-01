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
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>dao-cloud<small></small></h1>
        </section>

        <!-- Main content -->
        <section class="content">
            <div class="callout callout-info">
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
            </div>
        </section>
    </div>
    <@netCommon.commonFooter />
</div>
<@netCommon.commonScript />
</body>
</html>
