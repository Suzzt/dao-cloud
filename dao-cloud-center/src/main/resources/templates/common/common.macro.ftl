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
<#--    <script src="${request.contextPath}/static/adminlte/bower_components/PACE/pace.min.js"></script>-->
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
        var current_page_value = $('.sidebar-menu li.active a span').text();
        ;
        $('#headerText').text(current_page_value);
    </script>

</#macro>

<#macro commonHeader>
    <style>
        /* 定义退出登录按钮样式及悬停高亮效果 */
        #logoutBtn {
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100%;
            color: #ffffff;
            font-size: 16px;
            background-color: transparent;
            transition: background-color 0.3s ease, color 0.3s ease;
        }

        #logoutBtn:hover {
            background-color: #3c8dbc; /* 悬停时背景颜色高亮 */
            color: #ffffff; /* 保持文字白色 */
        }
    </style>
    <header class="main-header">
        <a href="${request.contextPath}" class="logo">
            <span class="logo-mini"><b>dao</b></span>
            <span class="logo-lg">
                <img src="${request.contextPath}/static/dao-cloud-logo.png" alt="dao-cloud logo"
                     style="height: 30px; margin-right: 15px;"/>
                <b>dao-cloud</b>
            </span>
        </a>
        <nav class="navbar navbar-static-top" role="navigation">
            <!-- 左侧菜单切换按钮 -->
            <a href="#" class="sidebar-toggle" data-toggle="push-menu" role="button">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </a>

            <!-- 文案显示区域，位于导航栏中间，并且上下左右居中 -->
            <div class="navbar-text" id="headerText"
                 style="position: absolute; left: 50%; transform: translateX(-50%); text-align: center; font-size: 18px; font-weight: bold; color: #FFFFFF;">
                dao-cloud
            </div>

            <div class="navbar-custom-menu" style="float: right;">
                <ul class="nav navbar-nav">
                    <li class="dropdown" style="flex-grow: 1;">
                        <a href="javascript:void(0);" id="logoutBtn" class="dropdown-toggle"
                           style="display: flex; justify-content: center; align-items: center; height: 100%; color: #ffffff; font-size: 16px; background-color: transparent;">
                            <i class="fa fa-sign-out" aria-hidden="true" style="margin-right: 8px;"></i>退出登陆
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
                <li class="nav-click <#if pageName == "index">active</#if>"><a
                            href="${request.contextPath}/dao-cloud"><i
                                class="fa fa-circle-o text-red"></i><span>指标概况</span></a></li>
                <li class="nav-click <#if pageName == "registry">active</#if>"><a
                            href="${request.contextPath}/dao-cloud/registry"><i
                                class="fa fa-circle-o text-orange"></i><span>服务中心</span></a></li>
                <li class="nav-click <#if pageName == "config">active</#if>"><a
                            href="${request.contextPath}/dao-cloud/config"><i
                                class="fa fa-circle-o text-blue"></i><span>配置中心</span></a></li>
                <li class="nav-click <#if pageName == "log">active</#if>"><a
                            href="${request.contextPath}/dao-cloud/log"><i
                                class="fa fa-circle-o text-nowrap"></i><span>日志中心</span></a></li>
                <li class="nav-click <#if pageName == "help">active</#if>"><a
                            href="${request.contextPath}/dao-cloud/help"><i
                                class="fa fa-circle-o text-green"></i><span>使用教程</span></a></li>
            </ul>
        </section>
        <!-- /.sidebar -->
    </aside>
</#macro>

<#macro commonFooter >
    <footer class="main-footer">
        Powered by <a href="https://github.com/Suzzt/dao-cloud" target="_blank"><b>dao-cloud</b></a>
        <div class="pull-right hidden-xs">
            <strong>去
                <a href="https://github.com/Suzzt/dao-cloud" target="_blank">github</a>
                看源码!
            </strong>
        </div>
    </footer>
</#macro>