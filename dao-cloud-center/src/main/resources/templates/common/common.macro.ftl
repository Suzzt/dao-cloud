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
    
    <!-- dao-cloud 玻璃拟态设计系统 -->
    <link rel="stylesheet" href="${request.contextPath}/static/css/dao-glassmorphism.css">

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
    </script>

</#macro>

<#macro commonHeader>
    <style>
        /* 现代化头部样式 */
        .main-header {
            background: var(--primary-gradient);
            box-shadow: var(--shadow-md);
            border-bottom: none;
        }
        
        .main-header .logo {
            background: rgba(255, 255, 255, 0.1);
            backdrop-filter: blur(10px);
            border-radius: 0 12px 12px 0;
            margin: 8px 0;
            transition: all 0.3s ease;
        }
        
        .main-header .logo:hover {
            background: rgba(255, 255, 255, 0.2);
            transform: translateX(5px);
        }
        
        .main-header .logo-lg {
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 0 15px;
        }
        
        .main-header .navbar {
            background: transparent;
        }
        
        .sidebar-toggle {
            background: rgba(255, 255, 255, 0.1);
            border-radius: var(--radius-md);
            margin: 8px;
            padding: 12px 15px;
            transition: all 0.3s ease;
        }
        
        .sidebar-toggle:hover {
            background: rgba(255, 255, 255, 0.2);
            transform: scale(1.05);
        }
        
        #headerText {
            background: rgba(255, 255, 255, 0.1);
            backdrop-filter: blur(10px);
            padding: 8px 24px;
            border-radius: var(--radius-lg);
            transition: all 0.3s ease;
            animation: slideInDown 0.6s ease-out;
        }
        
        #logoutBtn {
            display: flex;
            justify-content: center;
            align-items: center;
            height: 44px;
            color: #ffffff;
            font-size: 14px;
            font-weight: 600;
            background: rgba(255, 255, 255, 0.1);
            backdrop-filter: blur(10px);
            border-radius: var(--radius-md);
            margin: 8px;
            padding: 0 16px;
            transition: all 0.3s ease;
            text-decoration: none;
            border: 1px solid rgba(255, 255, 255, 0.2);
        }

        #logoutBtn:hover {
            background: rgba(255, 255, 255, 0.2);
            color: #ffffff;
            transform: translateY(-1px);
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
            text-decoration: none;
        }
        
        #logoutBtn i {
            margin-right: 6px;
            transition: transform 0.3s ease;
        }
        
        #logoutBtn:hover i {
            transform: translateX(-2px);
        }
    </style>
    <header class="main-header">
        <a href="${request.contextPath}" class="logo">
            <span class="logo-mini"><b>dao</b></span>
            <span class="logo-lg">
                <img src="${request.contextPath}/static/dao-cloud-logo.png" alt="dao-cloud logo"
                     style="height: 30px; margin-right: 12px;"/>
                <b style="color: white; font-size: 18px;">dao-cloud</b>
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

            <div class="navbar-custom-menu" style="float: right;">
                <ul class="nav navbar-nav">
                    <li>
                        <a href="javascript:void(0);" id="logoutBtn">
                            <i class="fa fa-sign-out" aria-hidden="true"></i>退出登录
                        </a>
                    </li>
                </ul>
            </div>
        </nav>
    </header>
</#macro>

<#macro commonLeft pageName >
    <style>
        /* 现代化侧边栏样式 */
        .main-sidebar {
            background: linear-gradient(180deg, #2c3e50 0%, #34495e 100%);
            box-shadow: var(--shadow-lg);
        }
        
        .sidebar {
            padding-top: 20px;
        }
        
        .sidebar-menu > .header {
            background: rgba(255, 255, 255, 0.1);
            color: rgba(255, 255, 255, 0.8);
            padding: 12px 20px;
            font-size: 12px;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 1px;
            margin: 0 15px 15px;
            border-radius: var(--radius-md);
            backdrop-filter: blur(10px);
        }
        
        .sidebar-menu > li {
            margin: 0 15px 8px;
        }
        
        .sidebar-menu > li > a {
            color: rgba(255, 255, 255, 0.85);
            padding: 14px 18px;
            border-radius: var(--radius-md);
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            font-weight: 500;
            border: 1px solid transparent;
        }
        
        .sidebar-menu > li > a:hover {
            background: rgba(255, 255, 255, 0.1);
            color: white;
            transform: translateX(8px);
            border-color: rgba(255, 255, 255, 0.2);
        }
        
        .sidebar-menu > li.active > a {
            background: var(--primary-gradient);
            color: white;
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
            border-color: rgba(255, 255, 255, 0.3);
        }
        
        .sidebar-menu > li > a > i {
            margin-right: 12px;
            font-size: 16px;
            width: 20px;
            text-align: center;
            transition: transform 0.3s ease;
        }
        
        .sidebar-menu > li > a:hover > i {
            transform: scale(1.1);
        }
        
        .sidebar-menu > li.active > a > i {
            animation: pulse 2s infinite;
        }
        
        .sidebar-menu > li > a > span {
            font-size: 14px;
            line-height: 1.4;
        }
        
        /* 导航图标颜色 */
        .sidebar-menu .text-red { color: #e74c3c !important; }
        .sidebar-menu .text-orange { color: #f39c12 !important; }
        .sidebar-menu .text-blue { color: #3498db !important; }
        .sidebar-menu .text-success { color: #27ae60 !important; }
        .sidebar-menu .text-nowrap { color: #9b59b6 !important; }
        .sidebar-menu .text-green { color: #2ecc71 !important; }
    </style>
    <!-- Left side column. contains the logo and sidebar -->
    <aside class="main-sidebar">
        <!-- sidebar: style can be found in sidebar.less -->
        <section class="sidebar">
            <!-- sidebar menu: style can be found in sidebar.less -->
            <ul class="sidebar-menu">
                <li class="nav-click <#if pageName == "index">active</#if>">
                    <a href="${request.contextPath}/dao-cloud">
                        <i class="fa fa-dashboard text-red"></i>
                        <span>指标概况</span>
                    </a>
                </li>
                <li class="nav-click <#if pageName == "registry">active</#if>">
                    <a href="${request.contextPath}/dao-cloud/registry">
                        <i class="fa fa-server text-orange"></i>
                        <span>服务中心</span>
                    </a>
                </li>
                <li class="nav-click <#if pageName == "configuration">active</#if>">
                    <a href="${request.contextPath}/dao-cloud/configuration">
                        <i class="fa fa-file-code-o text-blue"></i>
                        <span>配置文件</span>
                    </a>
                </li>
                <li class="nav-click <#if pageName == "config">active</#if>">
                    <a href="${request.contextPath}/dao-cloud/config">
                        <i class="fa fa-cogs text-success"></i>
                        <span>配置订阅</span>
                    </a>
                </li>
                <li class="nav-click <#if pageName == "log">active</#if>">
                    <a href="${request.contextPath}/dao-cloud/log">
                        <i class="fa fa-list-alt text-nowrap"></i>
                        <span>日志中心</span>
                    </a>
                </li>
                <li class="nav-click <#if pageName == "help">active</#if>">
                    <a href="${request.contextPath}/dao-cloud/help">
                        <i class="fa fa-question-circle text-green"></i>
                        <span>使用教程</span>
                    </a>
                </li>
            </ul>
        </section>
        <!-- /.sidebar -->
    </aside>
</#macro>

<#macro commonFooter >
    <style>
        /* 现代化底部样式 */
        .main-footer {
            background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
            border-top: 1px solid var(--border-color);
            padding: 20px;
            color: var(--text-secondary);
            font-size: 13px;
            box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
        }
        
        .main-footer a {
            color: var(--primary-color);
            font-weight: 600;
            text-decoration: none;
            transition: all 0.3s ease;
        }
        
        .main-footer a:hover {
            color: var(--primary-dark);
            text-shadow: 0 1px 2px rgba(102, 126, 234, 0.2);
        }
        
        .main-footer .pull-right {
            font-weight: 500;
        }
    </style>
    <footer class="main-footer">
        <div class="pull-left">
            Powered by <a href="https://github.com/Suzzt/dao-cloud" target="_blank"><b>dao-cloud</b></a> 
            - 分布式服务中心管理平台
        </div>
        <div class="pull-right hidden-xs">
            <strong>
                <a href="https://github.com/Suzzt/dao-cloud" target="_blank">
                    <i class="fa fa-github"></i> 查看源码
                </a>
            </strong>
        </div>
    </footer>
</#macro>