package com.junmo.boot.handler;

import com.junmo.boot.bootstrap.manager.ServiceManager;
import com.junmo.boot.bootstrap.unit.ServiceInvoker;
import com.junmo.core.enums.CodeEnum;
import com.junmo.core.exception.NoMatchMethodException;
import com.junmo.core.model.*;
import com.junmo.core.netty.protocol.DaoMessage;
import com.junmo.core.netty.protocol.MessageType;
import com.junmo.core.resolver.MethodArgumentResolverHandler;
import com.junmo.core.util.DaoCloudConstant;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: sucf
 * @date: 2024/1/30 11:24
 * @description: 网关请求处理响应, 该请求会打到rpc服务调用的handler上
 */
@Slf4j
public class GatewayServiceMessageHandler extends SimpleChannelInboundHandler<GatewayRequestModel> {

    private MethodArgumentResolverHandler methodArgumentResolverHandler;

    public GatewayServiceMessageHandler(MethodArgumentResolverHandler methodArgumentResolverHandler) {
        this.methodArgumentResolverHandler = methodArgumentResolverHandler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GatewayRequestModel gatewayRequestModel) {
        RpcRequestModel rpcRequestModel;
        try {
            rpcRequestModel = wrapper(gatewayRequestModel);
        } catch (NoMatchMethodException e) {
            log.error("网关请求方法不存在", e);
            RpcResponseModel responseModel = RpcResponseModel.builder(gatewayRequestModel.getSequenceId(), CodeEnum.GATEWAY_SERVICE_NOT_EXIST);
            DaoMessage daoMessage = new DaoMessage((byte) 1, MessageType.SERVICE_RPC_RESPONSE_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, responseModel);
            ctx.writeAndFlush(daoMessage).addListener((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    log.error("<<<<<<<<<< Request result failed! Sending data to the gateway also failed. >>>>>>>>>>", future.cause());
                }
            });
            return;
        } catch (Exception e) {
            log.error("网关参数绑定失败", e);
            RpcResponseModel responseModel = RpcResponseModel.builder(gatewayRequestModel.getSequenceId(), CodeEnum.GATEWAY_PARAM_PROCESS_BINDING_FAILED);
            DaoMessage daoMessage = new DaoMessage((byte) 1, MessageType.SERVICE_RPC_RESPONSE_MESSAGE, DaoCloudConstant.DEFAULT_SERIALIZE, responseModel);
            ctx.writeAndFlush(daoMessage).addListener((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    log.error("<<<<<<<<<< Request result failed! Sending data to the gateway also failed. >>>>>>>>>>", future.cause());
                }
            });
            return;
        }
        // 将加工后的对象传递到下一个rpc处理器
        ctx.fireChannelRead(rpcRequestModel);
    }

    /**
     * 包装处理网关请求到服务调用请求
     *
     * @param gatewayRequestModel
     * @return
     */
    private RpcRequestModel wrapper(GatewayRequestModel gatewayRequestModel) {

        ServiceInvoker serviceInvoker = ServiceManager.getServiceInvoker(gatewayRequestModel.getProvider(),
                gatewayRequestModel.getVersion());
        Object serviceBean = serviceInvoker.getServiceBean();
        HttpParameterBinderResult binderResult = this.binderHttpArgs(serviceBean.getClass(), gatewayRequestModel.getMethodName(), gatewayRequestModel.getRequest());

        //String provider, int version, String methodName, Class[] parameterTypes, Object[] parameterValue, Class<?> returnType
        RpcRequestModel requestModel = new RpcRequestModel(
                gatewayRequestModel.getProvider(),
                gatewayRequestModel.getVersion(),
                gatewayRequestModel.getMethodName(),
                binderResult.getParameterTypes(),
                binderResult.getParameterValues(),
                binderResult.getReturnType()
        );
        requestModel.setSequenceId(gatewayRequestModel.getSequenceId());
        requestModel.setHttp(true);
        requestModel.setHttpServletResponse(binderResult.getHttpServletResponse());
        return requestModel;
    }

    private HttpParameterBinderResult binderHttpArgs(Class<?> clss, String methodName, HttpServletRequestModel httpServletRequest) {

        Method[] methods = clss.getMethods();
        // 目前http请求的方式不支持重载，后续有空再迭代
        Method method = Arrays.stream(methods).filter(m -> m.getName().equals(methodName)).findFirst()
                .orElseThrow(() -> new NoMatchMethodException());
        Parameter[] parameters = method.getParameters();
        HttpServletResponse httpServletResponse = this.createDefaultResponse();
        if (Objects.isNull(parameters) || parameters.length == 0) {
            HttpParameterBinderResult result = new HttpParameterBinderResult();
            result.setReturnType(method.getReturnType());
            result.setHttpServletResponse(httpServletResponse);
            result.setParameterTypes(new Class<?>[0]);
            result.setParameterValues(new Object[0]);
            return result;
        }
        List<Object> parameterValueList = Arrays.stream(parameters)
                .map(parameter -> methodArgumentResolverHandler.resolver(parameter, httpServletRequest, httpServletResponse))
                .collect(Collectors.toList());

        HttpParameterBinderResult result = new HttpParameterBinderResult();
        result.setParameterTypes(Arrays.stream(parameters).map(Parameter::getType)
                .collect(Collectors.toList()).toArray(new Class<?>[0]));
        result.setParameterValues(parameterValueList.toArray());
        result.setReturnType(method.getReturnType());
        result.setHttpServletResponse(httpServletResponse);
        return result;
    }

    private HttpServletResponse createDefaultResponse() {
        HttpServletResponse httpServletResponse = new HttpServletResponse();
        httpServletResponse.addHeader(HttpHeaderNames.CONTENT_TYPE.toString(),
                HttpHeaderValues.APPLICATION_JSON + ";charset=UTF-8");
        httpServletResponse.addHeader(HttpHeaderNames.CONTENT_LENGTH.toString(), "0");
        return httpServletResponse;
    }
}
