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
                <h1>项目地址</h1>
                <p>
                    <a target="_blank" href="https://github.com/Suzzt/dao-cloud" style="display: inline; font-size: 18px; font-weight: bold; color: red;">github</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <a target="_blank" href="https://gitee.com/Suzzt0/dao-cloud" style="display: inline; font-size: 18px; font-weight: bold; color: red;">gitee</a>&nbsp;&nbsp;&nbsp;&nbsp;
                </p>
                <br>
                <h1>服务部署</h1>
                <h3>1.建立dao-cloud-center</h3>
                <h4>在dao-cloud微服务框架中, 很幸运, 这注册中心与配置中心是一体的, 即dao-cloud-center !</h4>
                <p>
                    a. 在你的SpringBoot的工程中引入 dao-cloud-center maven的pom依赖
                    <br>
                <p>
                <pre>
&lt;dependency&gt;
    &lt;groupId&gt;org.junmo&lt;/groupId&gt;
    &lt;artifactId&gt;dao-cloud-center&lt;/artifactId&gt;
    &lt;version&gt;1.0-SNAPSHOT&lt;/version&gt;
&lt;/dependency&gt;</pre>
                </p>
                    b. 然后用一个注解@EnableDaoCloudCenter搞定, 加在SpringBoot的启动类上, 这样单机版的dao-cloud-center就ok了
                </p>
                </p>
                    c. 如果你认为单机的dao-cloud-center不可靠, 可以把dao-cloud-center部署成集群, 在SpringBoot的yml中配置就行
                    <br>
                <pre>
dao-cloud:
  center:
    ############################ 集群方式 ############################
    # 配置第一个节点center必须是空，后续加入cluster中必须填写值。值可以是随便一个节点的ip地址
     cluster:
        ip: 192.168.31.254 # 这里填一个cluster其中一台机器的ip就行</pre>
                </p>
            </div>
        </section>
        <!-- /.content -->
    </div>
    <!-- /.content-wrapper -->

    <!-- footer -->
    <@netCommon.commonFooter />
</div>
<@netCommon.commonScript />
</body>
</html>
