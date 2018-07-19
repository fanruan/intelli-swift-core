package com.fr.swift.http.servlet.interceptor;

import com.fr.stable.ArrayUtils;
import com.fr.swift.http.servlet.BodyReaderHttpServletRequestWrapper;
import com.fr.swift.http.servlet.BodyReaderHttpServletResponseWrapper;
import com.fr.third.springframework.web.servlet.HandlerInterceptor;
import com.fr.third.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class created on 2018/6/14
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class EndecryptionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        if (request.getContentLength() > 0) {
            BodyReaderHttpServletRequestWrapper requstWrapper = (BodyReaderHttpServletRequestWrapper) request;
            requstWrapper.setBody(requstWrapper.getBody());
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        byte[] data = ((BodyReaderHttpServletResponseWrapper) response)
                .getResponseData();
        if (!ArrayUtils.isEmpty(data)) {
            response.reset();
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(data);
            outputStream.flush();
            outputStream.close();
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
    }
}
