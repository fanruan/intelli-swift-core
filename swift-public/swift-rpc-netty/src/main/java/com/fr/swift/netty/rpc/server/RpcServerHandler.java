package com.fr.swift.netty.rpc.server;

import com.fr.swift.annotation.SwiftApi;
import com.fr.swift.basics.base.ProxyServiceRegistry;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.netty.bean.InternalRpcRequest;
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

    public RpcServerHandler(Map<String, Object> handlerMap, Map<String, Object> externalMap) {
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
        switch (request.requestType()) {
            case INTERNAL:
                if (request instanceof InternalRpcRequest) {
                    return handle(request, ProxyServiceRegistry.get().getInternalService(serviceName), false);
                } else {
                    throw new ServiceInvalidException(serviceName + " is invalid on remote machine!");
                }
            default:
                return handle(request, ProxyServiceRegistry.get().getExternalService(serviceName), true);

        }
    }

    private Object handle(RpcRequest request, Object serviceBean, boolean checkApiEnable) throws Exception {
        if (serviceBean == null) {
            throw new ServiceInvalidException(request.getInterfaceName() + " is invalid on remote machine!");
        }
        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();
        Method method = serviceClass.getMethod(methodName, parameterTypes);
        if (checkApiEnable) {
            checkApiEnable(methodName, method);
        }
        method.setAccessible(true);
        return method.invoke(serviceBean, parameters);
    }

    private void checkApiEnable(String methodName, Method method) throws ServiceInvalidException {
        SwiftApi api = method.getAnnotation(SwiftApi.class);
        if (null != api && !api.enable()) {
            throw new ServiceInvalidException(methodName + " is invalid on remote machine");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("server caught exception", cause);
        ctx.close();
    }
}
