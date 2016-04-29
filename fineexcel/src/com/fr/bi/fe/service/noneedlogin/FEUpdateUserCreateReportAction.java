package com.fr.bi.fe.service.noneedlogin;

import com.fr.bi.fe.fs.data.FineExcelUserService;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.web.core.ActionNoSessionCMD;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FEUpdateUserCreateReportAction extends ActionNoSessionCMD {

	@Override
	public String getCMD() {
		return "update_user_create_report_info";
	}

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
//		FineExcelUserService fineExcelUserService  =new FineExcelUserService();
//		fineExcelUserService.updateNewReport(fineExcelUserService.getFEUserById(userId).getEmail());
        String email = FineExcelUserService.getInstance().getFEUserById(userId).getEmail();
        FineExcelUserService.getInstance().updateNewReport(email);
    }
}