/**
 *
 */
package com.fr.bi.web.conf.services.cubetask;

import com.fr.bi.web.conf.AbstractBIConfigureAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class BIGetSingleTableUpdateAction extends AbstractBIConfigureAction {


    @Override
    public String getCMD() {
        return "get_single_table_update_action";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
//		String tableString = WebUtils.getHTTPRequestParameter(req, "table");
//		long userId = ServiceUtils.getCurrentUserID(req);
//		if(tableString != null){
//			BITableConf table = new BITableConf();
//			table.parseJSON(new JSONObject(tableString));
//
//			SingleTableUpdateAction action = BIInterfaceAdapter.getBIBusiPackAdapter().getSingleTableUpdateAction(table.createKey(), userId);
//            WebUtils.printAsJSON(res, action.createJSON());
//		}
    }

}