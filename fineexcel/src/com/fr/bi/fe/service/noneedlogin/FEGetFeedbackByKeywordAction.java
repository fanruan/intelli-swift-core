package com.fr.bi.fe.service.noneedlogin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fr.bi.fe.fs.data.FineExcelUserService;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

/**
 * Created by young.
 */
public class FEGetFeedbackByKeywordAction extends ActionNoSessionCMD {
	@Override
	public void actionCMD(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		long userId = ServiceUtils.getCurrentUserID(req);
		String keyword = WebUtils.getHTTPRequestParameter(req, "keyword");
		JSONArray ja = FineExcelUserService.getInstance().getFeedbackByKeyword(
				keyword);
		WebUtils.printAsJSON(res, ja);
	}

	@Override
	public String getCMD() {
		return "get_feedback_by_keyword";
	}
}