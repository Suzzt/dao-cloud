package com.dao.cloud.gateway.global;

import com.dao.cloud.core.ApiResult;
import com.dao.cloud.core.enums.CodeEnum;
import com.dao.cloud.core.exception.DaoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2024/2/20 15:05
 * Gateway global exception handling and unified response
 */
@Slf4j
@ControllerAdvice
public class GlobalGatewayExceptionHandler {

    /**
     * 网关异常处理
     *
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(value = DaoException.class)
    @ResponseBody
    public ApiResult daoExceptionHandler(HttpServletRequest request, DaoException e) {
        log.error("Gateway request processing exception. request method={}", request.getMethod(), e);
        return ApiResult.buildFail(e.getCode(), e.getMessage());
    }

    /**
     * 非法异常
     *
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(value = Throwable.class)
    @ResponseBody
    public ApiResult unknownExceptionHandler(HttpServletRequest request, Throwable e) {
        log.error("An accident occurred at the gateway. request method={}", request.getMethod(), e);
        return ApiResult.buildFail(CodeEnum.GATEWAY_REQUEST_ERROR.getCode(), e.getMessage());
    }

}
