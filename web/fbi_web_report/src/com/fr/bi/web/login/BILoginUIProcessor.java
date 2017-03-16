package com.fr.bi.web.login;

import com.fr.base.TemplateUtils;
import com.fr.fs.fun.impl.AbstractLoginUIProcessor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by richie on 2017/1/9.
 */
public class BILoginUIProcessor extends AbstractLoginUIProcessor {

    public static final String PLUGIN_ID = "com.fr.bi.plugin.login";

    public void process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String url = TemplateUtils.render("${servletURL}?" + loginFace(req) + "&_=" + System.currentTimeMillis());
        res.sendRedirect(url);
    }

    public String loginFace() throws Exception {
        return "op=fs_load&cmd=fs_signin&pid=" + PLUGIN_ID;
    }
}
