package com.fr.bi;

import com.fr.base.TemplateUtils;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.analyze.session.BIWeblet;
import com.fr.bi.cal.stable.utils.BIReportUtils;
import com.fr.bi.fs.BIReportNode;
import com.fr.bi.fs.BISuperManagetDAOManager;
import com.fr.bi.test.*;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.stable.fun.impl.NoSessionIDService;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.core.SessionDealWith;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by richie on 15/7/21.
 */
public class DemoService extends NoSessionIDService {

    private static ActionNoSessionCMD[] actions = {
            new BIGetTreeNodeAction(),
            new BIGetTreeSearchNodeAction(),
            new BIGetLargeDataAction(),
            new BIGetTreeDisplayNodeAction(),
            new BIGetTreeSelectNodeAction(),
            new BIAdjustTreeDataStructureAction(),
            new AttachmentUploadAction()
    };

    @Override
    public void process(HttpServletRequest req, HttpServletResponse res, String op) throws Exception {
        String cmd = WebUtils.getHTTPRequestParameter(req, "cmd");
        if (StringUtils.isBlank(cmd)) {
            return;
        }
        for (ActionNoSessionCMD act : actions) {
            if (act.getCMD().equalsIgnoreCase(cmd)) {
                act.actionCMD(req, res);
                return;
            }
        }
        if (ComparatorUtils.equals(cmd, "mvc")) {
            Map<String, Object> map = new HashMap<String, Object>();
            List<BIReportNode> list = BISuperManagetDAOManager.getInstance().listAll();
            String sessionID = SessionDealWith.generateSessionID(req, res, new BIWeblet(new BIReportNode()));
            BISession biSessionInfor = (BISession) SessionDealWith.getSessionIDInfor(sessionID);
            biSessionInfor.setShareReq(true);
            map.put("sessionID", sessionID);
            if (!list.isEmpty()) {
                BIReportNode node = list.get(0);
                JSONObject reportSetting = BIReportUtils.getBIReportNodeJSON(node);
                map.put("popConfig", reportSetting);
            } else {
                map.put("popConfig", "null");
            }
            WebUtils.writeOutTemplate("/com/fr/bi/web/cross/demo/" + cmd + ".html", res, map);
            return;
        }
        Map map = new HashMap();
        map.put("layoutType", cmd);
        PrintWriter writer = WebUtils.createPrintWriter(res);
        TemplateUtils.dealWithTemplate("/com/fr/bi/web/cross/demo/" + cmd + ".html", writer, map);
        writer.flush();
        writer.close();
    }

    @Override
    public String actionOP() {
        return "bi_demo";
    }
}