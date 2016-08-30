package com.fr.bi.web.report.services.finecube;

import com.fr.bi.cal.analyze.session.BISession;
import com.fr.json.JSONObject;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.core.SessionDealWith;
import com.fr.web.core.SessionIDInfor;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Young's on 2016/8/29.
 */
public class FCGetAllFieldsByPackageAction extends ActionNoSessionCMD {
    @Override
    public String getCMD() {
        return "fc_get_all_fields_by_package";
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        SessionIDInfor sessionInfo = SessionDealWith.getSessionIDInfor(sessionID);

        JSONObject result = new JSONObject();
        if (sessionInfo == null || !(sessionInfo instanceof BISession)) {
            result.put("error", "no session!");
        } else {
            BISession session = (BISession) sessionInfo;
            String packageId = WebUtils.getHTTPRequestParameter(req, "package_id");
            JSONObject groupsAndPackages = session.getAllAvailablePackagesGroups(req);
            if (groupsAndPackages.has("packages")) {
                JSONObject packages = groupsAndPackages.getJSONObject("packages");
                if (packages.has(packageId)) {
                    result.put("fields", session.getAllFieldsByPackage(packageId));
                } else {
                    result.put("error", "the package you have no permission!");
                }
            } else {
                result.put("error", "no permission package!");
            }
        }
        WebUtils.printAsJSON(res, result);
    }
}
