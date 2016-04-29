package com.fr.bi.web.conf.services;

import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.control.UserControl;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BIGetAllTemplatesMadeByAdminAction extends
        AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "get_all_template_made_by_admin";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        JSONArray ja = new JSONArray();
        if (userId == UserControl.getInstance().getSuperManagerID()) {
//    		java.util.List templatesMadeByAdmin = SystemManagerFavoriteAndADHOC.getInstance().findSysBIReportNodes();
//
//    		for (int i = 0, len = templatesMadeByAdmin.size(); i < len; i++) {
//    			BIReportNode node = (BIReportNode)templatesMadeByAdmin.get(i);
//    			ja.put(node.createJSONConfig());
//    		}
        }

        WebUtils.printAsJSON(res, ja);
    }

}