package com.fr.bi.web.conf.services;

import com.fr.bi.web.conf.AbstractBIConfigureAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BISearchTableInfoInPackAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "search_table_info";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
        /*long userId = ServiceUtils.getCurrentUserID(req);
		boolean hasConnectionRight = userId == UserControl.getInstance().getSuperManagerID();
		if(!hasConnectionRight){
			WebUtils.printAsJSON(res, new JSONObject());
			return;
		}
		String keyword = WebUtils.getHTTPRequestParameter(req, "keyword");
		String packageName = WebUtils.getHTTPRequestParameter(req, "package_name");
		String connectionName = WebUtils.getHTTPRequestParameter(req, "connection_name");
		String tableName = WebUtils.getHTTPRequestParameter(req, "table_name");
		
		WebUtils.printAsJSON(res, BIInterfaceAdapter.getBIBusiPackAdapter().search4keywordAsJsonByConnection( 
			keyword, packageName, tableName, connectionName, userId
		));*/
    }

}