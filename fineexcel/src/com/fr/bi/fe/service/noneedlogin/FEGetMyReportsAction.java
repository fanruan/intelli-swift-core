/**
 * 
 */
package com.fr.bi.fe.service.noneedlogin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fr.bi.platform.web.fs.BIFSHelper;
import com.fr.bi.web.services.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

/**
 * @author young
 *
 */
public class FEGetMyReportsAction extends AbstractBIConfigureAction {

	@Override
	public String getCMD() {
		return "fe_get_my_reports";
	}
	
	@Override
	protected void actionCMDPrivilegePassed(HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		long userId = ServiceUtils.getCurrentUserID(req);

		JSONArray resultNodes = BIFSHelper.createRootNodesInfor(userId, req.getLocale());
		JSONArray ja = new JSONArray();
		for(int i = 0; i < resultNodes.length(); i ++){
			JSONObject jo = resultNodes.getJSONObject(i);
			JSONArray jaChild = jo.getJSONArray("ChildNodes");
			
			for(int j = 0; j < jaChild.length(); j++){
				JSONObject jo_ = new JSONObject();
				jo_.put("text",jaChild.getJSONObject(j).getString("text"));
				ja.put(jo_);
			}
		}
		
		
		WebUtils.printAsJSON(res, ja);
		
	}
}