package com.fr.bi.web.conf.services;

import com.fr.bi.web.conf.AbstractBIConfigureAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BIGetUserLoginInfoAction extends
        AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "get_login_info";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
//		JSONObject jo = new JSONObject();
//		long userId = ServiceUtils.getCurrentUserID(req);
//		BILoginUserInfo info = BIInterfaceAdapter.getBIConnectionAdapter().settingUseGetLoginUserInfo(userId);
//		if( UserControl.getInstance().hasModulePrivilege(userId, FSConstants.MODULEID.BI)
//				&& info != null ){
//			jo = info.createJSON();
//		}
//		WebUtils.printAsJSON(res, jo);
    }

}