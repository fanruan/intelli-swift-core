package com.fr.bi.fe.service.noneedlogin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fr.bi.conf.aconfig.BIInterfaceAdapter;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

public class FEGetCubeStatuslAction extends ActionNoSessionCMD {

	@Override
	public String getCMD() {
		return "get_cube_status";
	}

	@Override
	public void actionCMD(HttpServletRequest req, HttpServletResponse res ) throws Exception {
		long userId = ServiceUtils.getCurrentUserID(req);
		boolean cubeStatus = BIInterfaceAdapter.getCubeAdapter().checkCubeStatus(userId);
		JSONObject jo = new JSONObject();
		jo.put("cubeStatus", cubeStatus);
		WebUtils.printAsJSON(res, jo);
	}
}