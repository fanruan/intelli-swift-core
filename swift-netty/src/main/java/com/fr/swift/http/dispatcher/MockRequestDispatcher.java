package com.fr.swift.http.dispatcher;

import com.fr.swift.http.servlet.MockHttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * This class created on 2018/6/14
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class MockRequestDispatcher implements RequestDispatcher {

    private final Log logger = LogFactory.getLog(getClass());

    private final String url;


    /**
     * Create a new MockRequestDispatcher for the given URL.
     *
     * @param url the URL to dispatch to.
     */
    public MockRequestDispatcher(String url) {
        this.url = url;
    }


    public void forward(ServletRequest request, ServletResponse response) {
        if (response.isCommitted()) {
            throw new IllegalStateException("Cannot perform forward - response is already committed");
        }
        getMockHttpServletResponse(response).setForwardedUrl(this.url);
        if (logger.isDebugEnabled()) {
            logger.debug("MockRequestDispatcher: forwarding to URL [" + this.url + "]");
        }
    }

    public void include(ServletRequest request, ServletResponse response) {
        getMockHttpServletResponse(response).addIncludedUrl(this.url);
        if (logger.isDebugEnabled()) {
            logger.debug("MockRequestDispatcher: including URL [" + this.url + "]");
        }
    }

    /**
     * Obtain the underlying MockHttpServletResponse,
     * unwrapping {@link HttpServletResponseWrapper} decorators if necessary.
     */
    protected MockHttpServletResponse getMockHttpServletResponse(ServletResponse response) {
        if (response instanceof MockHttpServletResponse) {
            return (MockHttpServletResponse) response;
        }
        if (response instanceof HttpServletResponseWrapper) {
            return getMockHttpServletResponse(((HttpServletResponseWrapper) response).getResponse());
        }
        throw new IllegalArgumentException("MockRequestDispatcher requires MockHttpServletResponse");
    }

}