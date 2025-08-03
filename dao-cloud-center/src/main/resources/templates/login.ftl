<!DOCTYPE html>
<html>
<head>
	<#import "common/common.macro.ftl" as netCommon>
  	<title>dao-cloud - 分布式服务中心</title>
	<@netCommon.commonStyle />
	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/iCheck/square/blue.css">
	<style>
		body {
			background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
			min-height: 100vh;
			font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
			overflow: hidden;
		}
		
		.login-container {
			display: flex;
			justify-content: center;
			align-items: center;
			min-height: 100vh;
			position: relative;
		}
		
		.login-card {
			background: rgba(255, 255, 255, 0.95);
			backdrop-filter: blur(10px);
			border-radius: 20px;
			padding: 40px;
			box-shadow: 0 25px 45px rgba(0, 0, 0, 0.2);
			width: 400px;
			max-width: 90vw;
			position: relative;
			z-index: 2;
			animation: slideInUp 0.8s ease-out;
		}
		
		@keyframes slideInUp {
			from {
				opacity: 0;
				transform: translateY(50px);
			}
			to {
				opacity: 1;
				transform: translateY(0);
			}
		}
		
		.login-header {
			text-align: center;
			margin-bottom: 30px;
		}
		
		.login-logo {
			width: 80px;
			height: 80px;
			margin: 0 auto 20px;
			background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
			border-radius: 50%;
			display: flex;
			align-items: center;
			justify-content: center;
			box-shadow: 0 10px 25px rgba(102, 126, 234, 0.3);
		}
		
		.login-logo i {
			font-size: 36px;
			color: white;
		}
		
		.login-title {
			font-size: 28px;
			font-weight: 700;
			color: #333;
			margin-bottom: 5px;
		}
		
		.login-subtitle {
			color: #666;
			font-size: 14px;
			margin-bottom: 0;
		}
		
		.form-group {
			margin-bottom: 25px;
			position: relative;
		}
		
		.form-control {
			height: 50px;
			border: 2px solid #e1e5e9;
			border-radius: 12px;
			padding: 0 20px 0 50px;
			font-size: 16px;
			background: #f8f9fa;
			transition: all 0.3s ease;
		}
		
		.form-control:focus {
			border-color: #667eea;
			background: white;
			box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
			outline: none;
		}
		
		.form-icon {
			position: absolute;
			left: 18px;
			top: 50%;
			transform: translateY(-50%);
			color: #999;
			font-size: 18px;
			z-index: 1;
		}
		
		.remember-me {
			display: flex;
			align-items: center;
			margin-bottom: 25px;
		}
		
		.remember-me input[type="checkbox"] {
			margin-right: 8px;
			transform: scale(1.2);
		}
		
		.remember-me label {
			font-size: 14px;
			color: #666;
			margin-bottom: 0;
			cursor: pointer;
		}
		
		.login-btn {
			width: 100%;
			height: 50px;
			background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
			border: none;
			border-radius: 12px;
			color: white;
			font-size: 16px;
			font-weight: 600;
			cursor: pointer;
			transition: all 0.3s ease;
			position: relative;
			overflow: hidden;
		}
		
		.login-btn:hover {
			transform: translateY(-2px);
			box-shadow: 0 15px 35px rgba(102, 126, 234, 0.4);
		}
		
		.login-btn:active {
			transform: translateY(0);
		}
		
		.floating-shapes {
			position: absolute;
			top: 0;
			left: 0;
			width: 100%;
			height: 100%;
			overflow: hidden;
			z-index: 1;
		}
		
		.shape {
			position: absolute;
			border-radius: 50%;
			background: rgba(255, 255, 255, 0.1);
			animation: float 6s ease-in-out infinite;
		}
		
		@keyframes float {
			0%, 100% { transform: translateY(0px) rotate(0deg); }
			50% { transform: translateY(-20px) rotate(180deg); }
		}
		
		.shape:nth-child(1) {
			width: 80px;
			height: 80px;
			top: 10%;
			left: 10%;
			animation-delay: 0s;
		}
		
		.shape:nth-child(2) {
			width: 120px;
			height: 120px;
			top: 20%;
			right: 10%;
			animation-delay: 2s;
		}
		
		.shape:nth-child(3) {
			width: 60px;
			height: 60px;
			bottom: 20%;
			left: 20%;
			animation-delay: 4s;
		}
		
		.shape:nth-child(4) {
			width: 100px;
			height: 100px;
			bottom: 10%;
			right: 20%;
			animation-delay: 1s;
		}
		
		.help-block {
			color: #e74c3c;
			font-size: 12px;
			margin-top: 5px;
		}
		
		.form-group.has-error .form-control {
			border-color: #e74c3c;
			background: #fdf2f2;
		}
		
		.form-group.has-error .form-icon {
			color: #e74c3c;
		}
		
		@media (max-width: 480px) {
			.login-card {
				padding: 30px 20px;
			}
			
			.login-title {
				font-size: 24px;
			}
		}
	</style>
</head>
<body>
	<div class="floating-shapes">
		<div class="shape"></div>
		<div class="shape"></div>
		<div class="shape"></div>
		<div class="shape"></div>
	</div>
	
	<div class="login-container">
		<div class="login-card">
			<div class="login-header">
				<div class="login-logo">
					<i class="fa fa-cloud"></i>
				</div>
				<h1 class="login-title">dao-cloud</h1>
				<p class="login-subtitle">分布式服务中心</p>
			</div>
			
			<form id="loginForm" method="post">
				<div class="form-group">
					<i class="fa fa-user form-icon"></i>
					<input type="text" name="userName" class="form-control" placeholder="请输入登录账号" value="admin" maxlength="18">
				</div>
				
				<div class="form-group">
					<i class="fa fa-lock form-icon"></i>
					<input type="password" name="password" class="form-control" placeholder="请输入登录密码" value="123456" maxlength="18">
				</div>
				
				<div class="remember-me">
					<input type="checkbox" name="ifRemember" id="rememberMe">
					<label for="rememberMe">记住登录状态</label>
				</div>
				
				<button type="submit" class="login-btn">
					<span>登 录</span>
				</button>
			</form>
		</div>
	</div>
	
<@netCommon.commonScript />
<script src="${request.contextPath}/static/plugins/jquery/jquery.validate.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/iCheck/icheck.min.js"></script>
<script src="${request.contextPath}/static/js/login.1.js"></script>

</body>
</html>
