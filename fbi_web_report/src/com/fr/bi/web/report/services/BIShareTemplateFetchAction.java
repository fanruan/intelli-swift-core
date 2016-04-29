package com.fr.bi.web.report.services;

import com.fr.bi.fs.BISharedReportDAO;
import com.fr.fs.base.entity.User;
import com.fr.fs.control.UserControl;
import com.fr.general.FArray;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BIShareTemplateFetchAction extends ActionNoSessionCMD {

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String templateIdString = WebUtils.getHTTPRequestParameter(req, "template_id");
        long templateId = Long.parseLong(templateIdString);
        User[] usersShared = UserControl.getInstance().getOpenDAO(BISharedReportDAO.class).findUsersByReportId(templateId);
        JSONArray ja = new JSONArray();
        for (int i = 0; i < usersShared.length; i++) {
            User user = usersShared[i];
            /*ja.put(createUserJSON(user).put(
                    "user_access",
                    BIReportUtils.getAccessabilityOfTemplateByUserId(templateId, BIInterfaceAdapter.getBIBusiPackAdapter().loadAllAvailablePackages(user.getId()), user.getId())
            ));*/
            ja.put(createUserJSON(user));
        }
        WebUtils.printAsJSON(res, ja);
    }

    private JSONObject createUserJSON(User user) throws Exception{
        JSONArray ja = new JSONArray();

        try {
            FArray departs = UserControl.getInstance().getUserDP(user.getId());

            for(int i = 0; i< departs.length(); i++) {
                JSONObject depart = (JSONObject)departs.elementAt(i);
                ja.put(depart.getString("departments") + "-" + depart.getString("jobTitle"));
            }
        } catch (Exception e) {
        }
        return (new JSONObject()).put("user_id", user.getId()).put("user_name", user.getRealname()).put("user_department", ja).put("roles", UserControl.getInstance().getAllSRoleNames(user.getId()));
    }

    @Override
    public String getCMD() {
        return "share_template_fetch";
    }

}