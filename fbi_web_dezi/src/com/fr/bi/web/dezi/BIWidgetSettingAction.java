package com.fr.bi.web.dezi;

import com.finebi.cube.api.BICubeManager;
import com.fr.bi.cal.analyze.cal.multithread.MultiThreadManagerImpl;
import com.fr.bi.cal.analyze.report.report.BIWidgetFactory;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.stable.engine.TempPathGenerator;
import com.fr.bi.cal.stable.loader.CubeTempModelReadingTableIndexLoader;
import com.fr.bi.conf.report.BIReport;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.stable.data.BITableID;
import com.fr.json.JSONObject;
import com.fr.web.core.ErrorHandlerHelper;
import com.fr.web.core.SessionDealWith;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 小灰灰 on 2015/7/10.
 */
public class BIWidgetSettingAction extends AbstractBIDeziAction {

    public static final String CMD = "widget_setting";

    /**
     * 注释
     *
     * @param req       注释
     * @param res       注释
     * @param sessionID 注释
     * @return 注释
     */
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
            loader.registerTableIndex(Thread.currentThread().getId(), loader.getTableIndex(new BITableID("")));
        }
        JSONObject json = parseJSON(req);
        String widgetName = json.optString("name");
        BIWidget widget = BIWidgetFactory.parseWidget(json, userId);
        BIReport biReport = sessionIDInfor.getBIReport();
        int index = biReport.getWidgetIndexByName(widgetName);
        biReport.setWidget(index, widget);
        JSONObject jo = JSONObject.create();
        try {
            MultiThreadManagerImpl.getInstance().refreshExecutorService();
            jo = widget.createDataJSON(sessionIDInfor);
        } catch (Exception exception) {
            jo.put("error", exception.getMessage());
        }
        if (sessionIDInfor.getLoader() instanceof CubeTempModelReadingTableIndexLoader) {
            CubeTempModelReadingTableIndexLoader loader = (CubeTempModelReadingTableIndexLoader) sessionIDInfor.getLoader();
            loader.releaseTableIndex(Thread.currentThread().getId());
            TempPathGenerator.removeTempPath(Thread.currentThread().getId());
        }
        BICubeManager.getInstance().fetchCubeLoader(userId).releaseCurrentThread();
        WebUtils.printAsJSON(res, jo);
    }

    protected JSONObject parseJSON(HttpServletRequest req) throws Exception {
        return new JSONObject(WebUtils.getHTTPRequestParameter(req, "widget"));
    }


    /**
     * @see com.fr.web.core.AcceptCMD#getCMD()
     */
    @Override
    public String getCMD() {
        return CMD;
    }
}