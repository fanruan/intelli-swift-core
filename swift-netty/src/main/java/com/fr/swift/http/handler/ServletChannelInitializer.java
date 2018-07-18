package com.fr.swift.http.handler;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.http.dispatcher.SwiftDispatcher;
import com.fr.swift.http.servlet.MockServletConfig;
import com.fr.swift.http.servlet.MockServletContext;
import com.fr.third.springframework.web.context.support.XmlWebApplicationContext;
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

    public ServletChannelInitializer() throws ServletException {

        MockServletContext servletContext = new MockServletContext();
        MockServletConfig servletConfig = new MockServletConfig(servletContext);
        XmlWebApplicationContext wac = new XmlWebApplicationContext();
        wac.setServletContext(servletContext);
        wac.setServletConfig(servletConfig);
        wac.setParent(SwiftContext.get());
        wac.setConfigLocation("classpath:swift-context.xml");
        wac.refresh();

        this.dispatcherServlet = new SwiftDispatcher(wac);
        this.dispatcherServlet.init(servletConfig);
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ch.pipeline().addLast("decoder", new HttpRequestDecoder());
        ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
        ch.pipeline().addLast("encoder", new HttpResponseEncoder());
        ch.pipeline().addLast("chunkedWriter", new ChunkedWriteHandler());
//        ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());
//        //将多个消息转换为单一的FullHttpRequest或FullHttpResponse对象
//        ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
//        ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
//        //支持异步发送大的码流,但不占用过多的内存,防止JAVA内存溢出
//        ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
        ch.pipeline().addLast("httpServerHandler", new HttpServerHandler(dispatcherServlet));
    }
}
