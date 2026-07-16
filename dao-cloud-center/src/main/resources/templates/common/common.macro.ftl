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

    <!-- dao-cloud 统一主题样式 -->
    <link rel="stylesheet" href="${request.contextPath}/static/css/dao-cloud-theme.css">

    <!-- 主题模式初始化（防止刷新闪烁 FOUC） -->
    <script>
        (function () {
            try {
                var t = localStorage.getItem('dao-theme');
                if (!t) {
                    t = window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
                }
                document.documentElement.setAttribute('data-theme', t);
            } catch (e) {
                document.documentElement.setAttribute('data-theme', 'light');
            }
        })();
    </script>

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

        // 主题切换：亮 <-> 暗，持久化并广播 daoThemeChange 事件（供 ECharts 等响应）
        (function () {
            var btn = document.getElementById('daoThemeToggle');
            if (!btn) return;
            btn.addEventListener('click', function () {
                var cur = document.documentElement.getAttribute('data-theme') === 'dark' ? 'dark' : 'light';
                var next = cur === 'dark' ? 'light' : 'dark';
                document.documentElement.setAttribute('data-theme', next);
                try { localStorage.setItem('dao-theme', next); } catch (e) {}
                window.dispatchEvent(new CustomEvent('daoThemeChange', { detail: { theme: next } }));
            });
        })();
    </script>

</#macro>

<#macro commonHeader>
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
                        <a href="javascript:void(0);" id="daoThemeToggle" title="切换主题">
                            <i class="fa fa-moon-o dao-theme-moon" aria-hidden="true"></i>
                            <i class="fa fa-sun-o dao-theme-sun" aria-hidden="true"></i>
                            <span class="hidden-xs">主题</span>
                        </a>
                    </li>
                    <li>
                        <a href="javascript:void(0);" id="logoutBtn">
                            <i class="fa fa-sign-out" aria-hidden="true"></i><span class="hidden-xs">退出登录</span>
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