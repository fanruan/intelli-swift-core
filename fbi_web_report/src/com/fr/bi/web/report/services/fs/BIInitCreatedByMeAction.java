package com.fr.bi.web.report.services.fs;

import com.fr.fs.control.UserControl;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by Young's on 2016/8/22.
 */
public class BIInitCreatedByMeAction extends ActionNoSessionCMD {
    @Override
    public String getCMD() {
        return "bi_init_created_by_me";
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        Map info = new java.util.HashMap();
        boolean isAdmin = userId == UserControl.getInstance().getSuperManagerID();
        info.put("isAdmin", isAdmin);
        WebUtils.writeOutTemplate("/com/fr/bi/web/html/plateform/bi_created_by_me.html", res, info);
    }
}
