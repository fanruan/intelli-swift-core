package com.fr.bi.web.report.services.fs;

import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * Created by Young's on 2016/8/22.
 */
public class BIInitSharedToMeAction extends ActionNoSessionCMD {
    @Override
    public String getCMD() {
        return "bi_init_shared_to_me";
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        WebUtils.writeOutTemplate("/com/fr/bi/web/html/plateform/bi_shared_to_me.html", res, new HashMap());
    }
}
