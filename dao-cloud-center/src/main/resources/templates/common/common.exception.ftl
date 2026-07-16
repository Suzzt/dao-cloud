<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>系统错误 - dao-cloud</title>
    <link rel="icon" href="${request.contextPath}/static/dao-cloud-logo.png" sizes="64x64"/>
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/font-awesome/css/font-awesome.min.css">
    <link rel="stylesheet" href="${request.contextPath}/static/css/dao-cloud-theme.css">
    <script>
        (function () {
            try {
                var t = localStorage.getItem('dao-theme');
                if (!t) { t = window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light'; }
                document.documentElement.setAttribute('data-theme', t);
            } catch (e) { document.documentElement.setAttribute('data-theme', 'light'); }
        })();
    </script>
    <style>
        body {
            background: var(--app-bg);
            min-height: 100vh;
            font-family: var(--font-sans);
            color: var(--text);
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 24px;
            position: relative;
            overflow: hidden;
        }
        .err-bg::before {
            content: '';
            position: absolute;
            width: 520px; height: 520px;
            top: -180px; right: -140px;
            border-radius: 50%;
            filter: blur(90px);
            opacity: 0.4;
            background: radial-gradient(circle, rgba(220,38,38,0.4), transparent 70%);
            pointer-events: none;
        }
        :root[data-theme="dark"] .err-bg::before { opacity: 0.24; }

        .err-card {
            position: relative;
            z-index: 2;
            width: 100%;
            max-width: 480px;
            background: var(--surface);
            border: 1px solid var(--border);
            border-radius: var(--radius-lg);
            box-shadow: var(--shadow-xl);
            padding: 40px 36px 32px;
            text-align: center;
            animation: errIn 0.45s cubic-bezier(0.16, 1, 0.3, 1);
        }
        @keyframes errIn {
            from { opacity: 0; transform: translateY(16px); }
            to { opacity: 1; transform: translateY(0); }
        }
        .err-icon {
            width: 72px; height: 72px;
            margin: 0 auto 20px;
            border-radius: 50%;
            background: var(--danger-soft);
            color: var(--danger);
            display: flex; align-items: center; justify-content: center;
            font-size: 34px;
        }
        .err-title {
            font-size: 22px;
            font-weight: 700;
            color: var(--text);
            margin: 0 0 16px;
        }
        .err-message {
            text-align: left;
            background: var(--surface-2);
            border: 1px solid var(--border);
            border-left: 3px solid var(--danger);
            border-radius: var(--radius-sm);
            padding: 14px 16px;
            font-size: 13.5px;
            line-height: 1.7;
            color: var(--text-secondary);
            word-break: break-word;
            max-height: 220px;
            overflow: auto;
            margin-bottom: 26px;
        }
        .err-message strong { color: var(--text); }
        .err-actions {
            display: flex;
            gap: 12px;
            justify-content: center;
        }
        .err-btn {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 7px;
            height: 44px;
            padding: 0 22px;
            border-radius: var(--radius-sm);
            font-size: 14px;
            font-weight: 600;
            text-decoration: none;
            cursor: pointer;
            transition: all 0.18s ease;
        }
        .err-btn-primary {
            background: var(--brand-gradient);
            color: #fff;
            box-shadow: 0 8px 20px rgba(102,126,234,0.3);
        }
        .err-btn-primary:hover { filter: brightness(1.05); color: #fff; }
        .err-btn-secondary {
            background: var(--surface);
            color: var(--text-secondary);
            border: 1px solid var(--border-strong);
        }
        .err-btn-secondary:hover { border-color: var(--brand); color: var(--brand); }

        @media (max-width: 480px) {
            .err-card { padding: 32px 22px 26px; }
            .err-actions { flex-direction: column; }
            .err-btn { width: 100%; }
        }
    </style>
</head>
<body>
    <div class="err-bg"></div>
    <div class="err-card">
        <div class="err-icon"><i class="fa fa-exclamation-triangle"></i></div>
        <h1 class="err-title">系统错误</h1>
        <div class="err-message">
            <strong>错误详情：</strong>${exceptionMsg}
        </div>
        <div class="err-actions">
            <a href="javascript:history.back()" class="err-btn err-btn-secondary">
                <i class="fa fa-arrow-left"></i><span>返回上一页</span>
            </a>
            <a href="${request.contextPath}/" class="err-btn err-btn-primary">
                <i class="fa fa-home"></i><span>返回首页</span>
            </a>
        </div>
    </div>
</body>
</html>
