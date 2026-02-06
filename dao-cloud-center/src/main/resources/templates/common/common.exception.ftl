<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>系统错误 - dao-cloud</title>
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/font-awesome/css/font-awesome.min.css">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
            position: relative;
            overflow: hidden;
        }

        /* 背景装饰元素 */
        .floating-shapes {
            position: absolute;
            width: 100%;
            height: 100%;
            overflow: hidden;
            z-index: 0;
        }

        .shape {
            position: absolute;
            opacity: 0.15;
            animation: float 20s infinite ease-in-out;
        }

        .shape:nth-child(1) {
            width: 80px;
            height: 80px;
            background: white;
            border-radius: 50%;
            top: 20%;
            left: 10%;
            animation-delay: 0s;
        }

        .shape:nth-child(2) {
            width: 60px;
            height: 60px;
            background: white;
            border-radius: 30%;
            top: 60%;
            left: 80%;
            animation-delay: 2s;
        }

        .shape:nth-child(3) {
            width: 100px;
            height: 100px;
            background: white;
            border-radius: 20%;
            top: 80%;
            left: 20%;
            animation-delay: 4s;
        }

        @keyframes float {
            0%, 100% {
                transform: translateY(0px) rotate(0deg) scale(1);
            }
            25% {
                transform: translateY(-30px) rotate(90deg) scale(1.1);
            }
            50% {
                transform: translateY(-20px) rotate(180deg) scale(0.9);
            }
            75% {
                transform: translateY(-40px) rotate(270deg) scale(1.05);
            }
        }

        .error-container {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(20px);
            -webkit-backdrop-filter: blur(20px);
            border-radius: 24px;
            padding: 60px 50px;
            max-width: 600px;
            width: 100%;
            box-shadow: 0 25px 50px rgba(0, 0, 0, 0.2),
                        0 15px 35px rgba(0, 0, 0, 0.15),
                        inset 0 1px 0 rgba(255, 255, 255, 0.6),
                        inset 0 -1px 0 rgba(255, 255, 255, 0.2);
            text-align: center;
            animation: slideInUp 0.6s ease-out;
            position: relative;
            z-index: 1;
            border: 1px solid rgba(255, 255, 255, 0.3);
        }

        @keyframes slideInUp {
            from {
                opacity: 0;
                transform: translateY(30px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        .error-icon {
            width: 120px;
            height: 120px;
            background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 30px;
            box-shadow: 0 10px 30px rgba(238, 90, 111, 0.4),
                        0 5px 15px rgba(238, 90, 111, 0.3),
                        inset 0 2px 4px rgba(255, 255, 255, 0.2);
            animation: pulse 2s ease-in-out infinite;
            position: relative;
            overflow: hidden;
        }

        .error-icon::before {
            content: '';
            position: absolute;
            top: -50%;
            left: -50%;
            width: 100%;
            height: 200%;
            background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.1), transparent);
            transform: translateX(-100%) translateY(-100%) rotate(45deg);
            animation: iconShine 3s linear infinite;
        }

        @keyframes iconShine {
            0% {
                transform: translateX(-100%) translateY(-100%) rotate(45deg);
            }
            100% {
                transform: translateX(100%) translateY(100%) rotate(45deg);
            }
        }

        @keyframes pulse {
            0%, 100% {
                transform: scale(1);
                box-shadow: 0 10px 30px rgba(238, 90, 111, 0.4),
                            0 5px 15px rgba(238, 90, 111, 0.3),
                            inset 0 2px 4px rgba(255, 255, 255, 0.2);
            }
            50% {
                transform: scale(1.05);
                box-shadow: 0 15px 40px rgba(238, 90, 111, 0.5),
                            0 8px 20px rgba(238, 90, 111, 0.4),
                            inset 0 2px 4px rgba(255, 255, 255, 0.3);
            }
        }

        .error-icon i {
            font-size: 60px;
            color: white;
            position: relative;
            z-index: 1;
        }

        .error-title {
            font-size: 32px;
            font-weight: 700;
            color: #333;
            margin-bottom: 16px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
        }

        .error-message {
            font-size: 16px;
            color: #666;
            line-height: 1.8;
            margin-bottom: 40px;
            padding: 20px;
            background: rgba(238, 90, 111, 0.05);
            border-radius: 12px;
            border-left: 4px solid #ee5a6f;
            text-align: left;
            word-break: break-word;
            box-shadow: 0 2px 8px rgba(238, 90, 111, 0.1);
        }

        .error-message strong {
            color: #ee5a6f;
            display: block;
            margin-bottom: 8px;
        }

        .error-actions {
            display: flex;
            gap: 16px;
            justify-content: center;
            flex-wrap: wrap;
        }

        .error-btn {
            padding: 14px 32px;
            border-radius: 12px;
            font-size: 16px;
            font-weight: 600;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            cursor: pointer;
            border: none;
            position: relative;
            overflow: hidden;
        }

        .error-btn::before {
            content: '';
            position: absolute;
            top: 50%;
            left: 50%;
            width: 0;
            height: 0;
            border-radius: 50%;
            background: rgba(255, 255, 255, 0.3);
            transform: translate(-50%, -50%);
            transition: width 0.6s, height 0.6s;
        }

        .error-btn:hover::before {
            width: 300px;
            height: 300px;
        }

        .error-btn span {
            position: relative;
            z-index: 1;
        }

        .error-btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4),
                        0 4px 12px rgba(118, 75, 162, 0.3),
                        inset 0 1px 0 rgba(255, 255, 255, 0.3);
        }

        .error-btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 12px 30px rgba(102, 126, 234, 0.5),
                        0 6px 18px rgba(118, 75, 162, 0.4),
                        inset 0 1px 0 rgba(255, 255, 255, 0.4);
        }

        .error-btn-primary:active {
            transform: translateY(0);
        }

        .error-btn-secondary {
            background: white;
            color: #667eea;
            border: 2px solid #667eea;
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
        }

        .error-btn-secondary:hover {
            background: #667eea;
            color: white;
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
        }

        .error-btn-secondary:active {
            transform: translateY(0);
        }

        /* 响应式设计 */
        @media (max-width: 480px) {
            .error-container {
                padding: 40px 30px;
            }

            .error-icon {
                width: 90px;
                height: 90px;
            }

            .error-icon i {
                font-size: 45px;
            }

            .error-title {
                font-size: 24px;
            }

            .error-message {
                font-size: 14px;
                padding: 16px;
            }

            .error-actions {
                flex-direction: column;
            }

            .error-btn {
                width: 100%;
                justify-content: center;
            }
        }
    </style>
</head>
<body>
    <div class="floating-shapes">
        <div class="shape"></div>
        <div class="shape"></div>
        <div class="shape"></div>
    </div>

    <div class="error-container">
        <div class="error-icon">
            <i class="fa fa-exclamation-triangle"></i>
        </div>
        <h1 class="error-title">系统错误</h1>
        <div class="error-message">
            <strong>错误详情：</strong>
            ${exceptionMsg}
        </div>
        <div class="error-actions">
            <a href="javascript:history.back()" class="error-btn error-btn-secondary">
                <i class="fa fa-arrow-left"></i>
                <span>返回上一页</span>
            </a>
            <a href="${request.contextPath}/" class="error-btn error-btn-primary">
                <i class="fa fa-home"></i>
                <span>返回首页</span>
            </a>
        </div>
    </div>
</body>
</html>
