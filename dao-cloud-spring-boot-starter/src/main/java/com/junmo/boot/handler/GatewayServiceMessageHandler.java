package com.junmo.boot.handler;

import com.junmo.boot.bootstrap.manager.ServiceManager;
import com.junmo.boot.bootstrap.unit.ServiceInvoker;
import com.junmo.core.exception.DaoException;
import com.junmo.core.model.*;
import com.junmo.core.resolver.MethodArgumentResolverHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;

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
public class GatewayServiceMessageHandler extends SimpleChannelInboundHandler<GatewayRequestModel> {

    private MethodArgumentResolverHandler methodArgumentResolverHandler;

    public GatewayServiceMessageHandler(MethodArgumentResolverHandler methodArgumentResolverHandler) {
        this.methodArgumentResolverHandler = methodArgumentResolverHandler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GatewayRequestModel gatewayRequestModel) {
        RpcRequestModel rpcRequestModel = wrapper(gatewayRequestModel);
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
            .orElseThrow(() -> new DaoException(String.format("未找到服务：%s#%s", clss.getName(), methodName)));
        Parameter[] parameters = method.getParameters();
        HttpServletResponse httpServletResponse = this.createDefaultResponse();
        if(Objects.isNull(parameters) || parameters.length == 0) {
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

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // todo
    }
}
