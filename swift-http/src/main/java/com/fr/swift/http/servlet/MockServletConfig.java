package com.fr.swift.http.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class created on 2018/6/14
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class MockServletConfig implements ServletConfig {

    private final ServletContext servletContext;

    private final String servletName;

    private final Map<String, String> initParameters = new LinkedHashMap<String, String>();


    /**
     * Create a new MockServletConfig with a default {@link MockServletContext}.
     */
    public MockServletConfig() {
        this(null, "");
    }

    /**
     * Create a new MockServletConfig with a default {@link MockServletContext}.
     *
     * @param servletName the name of the servlet
     */
    public MockServletConfig(String servletName) {
        this(null, servletName);
    }

    /**
     * Create a new MockServletConfig.
     *
     * @param servletContext the ServletContext that the servlet runs in
     */
    public MockServletConfig(ServletContext servletContext) {
        this(servletContext, "");
    }

    /**
     * Create a new MockServletConfig.
     *
     * @param servletContext the ServletContext that the servlet runs in
     * @param servletName    the name of the servlet
     */
    public MockServletConfig(ServletContext servletContext, String servletName) {
        this.servletContext = (servletContext != null ? servletContext : new MockServletContext());
        this.servletName = servletName;
    }


    public String getServletName() {
        return this.servletName;
    }

    public ServletContext getServletContext() {
        return this.servletContext;
    }

    public void addInitParameter(String name, String value) {
        this.initParameters.put(name, value);
    }

    public String getInitParameter(String name) {
        return this.initParameters.get(name);
    }

    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(this.initParameters.keySet());
    }

}

