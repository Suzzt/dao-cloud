package handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import model.DaoMessage;
import model.RpcRequestModel;
import model.RpcResponseModel;
import test.TestService;
import test.TestServiceImpl;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

/**
 * @author: sucf
 * @date: 2022/10/29 10:28
 * @description:
 */
@Slf4j
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestModel> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestModel rpcRequestModel) {
        RpcResponseModel rpcResponseModel = new RpcResponseModel();
        rpcResponseModel.setSequenceId(rpcRequestModel.getSequenceId());
        try {
            //先写死测试下 // TODO: 2022/10/29  要找到它的实现类
            TestService service = new TestServiceImpl();
            Method method = service.getClass().getMethod(rpcRequestModel.getMethodName(), rpcRequestModel.getParameterTypes());
            Object invoke = method.invoke(service, rpcRequestModel.getParameterValue());
            rpcResponseModel.setReturnValue(invoke);

        } catch (Exception e) {
            e.printStackTrace();
            String msg = e.getCause().getMessage();
            rpcResponseModel.setExceptionValue(new Exception("execute rpc error:" + msg));
        }
        DaoMessage daoMessage = new DaoMessage("dao".getBytes(StandardCharsets.UTF_8), (byte) 1, (byte) 1, (byte) 0, rpcResponseModel);
        ctx.writeAndFlush(daoMessage);
    }

}
