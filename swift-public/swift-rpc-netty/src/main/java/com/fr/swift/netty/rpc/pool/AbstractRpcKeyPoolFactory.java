package com.fr.swift.netty.rpc.pool;

import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.netty.rpc.client.AbstractRpcClientHandler;
import com.fr.swift.property.SwiftProperty;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;

/**
 * This class created on 2018/8/1
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public abstract class AbstractRpcKeyPoolFactory<T extends AbstractRpcClientHandler> extends BaseKeyedPooledObjectFactory<String, T> {

    private SwiftProperty swiftProperty = SwiftProperty.getProperty();


    protected ChannelFuture bindBootstrap(final AbstractRpcClientHandler clientHandler) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup(1);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel channel) {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addLast(
                        new ObjectDecoder(swiftProperty.getRpcMaxObjectSize(), ClassResolvers.cacheDisabled(this
                                .getClass().getClassLoader())));
                pipeline.addLast(new ObjectEncoder());
                pipeline.addLast(clientHandler);
            }
        });
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        clientHandler.setGroup(group);
        return bootstrap.connect(clientHandler.getHost(), clientHandler.getPort()).sync();
    }

    @Override
    public void destroyObject(String key, PooledObject<T> pooledObject) throws Exception {
        super.destroyObject(key, pooledObject);
        pooledObject.getObject().shutdown();
        SwiftLoggers.getLogger().debug("Destroy idle object end! [key:" + key + "]");
    }
}
