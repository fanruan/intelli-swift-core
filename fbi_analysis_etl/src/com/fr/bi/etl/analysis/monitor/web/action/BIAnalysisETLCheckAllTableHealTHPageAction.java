package com.fr.bi.etl.analysis.monitor.web.action;

import com.fr.bi.etl.analysis.monitor.MonitorUtils;
import com.fr.bi.web.service.action.AbstractAnalysisETLAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * Created by kary on 17-1-6.
 */
public class BIAnalysisETLCheckAllTableHealTHPageAction extends AbstractAnalysisETLAction {
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        WebUtils.writeOutTemplate("/com/fr/bi/etl/analysis/monitor/web/html/view.html", res, new HashMap());
    }


    public String getCMD() {
        return "view";
    }

}
