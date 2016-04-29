package com.fr.bi.web.dezi.services;

import com.fr.bi.cal.analyze.exception.NoneAccessablePrivilegeException;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.web.dezi.AbstractBIDeziAction;
import com.fr.json.JSONObject;
import com.fr.web.core.SessionDealWith;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 小灰灰 on 2014/10/21.
 */
public class BIGetTempCubeGenerateStatusAction extends AbstractBIDeziAction {
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        BISession session = (BISession) SessionDealWith.getSessionIDInfor(sessionID);
        if (session == null) {
            throw new NoneAccessablePrivilegeException();
        }
        JSONObject jo = new JSONObject();
        if (session.getProvider() != null) {
            jo.put("percent", session.getProvider().getPercent());
            jo.put("detail", session.getProvider().getDetail());
        } else {
            jo.put("percent", 100);
        }
        WebUtils.printAsJSON(res, jo);
    }

    @Override
    public String getCMD() {
        return "get_temp_cube_generate_status";
    }
}