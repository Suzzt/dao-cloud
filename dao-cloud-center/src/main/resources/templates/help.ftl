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
                <br>
                <p>
                    <a target="_blank" href="https://github.com/Suzzt/dao-cloud">Github</a>&nbsp;&nbsp;&nbsp;&nbsp;
                    <br><br>
                    <b>待详细构建中......</b>
                </p>
                <br>
                <h1>服务部署</h1>
                <p></p>
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
