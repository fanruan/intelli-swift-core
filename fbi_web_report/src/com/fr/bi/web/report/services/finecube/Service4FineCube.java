package com.fr.bi.web.report.services.finecube;

import com.fr.stable.fun.Service;
import com.fr.web.core.ActionCMD;
import com.fr.web.core.WebActionsDispatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Young's on 2016/8/29.
 */
public class Service4FineCube implements Service {

    private static ActionCMD[] actions = {
            new FCOpenSessionAction(),
            new FCUpdateSessionAction(),
            new FCCheckSessionAction(),
            new FCCloseSessionAction(),
            new FCGetAllAvailableCubeDataAction(),
            new FCLoadDataAction()

    };

    @Override
    public String actionOP() {
        return "fr_fine_cube";
    }

    @Override
    public void process(HttpServletRequest req, HttpServletResponse res, String op, String sessionID) throws Exception {
        WebActionsDispatcher.dealForActionCMD(req, res, sessionID, actions);
    }

}
