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
			background: linear-gradient(135deg, rgba(255, 255, 255, 0.25), rgba(255, 255, 255, 0.1));
			backdrop-filter: blur(20px);
			-webkit-backdrop-filter: blur(20px);
			border: 1px solid rgba(255, 255, 255, 0.3);
			border-radius: 24px;
			padding: 45px;
			box-shadow: 
				0 25px 50px rgba(0, 0, 0, 0.15),
				0 15px 35px rgba(0, 0, 0, 0.1),
				inset 0 1px 0 rgba(255, 255, 255, 0.6),
				inset 0 -1px 0 rgba(255, 255, 255, 0.2);
			width: 420px;
			max-width: 90vw;
			position: relative;
			z-index: 2;
			animation: slideInUp 0.8s ease-out, cardFloat 6s ease-in-out infinite;
			overflow: hidden;
		}
		
		.login-card::before {
			content: '';
			position: absolute;
			top: 0;
			left: -100%;
			width: 100%;
			height: 100%;
			background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
			animation: cardShine 4s ease-in-out infinite;
		}
		
		@keyframes cardFloat {
			0%, 100% { transform: translateY(0px); }
			50% { transform: translateY(-10px); }
		}
		
		@keyframes cardShine {
			0% { left: -100%; }
			50% { left: 100%; }
			100% { left: 100%; }
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
			width: 90px;
			height: 90px;
			margin: 0 auto 20px;
			background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
			border-radius: 50%;
			display: flex;
			align-items: center;
			justify-content: center;
			box-shadow: 
				0 20px 40px rgba(102, 126, 234, 0.4),
				0 8px 16px rgba(118, 75, 162, 0.3),
				inset 0 2px 4px rgba(255, 255, 255, 0.2);
			position: relative;
			overflow: hidden;
			animation: logoGlow 2s ease-in-out infinite alternate;
		}
		
		.login-logo::before {
			content: '';
			position: absolute;
			top: -50%;
			left: -50%;
			width: 200%;
			height: 200%;
			background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.3), transparent);
			transform: rotate(45deg);
			animation: logoShine 3s linear infinite;
		}
		
		.login-logo i {
			font-size: 42px;
			color: white;
			text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
			z-index: 2;
			position: relative;
		}
		
		@keyframes logoGlow {
			0% {
				box-shadow: 
					0 20px 40px rgba(102, 126, 234, 0.4),
					0 8px 16px rgba(118, 75, 162, 0.3),
					inset 0 2px 4px rgba(255, 255, 255, 0.2);
			}
			100% {
				box-shadow: 
					0 25px 50px rgba(102, 126, 234, 0.6),
					0 12px 24px rgba(118, 75, 162, 0.5),
					inset 0 2px 4px rgba(255, 255, 255, 0.4);
			}
		}
		
		@keyframes logoShine {
			0% { transform: translateX(-100%) translateY(-100%) rotate(45deg); }
			100% { transform: translateX(100%) translateY(100%) rotate(45deg); }
		}
		
		.login-title {
			font-size: 28px;
			font-weight: 700;
			margin-bottom: 5px;
			background: linear-gradient(
				45deg, 
				#ff0000 0%, 
				#ff7f00 14%, 
				#ffff00 28%, 
				#00ff00 42%, 
				#0000ff 57%, 
				#4b0082 71%, 
				#9400d3 85%, 
				#ff0000 100%
			);
			background-size: 200% 100%;
			-webkit-background-clip: text;
			-webkit-text-fill-color: transparent;
			background-clip: text;
			animation: rainbowFlow 3s ease-in-out infinite;
			text-shadow: 0 0 30px rgba(255, 255, 255, 0.5);
		}
		
		@keyframes rainbowFlow {
			0%, 100% { background-position: 0% 50%; }
			50% { background-position: 100% 50%; }
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
			height: 54px;
			border: 1px solid rgba(255, 255, 255, 0.3);
			background: linear-gradient(135deg, rgba(255, 255, 255, 0.2), rgba(255, 255, 255, 0.05));
			backdrop-filter: blur(10px);
			-webkit-backdrop-filter: blur(10px);
			border-radius: 16px;
			padding: 0 24px 0 54px;
			font-size: 16px;
			color: #333;
			transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
			box-shadow: 
				inset 0 1px 0 rgba(255, 255, 255, 0.4),
				0 2px 8px rgba(0, 0, 0, 0.05);
		}
		
		.form-control:focus {
			border-color: rgba(102, 126, 234, 0.6);
			background: linear-gradient(135deg, rgba(255, 255, 255, 0.4), rgba(255, 255, 255, 0.1));
			box-shadow: 
				inset 0 1px 0 rgba(255, 255, 255, 0.6),
				0 0 0 4px rgba(102, 126, 234, 0.15),
				0 8px 25px rgba(102, 126, 234, 0.2);
			outline: none;
			transform: translateY(-2px);
		}
		
		.form-control::placeholder {
			color: rgba(51, 51, 51, 0.6);
			font-weight: 500;
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
			height: 54px;
			background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
			border: none;
			border-radius: 16px;
			color: white;
			font-size: 17px;
			font-weight: 700;
			cursor: pointer;
			transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
			position: relative;
			overflow: hidden;
			letter-spacing: 0.5px;
			text-transform: uppercase;
			box-shadow: 
				0 8px 25px rgba(102, 126, 234, 0.4),
				0 4px 12px rgba(118, 75, 162, 0.3),
				inset 0 1px 0 rgba(255, 255, 255, 0.3);
		}
		
		.login-btn::before {
			content: '';
			position: absolute;
			top: 0;
			left: -100%;
			width: 100%;
			height: 100%;
			background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
			transition: left 0.5s ease;
		}
		
		.login-btn:hover {
			transform: translateY(-4px) scale(1.02);
			box-shadow: 
				0 15px 40px rgba(102, 126, 234, 0.6),
				0 8px 25px rgba(118, 75, 162, 0.4),
				inset 0 1px 0 rgba(255, 255, 255, 0.4);
		}
		
		.login-btn:hover::before {
			left: 100%;
		}
		
		.login-btn:active {
			transform: translateY(-2px) scale(1.01);
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
			background: linear-gradient(135deg, rgba(255, 255, 255, 0.15), rgba(255, 255, 255, 0.05));
			backdrop-filter: blur(5px);
			-webkit-backdrop-filter: blur(5px);
			border: 1px solid rgba(255, 255, 255, 0.2);
			animation: float 8s ease-in-out infinite;
			box-shadow: 
				0 10px 30px rgba(255, 255, 255, 0.1),
				inset 0 1px 0 rgba(255, 255, 255, 0.3);
		}
		
		@keyframes float {
			0%, 100% { 
				transform: translateY(0px) rotate(0deg) scale(1); 
				opacity: 0.8;
			}
			25% { 
				transform: translateY(-30px) rotate(90deg) scale(1.1); 
				opacity: 0.9;
			}
			50% { 
				transform: translateY(-20px) rotate(180deg) scale(0.9); 
				opacity: 1;
			}
			75% { 
				transform: translateY(-40px) rotate(270deg) scale(1.05); 
				opacity: 0.9;
			}
		}
		
		.shape:nth-child(1) {
			width: 100px;
			height: 100px;
			top: 8%;
			left: 8%;
			animation-delay: 0s;
		}
		
		.shape:nth-child(2) {
			width: 140px;
			height: 140px;
			top: 15%;
			right: 8%;
			animation-delay: 2.5s;
		}
		
		.shape:nth-child(3) {
			width: 80px;
			height: 80px;
			bottom: 25%;
			left: 15%;
			animation-delay: 5s;
		}
		
		.shape:nth-child(4) {
			width: 120px;
			height: 120px;
			bottom: 8%;
			right: 15%;
			animation-delay: 1.5s;
		}
		
		.shape:nth-child(5) {
			width: 60px;
			height: 60px;
			top: 45%;
			left: 5%;
			animation-delay: 3.5s;
		}
		
		.shape:nth-child(6) {
			width: 90px;
			height: 90px;
			top: 60%;
			right: 5%;
			animation-delay: 4.5s;
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
