package com.fr.bi.fe.service.noneedlogin;

import com.fr.fs.FSRegisterForBI;
import com.fr.fs.web.service.FSOpenEntryService;
import com.fr.general.RegistEditionException;
import com.fr.general.VT4FR;
import com.fr.json.JSONObject;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Service4BIFSNoLogin extends FSOpenEntryService {
	
	public static JSONObject VTJSONObject;
	
	/**
	 * 注释
	 * @return 注释
	 */
	@Override
	public String actionOP() {
		return "fbi_no_need_login";
	}
	
	private static ActionNoSessionCMD[] actions = {
		
		//FE part
		new FEUploadFeedbackFileAction(),
		new FEScreenCaptureAction(),
		new FESaveUserFeedbackAction(),
		new FEResetPasswordAction(),
		new FEResetPasswordValidateAction(),
		new FECheckUserNameAction(),
		new FECheckUserEmailAction(),
		new FEGetAllFeedbackAction(),
		new FESaveFeedbackAgreeAction(),
		new FEGetFeedbackByUserAction(),
		new FEGetFeedbackImagesAction(),
		new FEGetFeedbackByKeywordAction(),
		new FESaveAdminReplyDisagreeAction(),
		new FESaveAdminReplyAgreeAction(),
		new FEGetAdminReplyAgreeByUserAction(),
		new FEGetUserFeedbackVote4ChartsAction(),
		new FEGetUserFeedbackStatus4ChartsAction(),
        new BIFERegisterAction(),
		new FEEmailValidateAction(),
            new FEImportExcelDataAction(),
            new FEInitConfigurePaneAction(),
            new FERemoveTableFromBusiPackage(),
            new FESynchronousTableInforInPackage(),
            new FESaveUserFeedbackAction(),
            new FEGetBriefTablesOfOnePackageAction(),
            new FEGetMyReportsAction(),
            new FEUploadFeedbackFileAction(),
            new FEGetBriefTablesFromFEExcelAction(),
            new FEUpdateUserCreateReportAction(),
			new FEFindMyReportPageAction(),
            //FineExcel part
            new FEGetCubeStatuslAction(),
            new FEGetFullImportedExcelAction(),
            new FEGetImportedExcelAction(),
            new FEGetPartImportedExcelAction(),
            new FEScreenCaptureAction(),
            new FEGetSelectedExcelJSONAction()
    };
	
	
    /**
     * op=fs内的执行action
     * @param req   请求
     * @param res   返回
     * @param op    当前操作类型
     * @throws Exception
     */
	@Override
	public void process(HttpServletRequest req, HttpServletResponse res, String op) throws Exception {
		if(!FSRegisterForBI.isSupportFS()){
			throw new RegistEditionException(VT4FR.FS_BI);
		}
		
		String cmd = WebUtils.getHTTPRequestParameter(req, "cmd");
			for(int i = 0; i < actions.length; i++) {
				ActionNoSessionCMD action = actions[i];
				String aceptCmd = action.getCMD();
				if(aceptCmd.equalsIgnoreCase(cmd)) {
					action.actionCMD(req, res);
				}
			}

	    return;
	}
}