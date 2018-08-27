package com.fr.swift.netty.rpc.server;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.netty.rpc.exception.ServiceInvalidException;
import com.fr.swift.rpc.bean.RpcRequest;
import com.fr.swift.rpc.bean.RpcResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * This class created on 2018/6/6
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(RpcServerHandler.class);

    private final Map<String, Object> handlerMap;
    private final Map<String, Object> externalMap;

    public RpcServerHandler(Map<String, Object> handlerMap, Map<String, Object> externalMap) {
        this.handlerMap = handlerMap;
        this.externalMap = externalMap;
    }

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, final RpcRequest request) {
        LOGGER.debug("Receive request " + request.getRequestId());
        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());
        try {
            Object result = handle(request);
            response.setResult(result);
        } catch (Throwable e) {
            LOGGER.error("handle result failure", e);
            response.setException(e);
        }
        ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) {
                LOGGER.debug("Send response for request " + request.getRequestId());
            }
        });
    }

    private Object handle(RpcRequest request) throws Exception {
        String serviceName = request.getInterfaceName();
        Object serviceBean = null;
        switch (request.requestType()) {
            case EXTERNAL:
                serviceBean = externalMap.get(serviceName);
                break;
            default:
                serviceBean = handlerMap.get(serviceName);
        }
        if (serviceBean == null) {
            throw new ServiceInvalidException(serviceName + " is invalid on remote machine!");
        }
        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();
        Method method = serviceClass.getMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(serviceBean, parameters);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("server caught exception", cause);
        ctx.close();
    }
}
