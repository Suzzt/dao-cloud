package com.dao.cloud.center.web.interceptor;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2023/07/29 17:22
 * 权限具体拦截实现
 */
@Component
public class PermissionInterceptor extends HandlerInterceptorAdapter implements InitializingBean {

    /**
     * 用户名
     */
    @Value(value = "${dao-cloud.center.admin-web.username:#{admin}}")
    private String username;

    /**
     * 密码
     */
    @Value(value = "${dao-cloud.center.admin-web.password:#{123456}}")
    private String password;

    @Override
    public void afterPropertiesSet() {

        // valid
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new RuntimeException("权限账号密码不可为空");
        }

        // login token
        String tokenTmp = DigestUtils.md5DigestAsHex((username + "_" + password).getBytes());
        tokenTmp = new BigInteger(1, tokenTmp.getBytes()).toString(16);

        LOGIN_IDENTITY_TOKEN = tokenTmp;
    }

    public static final String LOGIN_IDENTITY_KEY = "DAO_CLOUD_MQ_LOGIN_IDENTITY";
    private static String LOGIN_IDENTITY_TOKEN;

    public static String getLoginIdentityToken() {
        return LOGIN_IDENTITY_TOKEN;
    }

    public static boolean login(HttpServletResponse response, String username, String password, boolean ifRemember) {

        // login token
        String tokenTmp = DigestUtils.md5DigestAsHex((username + "_" + password).getBytes());
        tokenTmp = new BigInteger(1, tokenTmp.getBytes()).toString(16);

        if (!getLoginIdentityToken().equals(tokenTmp)) {
            return false;
        }

        // do login
        CookieUtil.set(response, LOGIN_IDENTITY_KEY, getLoginIdentityToken(), ifRemember);
        return true;
    }

    public static void logout(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.remove(request, response, LOGIN_IDENTITY_KEY);
    }

    public static boolean ifLogin(HttpServletRequest request) {
        String indentityInfo = CookieUtil.getValue(request, LOGIN_IDENTITY_KEY);
        if (indentityInfo == null || !getLoginIdentityToken().equals(indentityInfo.trim())) {
            return false;
        }
        return true;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return super.preHandle(request, response, handler);
        }
        if (!ifLogin(request)) {
            HandlerMethod method = (HandlerMethod) handler;
            Permissions permission = method.getMethodAnnotation(Permissions.class);
            if (permission == null || permission.limit()) {
                response.sendRedirect(request.getContextPath() + "/dao-cloud/toLogin");
                return false;
            }
        }
        return super.preHandle(request, response, handler);
    }
}
