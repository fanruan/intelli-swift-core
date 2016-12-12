package com.fr.bi.web.report.services.fs;

import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.locale.Interbi;
import com.fr.bi.web.base.utils.BIWebUtils;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.ComparatorUtils;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

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
        long userId = ServiceUtils.getCurrentUserID(req);
        Map info = new java.util.HashMap();
        if (!ComparatorUtils.equals(BIWebUtils.getUserEditViewAuth(userId), BIReportConstant.REPORT_AUTH.NONE)) {
            WebUtils.writeOutTemplate("/com/fr/bi/web/html/platform/bi_shared_to_me.html", res, new HashMap());
        } else {
            info.put("message", Interbi.getLocText("BI-User_Has_No_View_Privilege"));
            WebUtils.writeOutTemplate("/com/fr/bi/web/html/bi_no_privilege.html", res, info);
        }
    }
}
