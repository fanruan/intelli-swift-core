package com.fr.bi.web.base.utils;

import com.fr.stable.Constants;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Coder: Sheldon
 * Date: 4/20/15
 * Time: 10:40 AM
 */
public class BIServiceUtil {

    public static void setPreviousUrl(HttpServletRequest req) {
        setPreviousUrl(req, WebUtils.getOriginalURL(req));
    }

    public static void setPreviousUrl(HttpServletRequest req, String url) {
        HttpSession session = req.getSession(true);
        session.setAttribute(Constants.ORIGINAL_URL, url);
    }
}