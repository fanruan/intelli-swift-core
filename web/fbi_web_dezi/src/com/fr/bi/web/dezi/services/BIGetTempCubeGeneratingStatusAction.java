package com.fr.bi.web.dezi.services;

import com.finebi.cube.conf.CubeGenerationManager;
import com.fr.bi.cal.analyze.exception.NoneAccessablePrivilegeException;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.web.dezi.AbstractBIDeziAction;
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
        //hasTask这里有问题,等kary提供taskId的check
        if (!CubeGenerationManager.getCubeManager().hasTask(session.getUserId())) {
            jo.put("percent", 100);
        } else {
            jo.put("percent", 50);
        }

        WebUtils.printAsJSON(res, jo);
    }

    @Override
    public String getCMD() {
        return "get_temp_cube_generating_status";
    }

}
