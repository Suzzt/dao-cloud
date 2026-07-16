<!DOCTYPE html>
<html>
<head>
	<#import "common/common.macro.ftl" as netCommon>
  	<title>登录 - dao-cloud 分布式服务治理控制台</title>
	<@netCommon.commonStyle />
	<style>
		body {
			background: var(--app-bg);
			min-height: 100vh;
			font-family: var(--font-sans);
			color: var(--text);
			position: relative;
			overflow: hidden;
		}
		/* 柔和品牌氛围背景（静态、克制） */
		.login-bg::before,
		.login-bg::after {
			content: '';
			position: absolute;
			border-radius: 50%;
			filter: blur(80px);
			opacity: 0.5;
			pointer-events: none;
		}
		.login-bg::before {
			width: 520px; height: 520px;
			top: -160px; left: -140px;
			background: radial-gradient(circle, rgba(102,126,234,0.55), transparent 70%);
		}
		.login-bg::after {
			width: 460px; height: 460px;
			bottom: -160px; right: -120px;
			background: radial-gradient(circle, rgba(118,75,162,0.5), transparent 70%);
		}
		:root[data-theme="dark"] .login-bg::before,
		:root[data-theme="dark"] .login-bg::after { opacity: 0.32; }

		.login-topbar {
			position: absolute;
			top: 20px; right: 24px;
			z-index: 5;
		}
		.login-theme-btn {
			display: inline-flex;
			align-items: center;
			gap: 7px;
			height: 38px;
			padding: 0 14px;
			border-radius: var(--radius-round);
			border: 1px solid var(--border);
			background: var(--surface);
			color: var(--text-secondary);
			font-size: 13px;
			font-weight: 600;
			cursor: pointer;
			box-shadow: var(--shadow-xs);
			transition: all 0.18s ease;
		}
		.login-theme-btn:hover { color: var(--brand); border-color: var(--brand); }
		.login-theme-btn .dao-theme-sun { display: none; }
		:root[data-theme="dark"] .login-theme-btn .dao-theme-sun { display: inline-block; }
		:root[data-theme="dark"] .login-theme-btn .dao-theme-moon { display: none; }

		.login-wrap {
			position: relative;
			z-index: 2;
			min-height: 100vh;
			display: flex;
			align-items: center;
			justify-content: center;
			padding: 24px;
		}
		.login-card {
			width: 100%;
			max-width: 400px;
			background: var(--surface);
			border: 1px solid var(--border);
			border-radius: var(--radius-lg);
			box-shadow: var(--shadow-xl);
			padding: 40px 36px 32px;
			animation: loginIn 0.5s cubic-bezier(0.16, 1, 0.3, 1);
		}
		@keyframes loginIn {
			from { opacity: 0; transform: translateY(18px); }
			to { opacity: 1; transform: translateY(0); }
		}
		.login-brand { text-align: center; margin-bottom: 30px; }
		.login-logo {
			width: 66px; height: 66px;
			margin: 0 auto 16px;
			background: var(--brand-gradient);
			border-radius: var(--radius-lg);
			display: flex; align-items: center; justify-content: center;
			box-shadow: 0 10px 24px rgba(102,126,234,0.35);
		}
		.login-logo i { font-size: 30px; color: #fff; }
		.login-title {
			font-size: 24px;
			font-weight: 700;
			color: var(--text);
			margin: 0 0 6px;
			letter-spacing: -0.3px;
		}
		.login-subtitle {
			color: var(--text-muted);
			font-size: 13.5px;
			margin: 0;
		}

		.login-form .form-group {
			margin-bottom: 20px;
			position: relative;
		}
		.login-field { position: relative; }
		.login-field > .field-icon {
			position: absolute;
			left: 15px; top: 50%;
			transform: translateY(-50%);
			color: var(--text-muted);
			font-size: 16px;
			pointer-events: none;
			transition: color 0.18s ease;
		}
		.login-form .form-control {
			width: 100%;
			height: 48px;
			padding: 0 46px;
			font-size: 14.5px;
			color: var(--text);
			background: var(--surface-2);
			border: 1px solid var(--border-strong);
			border-radius: var(--radius-sm);
			transition: border-color 0.18s ease, box-shadow 0.18s ease, background-color 0.18s ease;
			box-sizing: border-box;
		}
		.login-form .form-control::placeholder { color: var(--text-muted); }
		.login-form .form-control:focus {
			outline: none;
			border-color: var(--brand);
			background: var(--surface);
			box-shadow: var(--ring);
		}
		.login-field:focus-within > .field-icon { color: var(--brand); }
		.password-toggle {
			position: absolute;
			right: 14px; top: 50%;
			transform: translateY(-50%);
			color: var(--text-muted);
			font-size: 16px;
			cursor: pointer;
			z-index: 3;
			transition: color 0.18s ease;
		}
		.password-toggle:hover { color: var(--brand); }

		.help-block {
			display: block;
			color: var(--danger);
			font-size: 12px;
			margin-top: 6px;
		}
		.form-group.has-error .form-control {
			border-color: var(--danger);
			box-shadow: 0 0 0 3px var(--danger-soft);
		}
		.form-group.has-error .field-icon,
		.form-group.has-error .password-toggle { color: var(--danger); }

		.login-btn {
			width: 100%;
			height: 48px;
			margin-top: 6px;
			background: var(--brand-gradient);
			border: none;
			border-radius: var(--radius-sm);
			color: #fff;
			font-size: 15px;
			font-weight: 600;
			letter-spacing: 2px;
			cursor: pointer;
			box-shadow: 0 8px 20px rgba(102,126,234,0.32);
			transition: filter 0.18s ease, transform 0.06s ease, box-shadow 0.18s ease;
		}
		.login-btn:hover { filter: brightness(1.05); box-shadow: 0 10px 26px rgba(102,126,234,0.4); }
		.login-btn:active { transform: translateY(1px); }

		.login-footer {
			text-align: center;
			margin-top: 24px;
			font-size: 12.5px;
			color: var(--text-muted);
		}
		.login-footer a { color: var(--text-secondary); font-weight: 600; }
		.login-footer a:hover { color: var(--brand); }

		@media (max-width: 480px) {
			.login-card { padding: 32px 22px 26px; }
			.login-title { font-size: 22px; }
		}
	</style>
</head>
<body>
	<div class="login-bg"></div>

	<div class="login-topbar">
		<button type="button" class="login-theme-btn" id="daoThemeToggle" title="切换主题">
			<i class="fa fa-moon-o dao-theme-moon"></i>
			<i class="fa fa-sun-o dao-theme-sun"></i>
			<span>主题</span>
		</button>
	</div>

	<div class="login-wrap">
		<div class="login-card">
			<div class="login-brand">
				<div class="login-logo"><i class="fa fa-cloud"></i></div>
				<h1 class="login-title">dao-cloud</h1>
				<p class="login-subtitle">分布式服务治理控制台</p>
			</div>

			<form id="loginForm" method="post" class="login-form">
				<div class="form-group">
					<div class="login-field">
						<i class="fa fa-user field-icon"></i>
						<input type="text" name="userName" class="form-control" placeholder="请输入登录账号" value="admin" maxlength="18" autocomplete="username">
					</div>
				</div>

				<div class="form-group">
					<div class="login-field">
						<i class="fa fa-lock field-icon"></i>
						<input type="password" name="password" id="passwordInput" class="form-control" placeholder="请输入登录密码" maxlength="18" autocomplete="current-password">
						<i class="fa fa-eye password-toggle" id="passwordToggle" title="显示密码"></i>
					</div>
				</div>

				<button type="submit" class="login-btn">登 录</button>
			</form>

			<div class="login-footer">
				Powered by <a href="https://github.com/Suzzt/dao-cloud" target="_blank">dao-cloud</a>
			</div>
		</div>
	</div>

<@netCommon.commonScript />
<script src="${request.contextPath}/static/plugins/jquery/jquery.validate.min.js"></script>
<script src="${request.contextPath}/static/js/login.1.js"></script>

<script>
$(function() {
	// 密码显示 / 隐藏
	$('#passwordToggle').on('click', function() {
		var input = $('#passwordInput');
		var icon = $(this);
		if (input.attr('type') === 'password') {
			input.attr('type', 'text');
			icon.removeClass('fa-eye').addClass('fa-eye-slash').attr('title', '隐藏密码');
		} else {
			input.attr('type', 'password');
			icon.removeClass('fa-eye-slash').addClass('fa-eye').attr('title', '显示密码');
		}
	});
});
</script>
</body>
</html>
