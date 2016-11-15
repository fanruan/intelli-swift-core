package com.fr.bi.h5.services;

import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.analyze.session.BIWeblet;
import com.fr.bi.conf.VT4FBI;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.fs.BIDAOUtils;
import com.fr.bi.fs.BIReportNode;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.tool.BIReadReportUtils;
import com.fr.bi.web.base.utils.BIServiceUtil;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.ComparatorUtils;
import com.fr.general.web.ParameterConsts;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.stable.web.Device;
import com.fr.stable.web.Weblet;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.core.SessionDealWith;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class H5InitAction extends ActionNoSessionCMD {

    public static final String CMD = "h5_init";

    /**
     * 注释
     *
     * @param req 注释
     * @param res 注释
     * @return 注释
     */
    public static void dealWithWebPage(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String id = WebUtils.getHTTPRequestParameter(req, "id");
        long currentLoginUserId = ServiceUtils.getCurrentUserID(req);
        String createUserId = WebUtils.getHTTPRequestParameter(req, "createBy");
        long templateCreateUserId = Long.parseLong(createUserId);
        BIReportNode node = null;
        if (id != null) {
            node = BIDAOUtils.findByID(Long.parseLong(id), templateCreateUserId);
        }
        JSONObject reportSetting = BIReadReportUtils.getBIReportNodeJSON(node);
        dealWithPane(req, res, new BIWeblet(node), reportSetting, node);
    }

    private static void dealWithPane(HttpServletRequest req,
                                     HttpServletResponse res, Weblet let, JSONObject pop, BIReportNode node) throws Exception {
        String sessionID = SessionDealWith.generateSessionID(req, res, let);

        if (SessionDealWith.generateSessionID_isPromptRegisted(req)) {
            return;
        }
        final BISession sessionIDInfo = (BISession) SessionDealWith.getSessionIDInfor(sessionID);
        long userId = ServiceUtils.getCurrentUserID(req);
        dealWithNormal(req, res, sessionID, req.getLocale(), pop, sessionIDInfo, node, userId);
    }

    private static void dealWithNormal(HttpServletRequest req,
                                       HttpServletResponse res, final String sessionID, Locale locale, JSONObject pop, BISession sessionIDInfo, BIReportNode node, Long userId) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> parameterMap = WebUtils.parameters4SessionIDInfor(req);
        map.putAll(parameterMap);
        if (pop != null) {
            map.put("popConfig", pop);
            map.put(BIBaseConstant.REPORT_ID, node.getId());
        } else {
            map.put("popConfig", "null");
        }
        map.put(ParameterConsts.SESSION_ID, sessionID);
        map.put("userId", userId);
        map.put("createBy", node.getUserId());
        map.put("reportName", node.getReportName() != null ? node.getReportName() : "null");
        map.put("reg", VT4FBI.toJSONObject());
        map.put("description", node.getDescription());
        map.put("__version__", BIConfigureManagerCenter.getCubeConfManager().getPackageLastModify() + "" + userId);
        writeData(req, res, "/com/fr/bi/h5/dist/index.html", map);
    }

    private static void writeData(HttpServletRequest req, HttpServletResponse res, String templatePath, Map messageMap) throws Exception {
        Device device = WebUtils.getDevice(req);
        boolean isCached = !StringUtils.isBlank(WebUtils.getHTTPRequestParameter(req, "isCached"));
        if (device.isMobile()) {
            if (messageMap.containsKey("message")) {
                PrintWriter writer = WebUtils.createPrintWriter(res);
                writer.print(messageMap.get("message"));
                writer.flush();
                writer.close();
            } else {
                JSONObject jo = new JSONObject();
                if (!isCached) {
                    jo.put("popConfig", messageMap.get("popConfig"));
                }
                jo.put(ParameterConsts.SESSION_ID, messageMap.get(ParameterConsts.SESSION_ID));
                WebUtils.printAsJSON(res, jo);
            }
        } else {
            WebUtils.writeOutTemplate(templatePath, res, messageMap);
        }
    }

    @Override
    public String getCMD() {
        return CMD;
    }

    /**
     * 注释
     *
     * @param req 注释
     * @param res 注释
     * @return 注释
     */
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        BIServiceUtil.setPreviousUrl(req);
        dealWithWebPage(req, res);
    }
}