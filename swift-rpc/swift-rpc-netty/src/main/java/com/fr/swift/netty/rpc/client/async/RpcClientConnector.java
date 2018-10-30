//package com.fr.swift.netty.rpc.client.async;
//
//import com.fr.swift.log.SwiftLogger;
//import com.fr.swift.log.SwiftLoggers;
//import io.netty.bootstrap.Bootstrap;
//import io.netty.channel.ChannelFuture;
//import io.netty.channel.ChannelFutureListener;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.ChannelPipeline;
//import io.netty.channel.EventLoopGroup;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.channel.socket.nio.NioSocketChannel;
//import io.netty.handler.codec.serialization.ClassResolvers;
//import io.netty.handler.codec.serialization.ObjectDecoder;
//import io.netty.handler.codec.serialization.ObjectEncoder;
//
//import java.net.InetSocketAddress;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.CopyOnWriteArrayList;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.concurrent.locks.Condition;
//import java.util.concurrent.locks.ReentrantLock;
//
///**
// * This class created on 2018/6/11
// *
// * @author Lucifer
// * @description
// * @since Advanced FineBI 5.0
// * todo 考虑长连接再做。
// */
//public class RpcClientConnector {
//
//    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(AsyncRpcClientHandler.class);
//
//    private static ExecutorService executorService = Executors.newFixedThreadPool(16);
//
//    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);
//
//    private CopyOnWriteArrayList<AsyncRpcClientHandler> connectedHandlers = new CopyOnWriteArrayList<AsyncRpcClientHandler>();
//
//    private Map<InetSocketAddress, AsyncRpcClientHandler> connectedServerNodes = new ConcurrentHashMap<InetSocketAddress, AsyncRpcClientHandler>();
//
//    private ReentrantLock lock = new ReentrantLock();
//    private Condition connected = lock.newCondition();
//    private long connectTimeoutMillis = 6000;
//    private AtomicInteger roundRobin = new AtomicInteger(0);
//    private volatile boolean isRuning = true;
//    private volatile static RpcClientConnector clientConnector;
//
//    private RpcClientConnector() {
//
//    }
//
//    public static RpcClientConnector getInstance() {
//        if (clientConnector == null) {
//            synchronized (RpcClientConnector.class) {
//                if (clientConnector == null) {
//                    clientConnector = new RpcClientConnector();
//                }
//            }
//        }
//        return clientConnector;
//    }
//
//    public void connectServerNode(final InetSocketAddress remotePeer) {
//        executorService.submit(new Runnable() {
//            @Override
//            public void run() {
//                Bootstrap bootstrap = new Bootstrap();
//                bootstrap.group(eventLoopGroup)
//                        .channel(NioSocketChannel.class);
//
//                bootstrap.handler(new ChannelInitializer<SocketChannel>() {
//                    @Override
//                    public void initChannel(SocketChannel channel) throws Exception {
//                        ChannelPipeline pipeline = channel.pipeline();
//                        pipeline.addLast(
//                                new ObjectDecoder(1024, ClassResolvers.cacheDisabled(this
//                                        .getClass().getClassLoader())));
//                        pipeline.addLast(new ObjectEncoder());
//                        pipeline.addLast(new AsyncRpcClientHandler()); // 处理 RPC 响应
//                    }
//                });
//
//                ChannelFuture channelFuture = bootstrap.connect(remotePeer);
//                channelFuture.addListener(new ChannelFutureListener() {
//                    @Override
//                    public void operationComplete(final ChannelFuture channelFuture) throws Exception {
//                        if (channelFuture.isSuccess()) {
//                            LOGGER.info("Successfully connect to remote server. remote peer = " + remotePeer);
//                            AsyncRpcClientHandler handler = channelFuture.channel().pipeline().get(AsyncRpcClientHandler.class);
//                            addHandler(handler);
//                        }
//                    }
//                });
//            }
//        });
//    }
//
//    private void addHandler(AsyncRpcClientHandler handler) {
//        connectedHandlers.add(handler);
//        InetSocketAddress remoteAddress = (InetSocketAddress) handler.getChannel().remoteAddress();
//        connectedServerNodes.put(remoteAddress, handler);
//        signalAvailableHandler();
//    }
//
//    private void signalAvailableHandler() {
//        lock.lock();
//        try {
//            connected.signalAll();
//        } finally {
//            lock.unlock();
//        }
//    }
//
//    private boolean waitingForHandler() throws InterruptedException {
//        lock.lock();
//        try {
//            return connected.await(this.connectTimeoutMillis, TimeUnit.MILLISECONDS);
//        } finally {
//            lock.unlock();
//        }
//    }
//
//    public AsyncRpcClientHandler chooseHandler() {
//        int size = connectedHandlers.size();
//        while (isRuning && size <= 0) {
//            try {
//                boolean available = waitingForHandler();
//                if (available) {
//                    size = connectedHandlers.size();
//                }
//            } catch (InterruptedException e) {
//                LOGGER.error("Waiting for available node is interrupted! ", e);
//                throw new RuntimeException("Can't connect any servers!", e);
//            }
//        }
//        int index = (roundRobin.getAndAdd(1) + size) % size;
//        return connectedHandlers.get(index);
//    }
//
//    public void stop() {
//        isRuning = false;
//        for (int i = 0; i < connectedHandlers.size(); ++i) {
//            AsyncRpcClientHandler connectedServerHandler = connectedHandlers.get(i);
//            connectedServerHandler.close();
//        }
//        signalAvailableHandler();
//        executorService.shutdown();
//        eventLoopGroup.shutdownGracefully();
//    }
//}
