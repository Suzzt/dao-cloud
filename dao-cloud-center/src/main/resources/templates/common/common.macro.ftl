<#macro commonStyle>

<#-- favicon -->
    <link rel="icon" href="${request.contextPath}/static/dao-cloud-logo.png" sizes="64x64"/>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <!-- Tell the browser to be responsive to screen width -->
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <!-- Bootstrap -->
    <link rel="stylesheet"
          href="${request.contextPath}/static/adminlte/bower_components/bootstrap/css/bootstrap.min.css">
    <!-- Font Awesome -->
    <link rel="stylesheet"
          href="${request.contextPath}/static/adminlte/bower_components/font-awesome/css/font-awesome.min.css">
    <!-- Ionicons -->
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/Ionicons/css/ionicons.min.css">
    <!-- Theme style -->
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/dist/css/AdminLTE.min.css">
    <!-- AdminLTE Skins. Choose a skin from the css/skins folder instead of downloading all of them to reduce the load. -->
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/dist/css/skins/_all-skins.min.css">

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->

    <!-- pace -->
    <link rel="stylesheet"
          href="${request.contextPath}/static/adminlte/bower_components/PACE/themes/blue/pace-theme-flash.css">

    <!-- scrollup -->
    <link rel="stylesheet" href="${request.contextPath}/static/plugins/scrollup/image.css">

</#macro>

<#macro commonScript>

    <!-- jQuery 2.1.4 -->
    <script src="${request.contextPath}/static/adminlte/bower_components/jquery/jquery.min.js"></script>
    <!-- Bootstrap 3.3.5 -->
    <script src="${request.contextPath}/static/adminlte/bower_components/bootstrap/js/bootstrap.min.js"></script>
    <!-- FastClick -->
    <script src="${request.contextPath}/static/adminlte/bower_components/fastclick/fastclick.js"></script>
    <!-- AdminLTE App -->
    <script src="${request.contextPath}/static/adminlte/dist/js/adminlte.min.js"></script>
    <!-- jquery.slimscroll -->
    <script src="${request.contextPath}/static/adminlte/bower_components/jquery-slimscroll/jquery.slimscroll.min.js"></script>

    <!-- pace -->
    <script src="${request.contextPath}/static/adminlte/bower_components/PACE/pace.min.js"></script>
<#-- jquery cookie -->
    <script src="${request.contextPath}/static/plugins/jquery/jquery.cookie.js"></script>

<#-- layer -->
    <script src="${request.contextPath}/static/plugins/layer/layer.js"></script>

    <!-- scrollup -->
    <script src="${request.contextPath}/static/plugins/scrollup/jquery.scrollUp.min.js"></script>

<#-- common -->
    <script src="${request.contextPath}/static/js/common.1.js"></script>
    <script>
        var base_url = '${request.contextPath}' + "/dao-cloud";
    </script>

</#macro>

<#macro commonHeader>
    <header class="main-header">
        <a href="${request.contextPath}" class="logo">
            <span class="logo-mini"><b>dao</b></span>
            <span class="logo-lg"><img src="${request.contextPath}/static/dao-cloud-logo.png" alt="dao-cloud logo" style="height: 30px; margin-right: 15px;"/> <b>dao-cloud</b></span>
        </a>
        <nav class="navbar navbar-static-top" role="navigation">

            <a href="#" class="sidebar-toggle" data-toggle="push-menu" role="button">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </a>

            <div class="navbar-custom-menu">
                <ul class="nav navbar-nav">
                    <li class="dropdown user user-menu">
                        <a href=";" id="logoutBtn" class="dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
                            <span class="hidden-xs">退出登陆</span>
                        </a>
                    </li>
                </ul>
            </div>
        </nav>
    </header>
</#macro>

<#macro commonLeft pageName >
    <!-- Left side column. contains the logo and sidebar -->
    <aside class="main-sidebar">
        <!-- sidebar: style can be found in sidebar.less -->
        <section class="sidebar">
            <!-- sidebar menu: : style can be found in sidebar.less -->
            <ul class="sidebar-menu">
                <li class="header">导航菜单</li>
                <li class="nav-click <#if pageName == "index">active</#if>"><a href="${request.contextPath}/dao-cloud"><i
                                class="fa fa-circle-o text-red"></i><span>指标概况</span></a></li>
                <li class="nav-click <#if pageName == "registry">active</#if>"><a
                            href="${request.contextPath}/dao-cloud/registry"><i
                                class="fa fa-circle-o text-orange"></i><span>服务中心</span></a></li>
<#--                <li class="nav-click <#if pageName == "gateway">active</#if>"><a-->
<#--                            href="${request.contextPath}/dao-cloud/gateway"><i-->
<#--                                class="fa fa-circle-o text-blue"></i><span>网关中心</span></a></li>-->
                <li class="nav-click <#if pageName == "config">active</#if>"><a href="${request.contextPath}/dao-cloud/config"><i
                                class="fa fa-circle-o text-blue"></i><span>配置中心</span></a></li>
                <li class="nav-click <#if pageName == "log">active</#if>"><a href="${request.contextPath}/dao-cloud/log"><i
                                class="fa fa-circle-o text-nowrap"></i><span>日志查询</span></a></li>
                <li class="nav-click <#if pageName == "help">active</#if>"><a href="${request.contextPath}/dao-cloud/help"><i
                                class="fa fa-circle-o text-green"></i><span>使用教程</span></a></li>
            </ul>
        </section>
        <!-- /.sidebar -->
    </aside>
</#macro>

<#macro commonFooter >
    <footer class="main-footer">
        Powered by <b>dao-cloud</b>
        <div class="pull-right hidden-xs">
            <strong>去
                <a href="https://github.com/Suzzt/dao-cloud" target="_blank">github</a>
                看源码!
            </strong>
        </div>
    </footer>
</#macro>