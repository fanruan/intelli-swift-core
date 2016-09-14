package com.fr.bi.web.dezi;

import com.fr.bi.base.BIUser;
import com.fr.bi.cal.analyze.cal.multithread.MultiThreadManagerImpl;
import com.fr.bi.cal.analyze.report.report.BIWidgetFactory;
import com.fr.bi.cal.analyze.report.report.widget.MultiChartWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.BIChartDataConvertFactory;
import com.fr.bi.cal.analyze.report.report.widget.chart.BIChartSettingFactory;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.stable.engine.TempPathGenerator;
import com.fr.bi.cal.stable.loader.CubeTempModelReadingTableIndexLoader;
import com.fr.bi.conf.report.BIReport;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.code.BIPrintUtils;
import com.fr.json.JSONObject;
import com.fr.web.core.ErrorHandlerHelper;
import com.fr.web.core.SessionDealWith;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by User on 2016/8/31.
 */
public class BIChartSettingAction extends AbstractBIDeziAction {
    public static final String CMD = "chart_setting";

    @Override
    public String getCMD() {
        return CMD;
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        BISession sessionIDInfor = (BISession) SessionDealWith.getSessionIDInfor(sessionID);
        if (sessionIDInfor == null) {
            ErrorHandlerHelper.getErrorHandler().error(req, res, "Reportlet SessionID: \"" + sessionID + "\" time out.");
            return;
        }
        long userId = sessionIDInfor.getUserIdFromSession(req);
        MultiThreadManagerImpl.getInstance().refreshExecutorService();
        if (sessionIDInfor.getLoader() instanceof CubeTempModelReadingTableIndexLoader) {
            CubeTempModelReadingTableIndexLoader loader = (CubeTempModelReadingTableIndexLoader) sessionIDInfor.getLoader();
            loader.registerTableIndex(Thread.currentThread().getId(), loader.getTableIndex(BIModuleUtils.getSourceByID(new BITableID(sessionIDInfor.getTempTableId()), new BIUser(sessionIDInfor.getUserId()))));
        }
        JSONObject json = parseJSON(req);
        String widgetName = json.optString("name");
        json.put("sessionId", sessionID);
        BIWidget widget = BIWidgetFactory.parseWidget(json, userId);
        BIReport biReport = sessionIDInfor.getBIReport();
        int index = biReport.getWidgetIndexByName(widgetName);
        biReport.setWidget(index, widget);
        JSONObject jo = JSONObject.create();
        try {
            MultiThreadManagerImpl.getInstance().refreshExecutorService();
            jo = widget.createDataJSON(sessionIDInfor);
        } catch (Exception exception) {
            BILogger.getLogger().error(exception.getMessage(), exception);
            jo.put("error", BIPrintUtils.outputException(exception));
        }
        if (sessionIDInfor.getLoader() instanceof CubeTempModelReadingTableIndexLoader) {
            CubeTempModelReadingTableIndexLoader loader = (CubeTempModelReadingTableIndexLoader) sessionIDInfor.getLoader();
            loader.releaseTableIndex(Thread.currentThread().getId());
            TempPathGenerator.removeTempPath(Thread.currentThread().getId());
        }
        sessionIDInfor.getLoader().releaseCurrentThread();

        JSONObject configs = BIChartDataConvertFactory.convert((MultiChartWidget) widget, jo.getJSONObject("data"));

        try {
            BIChartSettingFactory.parseChartSetting((MultiChartWidget)widget, configs.getJSONArray("data"), configs.optJSONObject("options"), configs.getJSONArray("types"));
        }catch (Exception e){
            BILogger.getLogger().error(e.getMessage());
        }


        WebUtils.printAsJSON(res, jo);
    }

    protected JSONObject parseJSON(HttpServletRequest req) throws Exception {
        return new JSONObject(WebUtils.getHTTPRequestParameter(req, "widget"));
    }
}
