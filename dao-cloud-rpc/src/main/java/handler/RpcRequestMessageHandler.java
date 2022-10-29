package handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import model.RpcRequestModel;
import model.RpcResponseModel;
import test.TestService;
import test.TestServiceImpl;

import java.lang.reflect.Method;

/**
 * @author: sucf
 * @date: 2022/10/29 10:28
 * @description:
 */
@Slf4j
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestModel> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestModel message) {
        RpcResponseModel response = new RpcResponseModel();
        response.setSequenceId(message.getSequenceId());
        try {
            //先写死测试下 // TODO: 2022/10/29  要找到它的实现类
            TestService service = new TestServiceImpl();
            Method method = service.getClass().getMethod(message.getMethodName(), message.getParameterTypes());
            Object invoke = method.invoke(service, message.getParameterValue());
            response.setReturnValue(invoke);
        } catch (Exception e) {
            e.printStackTrace();
            String msg = e.getCause().getMessage();
            response.setExceptionValue(new Exception("execute rpc error:" + msg));
        }
        ctx.writeAndFlush(response);
    }

}
