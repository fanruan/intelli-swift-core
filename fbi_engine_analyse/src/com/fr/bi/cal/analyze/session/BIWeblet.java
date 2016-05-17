package com.fr.bi.cal.analyze.session;

import com.fr.base.FRContext;
import com.fr.bi.fs.BIReportNode;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.ComparatorUtils;
import com.fr.general.Inter;
import com.fr.general.web.ParameterConsts;
import com.fr.stable.Constants;
import com.fr.stable.web.SessionProvider;
import com.fr.stable.web.Weblet;
import com.fr.web.core.ReportWebUtils;
import com.fr.web.core.SessionDealWith;
import com.fr.web.core.action.PDFPrintPrintAction;
import com.fr.web.core.reserve.ExportService;
import com.fr.web.core.reserve.FormletDealWith;
import com.fr.web.core.reserve.ReportletDealWith;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 用于处理进入设计页面的sessionID
 *
 * @author Daniel-pc
 */
public class BIWeblet implements Weblet {
	
	private BIReportNode node;

    /**
     * 构造函数
     */
    public BIWeblet(BIReportNode node) {
    	this.node = node;
    }

    public BIWeblet() {
    }

    /**
     * 创建Session
     */
    @Override
    public SessionProvider createSessionIDInfor(HttpServletRequest req,
                                                String remoteAddress, Map parameterMap4Execute) throws Exception {
        return new BISession(remoteAddress, this, ServiceUtils.getCurrentUserID(req), req.getLocale(), node);
    }

    /**
     * 是否为Form
     */
    public boolean isForm() {
        return false;
    }

    /**
     * 是否为图表
     */
    public boolean isChart() {
        return false;
    }

    /**
     * 是否占并发
     */
    @Override
    public boolean isSessionOccupy() {
        return true;
    }

    /**
     * 生成报表
     */
    @Override
    public void dealWeblet(HttpServletRequest req, HttpServletResponse res)
            throws Exception {
        HttpSession s = req.getSession(false);
        if (s != null) {
            Object roleName = s.getAttribute(Constants.P.PRIVILEGE_AUTHORITY);
            if (roleName != null) {
                FRContext.getLogger().info(Inter.getLocText("INFO-Current_Role") + ":" + roleName);
            }
        }
        if (SessionDealWith.generateSessionID_isPromptRegisted(req)) {
            Weblet let = com.fr.web.factory.WebletFactory.createEmbeddedWeblet("/com/fr/web/tpl/lic.frm");
            FormletDealWith.dealWithFormlet(req, res, let);
            return;
        }
        dealWithBIWeblet(req, res);
    }

    private void dealWithBIWeblet(HttpServletRequest req,
                                  HttpServletResponse res) throws Exception {
        String sessionID = WebUtils.getHTTPRequestParameter(req, ParameterConsts.SESSION_ID);

        if (sessionID == null || !SessionDealWith.hasSessionID(sessionID)) {
            return;
        }
        if (WebUtils.getHTTPRequestParameter(req, "format") != null) {
            // 这里检查是不是输出成其他的形式,比如PDF, PDF_Activex, Excel等等..
            String format = WebUtils.getHTTPRequestParameter(req, "format");

            if (ComparatorUtils.equals(format, "pdfprint")) {
                new PDFPrintPrintAction().doAction(req, res);
            } else {
                String embedParameter = WebUtils.getHTTPRequestParameter(req, ParameterConsts.EXPORT_PDF_EMBED);
                boolean embed = "true".equals(embedParameter); // 当且仅当参数值是true时,才嵌入
                ExportService.dealWithExport(req, res, sessionID, embed);
            }
        } else {
            dealWithPageHtml(req, res, sessionID);
        }
    }

    private void dealWithPageHtml(HttpServletRequest req,
                                  HttpServletResponse res, String sessionID) {
        BIAbstractSession sessionIDInfor = (BIAbstractSession) SessionDealWith.getSessionIDInfor(sessionID);
        if (sessionIDInfor == null) {// Session timeout.
            return;
        }

        java.util.Map map4Tpl = ReportWebUtils.context4PageTpl(req, sessionIDInfor);

        // 防止浏览器前进和后退时直接用缓存里面的东西，导致sessiontimeout, 刷新时可能也有这个问题
        res.setHeader("Pragma", "No-cache");
        res.setHeader("Cache-Control", "no-cache, no-store");
        res.setDateHeader("Expires", -10);
        try {
            ReportletDealWith.dealWithReportlet(req, res, this);
        } catch (Exception e) {
            FRContext.getLogger().error(e.getMessage(), e);
        }
    }

    /* (non-Javadoc)
     * @see com.fr.stable.web.Weblet#setTplPath(java.lang.String)
     */
    @Override
    public void setTplPath(String tplPath) {


    }

    /* (non-Javadoc)
     * @see com.fr.stable.web.Weblet#setParameterMap(java.util.Map)
     */
    @Override
    public void setParameterMap(Map map) {
    }
}