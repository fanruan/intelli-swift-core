package com.fr.bi.web.dezi.services;

import com.fr.bi.cal.TempCubeManager;
import com.fr.bi.cal.analyze.exception.NoneAccessablePrivilegeException;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.generate.index.TempIndexGenerator;
import com.fr.bi.cal.stable.engine.TempCubeTask;
import com.fr.bi.web.dezi.AbstractBIDeziAction;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.web.core.SessionDealWith;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BIGetTempCubeGeneratingStatusAction extends AbstractBIDeziAction {

	@Override
	public void actionCMD(HttpServletRequest req, HttpServletResponse res,
			String sessionID) throws Exception {

        BISession session = (BISession) SessionDealWith.getSessionIDInfor(sessionID);
        if (session == null) {
            throw new NoneAccessablePrivilegeException();
        }
        JSONObject jo = new JSONObject();
        if(session.getProvider() != null && ComparatorUtils.equals(session.getProvider().getPercent(), 100)){
            TempIndexGenerator provider = TempCubeManager.getInstance(new TempCubeTask(session.getTempTableMd5(), session.getUserId())).getLoaderGenerating();
            jo.put("percent", provider.getPercent());
            jo.put("detail", provider.getDetail());
        } else{
            jo.put("percent",100);
        }

        WebUtils.printAsJSON(res, jo);
	}

	@Override
	public String getCMD() {
		return "get_temp_cube_generating_status";
	}

}
