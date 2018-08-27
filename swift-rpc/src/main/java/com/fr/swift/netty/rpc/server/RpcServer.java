package com.fr.swift.netty.rpc.server;

import com.fr.swift.annotation.RpcMethod;
import com.fr.swift.annotation.RpcService;
import com.fr.swift.annotation.RpcServiceType;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.netty.rpc.registry.ServiceRegistry;
import com.fr.swift.property.SwiftProperty;
import com.fr.third.jodd.util.StringUtil;
import com.fr.third.org.apache.commons.collections4.MapUtils;
import com.fr.third.springframework.beans.BeansException;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.context.ApplicationContext;
import com.fr.third.springframework.stereotype.Service;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * This class created on 2018/6/6
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Service
public class RpcServer {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(RpcServer.class);

    private String serviceAddress;

    private ServiceRegistry serviceRegistry;

    private SwiftProperty swiftProperty;

    /**
     * key:服务名
     * value:服务对象
     */
    private Map<String, Object> handlerMap = new HashMap<String, Object>();
    private Map<String, Object> externalMap = new HashMap<String, Object>();
    private Map<String, Method> methodMap = new HashMap<String, Method>();

    @Autowired
    public RpcServer(ServiceRegistry serviceRegistry) {
        swiftProperty = SwiftContext.get().getBean("swiftProperty", SwiftProperty.class);
        this.serviceAddress = swiftProperty.getServerAddress();
        this.serviceRegistry = serviceRegistry;
    }

    public void initService(ApplicationContext ctx) throws BeansException {
        // 扫描service和method
        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);
        if (MapUtils.isNotEmpty(serviceBeanMap)) {
            for (Object serviceBean : serviceBeanMap.values()) {
                RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);
                String serviceName = rpcService.value().getName();
                RpcServiceType rpcServiceType = rpcService.type();
                LOGGER.debug("Load service:" + serviceName);
                if (rpcServiceType == RpcServiceType.EXTERNAL) {
                    externalMap.put(serviceName, serviceBean);
                } else {
                    handlerMap.put(serviceName, serviceBean);
                }
                for (Method method : serviceBean.getClass().getMethods()) {
                    RpcMethod rpcMethod = method.getAnnotation(RpcMethod.class);
                    if (rpcMethod != null) {
                        LOGGER.debug("Load method:" + method.getName());
                        methodMap.put(rpcMethod.methodName(), method);
                    }
                }
            }
        }
    }

    public void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel channel) {
                    ChannelPipeline pipeline = channel.pipeline();
                    pipeline.addLast(
                            new ObjectDecoder(swiftProperty.getRpcMaxObjectSize(), ClassResolvers
                                    .weakCachingConcurrentResolver(this.getClass()
                                            .getClassLoader())));
                    pipeline.addLast(new ObjectEncoder());
                    pipeline.addLast(new RpcServerHandler(handlerMap, externalMap)); // 处理 RPC 请求
                }
            });
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            String[] addressArray = StringUtil.split(serviceAddress, ":");
            String ip = addressArray[0];
            int port = Integer.parseInt(addressArray[1]);
            ChannelFuture future = bootstrap.bind(ip, port).sync();
            if (serviceRegistry != null) {
                for (String interfaceName : handlerMap.keySet()) {
                    serviceRegistry.register(interfaceName, serviceAddress);
                }
            }
            LOGGER.info("RPC srver started on ip:" + ip + ", port :" + port);
            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public Method getMethodByName(String name) {
        return methodMap.get(name);
    }

    public Map<String, Method> getMethodNames() {
        return new HashMap<String, Method>(methodMap);
    }
}
