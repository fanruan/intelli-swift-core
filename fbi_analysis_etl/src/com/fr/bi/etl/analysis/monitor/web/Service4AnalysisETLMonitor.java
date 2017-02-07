package com.fr.bi.etl.analysis.monitor.web;

import com.fr.bi.etl.analysis.monitor.web.action.BIAnalysisETLCheckAllTableHealTHAction;
import com.fr.bi.etl.analysis.monitor.web.action.BIAnalysisETLCheckAllTableHealTHPageAction;
import com.fr.bi.etl.analysis.monitor.web.action.BIAnalysisETLCheckSingleTableHealTHAction;
import com.fr.bi.web.service.action.*;
import com.fr.stable.fun.Service;
import com.fr.stable.web.RequestCMDReceiver;
import com.fr.web.core.WebActionsDispatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by daniel on 2017/2/3.
 */
public class Service4AnalysisETLMonitor implements Service {


    private static RequestCMDReceiver[] actions = {
            new BIAnalysisETLCheckAllTableHealTHPageAction(),
            new BIAnalysisETLCheckAllTableHealTHAction(),
            new BIAnalysisETLCheckSingleTableHealTHAction()

    };

    public String actionOP() {
        return "sppa_monitor";
    }

    public void process(HttpServletRequest req, HttpServletResponse res, String op, String sessionID) throws Exception {
        WebActionsDispatcher.dealForActionDefaultCmd(req,res,sessionID,actions, actions[0].getCMD());
    }
}
