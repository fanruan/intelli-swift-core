package com.fr.swift.http.dispatcher;

import com.fr.swift.http.servlet.BodyReaderHttpServletRequestWrapper;
import com.fr.swift.http.servlet.BodyReaderHttpServletResponseWrapper;
import com.fr.third.springframework.web.context.WebApplicationContext;
import com.fr.third.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * This class created on 2018/6/14
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftDispatcher extends DispatcherServlet {

    public SwiftDispatcher() {
    }

    public SwiftDispatcher(WebApplicationContext webApplicationContext) {
        super(webApplicationContext);
    }

    @Override
    protected void initFrameworkServlet() throws ServletException {
        super.initFrameworkServlet();
    }

    @Override
    protected void doDispatch(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        try {
            Map requestParams = request.getParameterMap();
            BodyReaderHttpServletRequestWrapper requstWrapper = new BodyReaderHttpServletRequestWrapper(
                    request);

            BodyReaderHttpServletResponseWrapper responseWrapper = new BodyReaderHttpServletResponseWrapper(
                    response);

            responseWrapper.setHeader("Access-Control-Allow-Origin", "*");
            super.doDispatch(requstWrapper, responseWrapper);
            response.getOutputStream().write(responseWrapper.getResponseData());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}