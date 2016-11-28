package com.fr.bi.web.base.utils;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.fr.base.ChartPreStyleServerManager;
import com.fr.base.ConfigManager;
import com.fr.base.FRContext;
import com.fr.bi.cal.analyze.base.CubeIndexManager;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.analyze.session.BISessionUtils;
import com.fr.bi.conf.VT4FBI;
import com.fr.bi.conf.fs.FBIConfig;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.fs.BIReportNode;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.Status;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.stable.utils.conf.BISystemEnvUtils;
import com.fr.bi.web.base.operation.BIOperationRecord;
import com.fr.chart.base.ChartPreStyle;
import com.fr.fs.base.entity.User;
import com.fr.fs.control.UserControl;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.*;
import com.fr.general.web.ParameterConsts;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.Constants;
import com.fr.stable.StableUtils;
import com.fr.stable.StringUtils;
import com.fr.stable.web.Device;
import com.fr.stable.web.Weblet;
import com.fr.web.core.SessionDealWith;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;
import java.util.logging.Level;


public class BIWebUtils {


    public static void dealWithWebPage(HttpServletRequest req,
                                       HttpServletResponse res, Weblet let, JSONObject pop, BIReportNode node) throws Exception {
        HttpSession s = req.getSession(false);
        if (s != null) {
            Object roleName = s.getAttribute(Constants.P.PRIVILEGE_AUTHORITY);
            if (roleName != null) {
                FRContext.getLogger().info(Inter.getLocText("INFO-Current_Role") + ":" + roleName);
            }
        }
        String sessionID = SessionDealWith.generateSessionID(req, res, let);

        if (SessionDealWith.generateSessionID_isPromptRegisted(req)) {
            flushLicenseToast(req, res, sessionID, Inter.getLocText("BI-No_BI_Access_Permission", req.getLocale()));
            return;
        }

        if (sessionID == null || !SessionDealWith.hasSessionID(sessionID)) {
            return;
        }
        if (dealWithRegistration(req, pop, res, sessionID, node.getUserId())) {
            dealWithPaneHtml(req, res, sessionID, pop, node);
        }
    }

    private static boolean dealWithNotSupportMultiStatisticsWidget(HttpServletRequest req, JSONObject pop, HttpServletResponse res, String sessionID, Locale locale) throws Exception {
        int widgetCount = 0;
        if (pop.has("widget")) {
            widgetCount += pop.getJSONArray("widget").length();
        }
        if (pop.has("detail")) {
            widgetCount += pop.getJSONArray("detail").length();
        }
        if (widgetCount > 1) {
            return flushLicenseToast(req, res, sessionID, Inter.getLocText("BI-License_Not_Support_Add_More_Comp", locale));
        }
        return true;
    }

    private static boolean dealWithRegistration(HttpServletRequest req, JSONObject pop, HttpServletResponse res, String sessionID, long createBy) throws Exception {
        Locale locale = req.getLocale();
        if (pop != null &&
                (!VT4FBI.supportReportShare()
                        || !VT4FBI.supportMultiStatisticsWidget()
                        || !VT4FBI.supportSimpleControl()
                        || !VT4FBI.supportGeneralControl())) {
            if (!VT4FBI.supportReportShare() && !ComparatorUtils.equals(ServiceUtils.getCurrentUserID(req), createBy)) {
                return flushLicenseToast(req, res, sessionID, Inter.getLocText("BI-Donot_Support_Sharing", locale));

            }
            if (!VT4FBI.supportMultiStatisticsWidget()) {
                if (!dealWithNotSupportMultiStatisticsWidget(req, pop, res, sessionID, locale)) {
                    return false;
                }
            }
            if (!VT4FBI.supportSimpleControl() || !VT4FBI.supportGeneralControl()) {
                int controlCount = 0;
                boolean hasGeneral = false;
                if (pop.has("control")) {
                    JSONArray controls = pop.getJSONArray("control");
                    controlCount = controls.length();
                    for (int i = 0; i < controlCount; i++) {
                        if (controls.getJSONObject(i).getInt("type") == BIBaseConstant.GENERAL_QUERY_BUTTON) {
                            hasGeneral = true;
                            break;
                        }
                    }
                }
                if (!VT4FBI.supportSimpleControl() && controlCount > 0) {
                    return flushLicenseToast(req, res, sessionID, Inter.getLocText("BI-License_Not_Support_Add_Control", locale));
                }

                if (!VT4FBI.supportGeneralControl() && hasGeneral) {
                    return flushLicenseToast(req, res, sessionID, Inter.getLocText("BI-License_Not_Support_Add_Gener", locale));
                }
            }
        }
        return true;
    }

    private static boolean flushLicenseToast(HttpServletRequest req, HttpServletResponse res, String sessionID, String text) throws Exception {
        SessionDealWith.closeSession(sessionID);
        writeData(req, res, "/com/fr/bi/web/html/bi_no_lic.html", text);
        return false;
    }

    private static void dealWithEmptyPack(HttpServletRequest req,
                                          HttpServletResponse res, final String sessionID, Locale locale) throws Exception {
        SessionDealWith.closeSession(sessionID);
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", Inter.getLocText("BI-Contact_No_Package", locale));
        map.put("removeLoadingView", "yes");
        writeData(req, res, "/com/fr/bi/web/html/bi_error_message.html", map);
    }

    private static void dealWithNORightPack(HttpServletRequest req,
                                            HttpServletResponse res, final String sessionID, Locale locale) throws Exception {
        SessionDealWith.closeSession(sessionID);
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", Inter.getLocText("BI-Have_No_Any_Package", locale));
        map.put("removeLoadingView", "yes");
        writeData(req, res, "/com/fr/bi/web/html/bi_error_message.html", map);
    }

    /**
     * 生成页面
     *
     * @param req
     * @param res
     * @param sessionID
     * @throws Exception
     */
    private static void dealWithPaneHtml(HttpServletRequest req,
                                         HttpServletResponse res, final String sessionID, JSONObject pop, BIReportNode node) throws Exception {
        Locale locale = req.getLocale();
        if (BISystemEnvUtils.isSystemEnvProper()) {
            SessionDealWith.closeSession(sessionID);
            writeData(req, res, "/com/fr/bi/web/html/bi_invalid.html", Inter.getLocText("BI-Incompatible_HardWare", locale));
            return;
        }
        final BISession sessionIDInfo = (BISession) SessionDealWith.getSessionIDInfor(sessionID);
        if (sessionIDInfo == null) {
            return;
        }
        long userId = ServiceUtils.getCurrentUserID(req);
        if (BICubeConfigureCenter.getPackageManager().isPackagesEmpty(userId)) {
            dealWithEmptyPack(req, res, sessionID, locale);
            return;
        }
        if (!BIConfigureManagerCenter.getAuthorityManager().hasAuthPackageByUser(userId, sessionID)) {
            dealWithNORightPack(req, res, sessionID, locale);
            return;
        }
        if (CubeIndexManager.getInstance().getTableStatus() != Status.LOADED) {
            dealWithCubeGeneating(req, res, sessionID, locale);
        } else {
            dealWithNormal(req, res, sessionID, locale, pop, sessionIDInfo, node, userId);
        }
    }

    private static void dealWithNormal(HttpServletRequest req,
                                       HttpServletResponse res, final String sessionID, Locale locale, JSONObject pop, BISession sessionIDInfo, BIReportNode node, Long userId) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> parameterMap = WebUtils.parameters4SessionIDInfor(req);
        map.putAll(parameterMap);
        if (pop != null) {
            map.put("popConfig", pop);
            map.put(BIBaseConstant.REPORT_ID, node.getId());
            dealWithLog(sessionIDInfo, node.getReportName());
        } else {
            map.put("popConfig", "null");
        }
        JSONObject reportName = new JSONObject();
        reportName.put("name", node.getReportName() != null ? node.getReportName() : "null");
        map.put(ParameterConsts.SESSION_ID, sessionID);
        String isDebug = WebUtils.getHTTPRequestParameter(req, ParameterConsts.__ISDEBUG__);
        String edit = WebUtils.getHTTPRequestParameter(req, "edit");
        String show = WebUtils.getHTTPRequestParameter(req, "show");
        String hideTop = WebUtils.getHTTPRequestParameter(req, "hideTop");
        JSONObject plateConfig = getPlateConfig();
        map.put("userId", userId);
        map.put("edit", edit == null ? "null" : edit);
        map.put("show", show == null ? "null" : show);
        map.put("hideTop", hideTop == null ? "null" : hideTop);
        map.put("createBy", node.getUserId());
        map.put("reportName", reportName);
        map.put("reg", VT4FBI.toJSONObject());
        map.put("description", node.getDescription());
        map.put("plateConfig", plateConfig);
        //cube版本号 和 权限版本号
        map.put("__version__", BIConfigureManagerCenter.getCubeConfManager().getPackageLastModify() + ""
                + BIConfigureManagerCenter.getAuthorityManager().getAuthVersion() + "" + BIConfigureManagerCenter.getCubeConfManager().getMultiPathVersion() + "" + userId);
        boolean biEdit = pop == null || ComparatorUtils.equals(edit, "_bi_edit_");
        boolean isEdit = sessionIDInfo.setEdit(biEdit);
        if (biEdit && !isEdit) {
            map.put("lockedBy", BISessionUtils.getCurrentEditingUserByReport(node.getId(), node.getUserId()));
        }
        if (!hasPrivilege(isEdit, userId, map) && !ComparatorUtils.equals(node.getDescription(), "fine_excel")) {
            String ma = Inter.getLocText(isEdit ? "BI-User_Has_No_Edit_Privilege" : "BI-User_Has_No_View_Privilege", locale);
            map.put("message", ma);
            writeData(req, res, "/com/fr/bi/web/html/bi_no_privilege.html", map);
            return;
        }
        /**
         * Connery:用于预览用户于模板的操作
         * */
        String mode = WebUtils.getHTTPRequestParameter(req, "mode");
        if (ComparatorUtils.equals(mode, "user_operation_preview")) {
            BIOperationRecord record = new BIOperationRecord(node.getUserId(), node.getReportName());
            map.put("operations", record.getJsonObject());
            writeData(req, res, "/com/fr/bi/web/html/bi_dezi_operation_preview.html", map);
        }
        writeData(req, res, isEdit ? (isDebug == null ? "/com/fr/bi/web/html/bi_dezi.html" : "/com/fr/bi/web/html/bi_dezi_debug.html") : "/com/fr/bi/web/html/bi_show.html", map);

    }


    private static void dealWithCubeGeneating(HttpServletRequest req,
                                              HttpServletResponse res, String sessionID, Locale locale) throws Exception {
        SessionDealWith.closeSession(sessionID);
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", Inter.getLocText("BI-P_Wait_Data_Prepar", locale));
        map.put("reload", "true");
        writeData(req, res, "/com/fr/bi/web/html/bi_error_message.html", map);
    }

    private static void dealWithLog(BISession sessionIDInfo, String reportName) {
        if (ConfigManager.getInstance().getLogConfig().isRecordErr()) {
            try {
                FRLogManager.setSession(sessionIDInfo);

                FRContext.getLogger().getRecordManager().recordAccessNoExecuteInfo(reportName,
                        DeclareRecordType.WEB_WRITE_TYPE_VIEW, new ExecuteInfo("", ""));
            } catch (Throwable e) {
                FRContext.getLogger().log(Level.WARNING, e.getMessage(), e);
                FRContext.getLogger().log(Level.WARNING, "RecordManager error. Record is close.");
            }
        }
    }

    private static boolean hasPrivilege(boolean isEdit, long useId, Map<String, Object> helpMap) throws JSONException {
        helpMap.put("onlyViewAuth", false);
        boolean hasLic = StableUtils.getBytes() != null;
        //sheldon 超级管理员和没有lic都是有权限的
        if (useId == UserControl.getInstance().getSuperManagerID() || !hasLic) {
            return true;
        }
        try {
            User user = UserControl.getInstance().getUser(useId);
            String userName = user.getUsername();
            JSONObject editJo = FBIConfig.getInstance().getUserAuthorAttr().getBIEditUserJo();
            JSONObject viewJo = FBIConfig.getInstance().getUserAuthorAttr().getBIViewUserJo();

            if (isEdit) {

                return editJo.has(userName) || FBIConfig.getInstance().getUserAuthorAttr().getBiEditUserLimit() == 0; //0代表不限制
            } else {
                if (!editJo.has(userName) && viewJo.has(userName)) {
                    helpMap.put("onlyViewAuth", true);
                }
                return editJo.has(userName) || viewJo.has(userName) ||
                        FBIConfig.getInstance().getUserAuthorAttr().getBiViewUserLimit() == 0;   //0代表不限制
            }

        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return true;
    }


    private static void writeData(HttpServletRequest req, HttpServletResponse res, String templatePath, String message) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        if (message != null) {
            map.put("message", message);
        }
        map.put("help", Inter.getLocText("BI-Need_Help", req.getLocale()) + "business@finereport.com");
        writeData(req, res, templatePath, map);
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

    private static JSONObject getPlateConfig() throws JSONException {
        JSONObject jo = new JSONObject();

        jo.put("chartStyle", FBIConfig.getInstance().getChartStyleAttr().getChartStyle());
//        jo.put("defaultStyle", FBIConfig.getInstance().getChartStyleAttr().getDefaultStyle());
        String defaultColor = ChartPreStyleServerManager.getInstance().getCurrentStyle();
        jo.put("defaultColor", defaultColor);

        JSONArray ja = new JSONArray();
        Iterator<String> it = ChartPreStyleServerManager.getInstance().names();
        while (it.hasNext()) {
            String name = it.next();
            ChartPreStyle style = (ChartPreStyle) ChartPreStyleServerManager.getInstance().getPreStyle(name);
            java.util.List colorList = style.getAttrFillStyle().getColorList();

            Iterator itColor = colorList.iterator();
            List colorArray = new ArrayList();
            while (itColor.hasNext()) {
                Color color = (Color) itColor.next();
                colorArray.add(toHexEncoding(color));
            }
            JSONObject nameJo = new JSONObject();
            nameJo.put("text", name).put("value", name).put("colors", colorArray);
            ja.put(nameJo);
        }
        jo.put("styleList", ja);
        return jo;
    }

    //Color转换为16进制显示
    private static String toHexEncoding(Color color) {
        String R, G, B;
        StringBuffer sb = new StringBuffer();

        R = Integer.toHexString(color.getRed());
        G = Integer.toHexString(color.getGreen());
        B = Integer.toHexString(color.getBlue());

        R = R.length() == 1 ? "0" + R : R;
        G = G.length() == 1 ? "0" + G : G;
        B = B.length() == 1 ? "0" + B : B;

        sb.append("#");
        sb.append(R);
        sb.append(G);
        sb.append(B);

        return sb.toString();
    }
}