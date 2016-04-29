package com.fr.bi.web.conf.services;

import com.fr.bi.web.conf.AbstractBIConfigureAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BISetUserLoginInfoTableAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "set_user_login_info";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
//		long userId = ServiceUtils.getCurrentUserID(req);
//		if(UserControl.getInstance().hasModulePrivilege(userId, FSConstants.MODULEID.BI)){
//			String table = WebUtils.getHTTPRequestParameter(req, "table");
//			BILoginUserInfo info = null;
//			if(table != null){
//				String field_name = WebUtils.getHTTPRequestParameter(req, "field_name");
//				JSONObject jo = new JSONObject(table);
//				BITableConf td = new BITableConf();
//				td.parseJSON(jo);
//				info = new BILoginUserInfo();
//				info.setTable(td.createKey());
//				info.setUserNameColumn(field_name);
//			}
//			BIInterfaceAdapter.getBIConnectionAdapter().settingUseSetLoginUserInfo(info, userId);
//			FRContext.getCurrentEnv().writeResource(BIInterfaceAdapter.getBIConnectionAdapter().getBIConnectionManager(userId));
//			WebUtils.printAsString(res, "sucess");
//		} else {
//			WebUtils.printAsString(res, "error : no privilege");
//		}
    }

}