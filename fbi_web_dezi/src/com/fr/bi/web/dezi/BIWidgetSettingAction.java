package com.fr.bi.web.dezi;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.CubeGenerationManager;
import com.finebi.cube.data.disk.BICubeDiskPrimitiveDiscovery;
import com.fr.bi.CubeReaderCacheUtils;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.analyze.cal.multithread.MultiThreadManagerImpl;
import com.fr.bi.cal.analyze.report.report.BIWidgetFactory;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.stable.engine.TempPathGenerator;
import com.fr.bi.cal.stable.loader.CubeTempModelReadingTableIndexLoader;
import com.fr.bi.cluster.retry.RetryLoop;
import com.fr.bi.cluster.retry.RetryNTimes;
import com.fr.bi.conf.report.BIReport;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.fr.bi.manager.PerformancePlugManager;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.utils.code.BIPrintUtils;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.web.core.ErrorHandlerHelper;
import com.fr.web.core.SessionDealWith;
import com.fr.web.utils.WebUtils;
import com.google.common.util.concurrent.UncheckedExecutionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.Callable;

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
            loader.registerTableIndex(Thread.currentThread().getId(), loader.getTableIndex(BIModuleUtils.getSourceByID(new BITableID(sessionIDInfor.getTempTableId()), new BIUser(sessionIDInfor.getUserId()))));
        }
        JSONObject json = parseJSON(req);
        String widgetName = json.optString("name");
        json.put("sessionID", sessionID);
        BIWidget widget = BIWidgetFactory.parseWidget(json, userId);
        BIReport biReport = sessionIDInfor.getBIReport();
        int index = biReport.getWidgetIndexByName(widgetName);
        biReport.setWidget(index, widget);
        JSONObject jo = JSONObject.create();
        try {
            MultiThreadManagerImpl.getInstance().refreshExecutorService();
            jo = widget.createDataJSON(sessionIDInfor);
        } catch (Exception exception) {
            BILoggerFactory.getLogger(BIWidgetSettingAction.class).error(exception.getMessage(), exception);
//            IndexOutOfBoundsException,需要释放资源然后重新访问（并且当前没有cube正在生成），其他异常直接重新访问，
            if (isIndexOutOfBoundsException(exception)&& CubeGenerationManager.getCubeManager().hasTask()) {
                BILoggerFactory.getLogger(BIWidgetSettingAction.class).info("start clear readers");
                BICubeDiskPrimitiveDiscovery.getInstance().forceRelease();
                BICubeDiskPrimitiveDiscovery.getInstance().finishRelease();
                CubeReaderCacheUtils.clearUserMapCache();
                BILoggerFactory.getLogger(BIWidgetSettingAction.class).info("readers clear finished");
                jo = retry(jo, widget, sessionIDInfor);
            }else {
                jo = retry(jo, widget, sessionIDInfor);
            }
        }
        if (sessionIDInfor.getLoader() instanceof CubeTempModelReadingTableIndexLoader) {
            CubeTempModelReadingTableIndexLoader loader = (CubeTempModelReadingTableIndexLoader) sessionIDInfor.getLoader();
            loader.releaseTableIndex(Thread.currentThread().getId());
            TempPathGenerator.removeTempPath(Thread.currentThread().getId());
        }
        sessionIDInfor.getLoader().releaseCurrentThread();
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


    private boolean isIndexOutOfBoundsException(Exception exception){
        if((exception instanceof UncheckedExecutionException && exception.getCause().getCause() instanceof IndexOutOfBoundsException)
                || (exception instanceof RuntimeException && exception.getCause() instanceof IndexOutOfBoundsException)){
            return true;
        }
        return false;
    }

    private JSONObject retry(JSONObject jo, final BIWidget widget, final BISession sessionIDInfor) throws JSONException {
        Callable task = new Callable<JSONObject>() {
            @Override
            public JSONObject call() throws Exception {
                MultiThreadManagerImpl.getInstance().refreshExecutorService();
                return widget.createDataJSON(sessionIDInfor);
            }
        };
        RetryLoop retryLoop = new RetryLoop();
        retryLoop.initial(new RetryNTimes(PerformancePlugManager.getInstance().getRetryMaxTimes(), PerformancePlugManager.getInstance().getRetryMaxSleepTime()));
        try {
            BILoggerFactory.getLogger(BIWidgetSettingAction.class).info("retry widget_setting");
            jo = (JSONObject) RetryLoop.retry(task, retryLoop);
        } catch (Exception e) {
            jo.put("error", BIPrintUtils.outputException(e));
        }
        return jo;
    }

}