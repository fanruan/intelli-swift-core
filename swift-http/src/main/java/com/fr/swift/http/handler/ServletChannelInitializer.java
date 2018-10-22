package com.fr.swift.http.handler;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.http.dispatcher.SwiftDispatcher;
import com.fr.swift.http.servlet.MockServletConfig;
import com.fr.swift.http.servlet.MockServletContext;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.util.Crasher;
import com.fr.third.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import com.fr.third.springframework.web.servlet.DispatcherServlet;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

import javax.servlet.ServletException;

/**
 * This class created on 2018/6/14
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ServletChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final DispatcherServlet dispatcherServlet;
    private SwiftProperty swiftProperty;

    public ServletChannelInitializer() throws ServletException {
        swiftProperty = SwiftContext.get().getBean("swiftProperty", SwiftProperty.class);
        AnnotationConfigWebApplicationContext wac = initWebContext();
        this.dispatcherServlet = new SwiftDispatcher(wac);
        this.dispatcherServlet.init(wac.getServletConfig());
    }

    private static AnnotationConfigWebApplicationContext initWebContext() {
        try {
            MockServletContext servletContext = new MockServletContext();
            MockServletConfig servletConfig = new MockServletConfig(servletContext);
            AnnotationConfigWebApplicationContext wac = new AnnotationConfigWebApplicationContext();
            wac.setServletContext(servletContext);
            wac.setServletConfig(servletConfig);
            wac.register(Class.forName("com.fr.swift.boot.SwiftWebContextConfiguration"));
            wac.refresh();
            return wac;
        } catch (ClassNotFoundException e) {
            return Crasher.crash(e);
        }
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ch.pipeline().addLast("decoder", new HttpRequestDecoder());
        ch.pipeline().addLast("aggregator", new HttpObjectAggregator(swiftProperty.getRpcMaxObjectSize()));
        ch.pipeline().addLast("encoder", new HttpResponseEncoder());
        ch.pipeline().addLast("chunkedWriter", new ChunkedWriteHandler());
        ch.pipeline().addLast("httpServerHandler", new HttpServerHandler(dispatcherServlet));
    }
}
