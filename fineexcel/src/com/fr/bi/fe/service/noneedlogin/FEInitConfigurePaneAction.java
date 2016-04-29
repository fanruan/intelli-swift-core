package com.fr.bi.fe.service.noneedlogin;

import com.fr.bi.web.services.conf.AbstractBIConfigureAction;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class FEInitConfigurePaneAction extends AbstractBIConfigureAction {

	@Override
	public String getCMD() {
		return "fe_configure_pane";
	}

	@Override
	protected void actionCMDPrivilegePassed(HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		String tableName = WebUtils.getHTTPRequestParameter(req, "full_file_name");
		String md5tableName = WebUtils.getHTTPRequestParameter(req, "md5_table_name");
		
		Map map = new HashMap();
		map.put("tableName", tableName);
		map.put("md5TableName", md5tableName);
		WebUtils.writeOutTemplate("/com/fr/bi/web/html/fe_conf_pane.html", res, map);
	}
	

}