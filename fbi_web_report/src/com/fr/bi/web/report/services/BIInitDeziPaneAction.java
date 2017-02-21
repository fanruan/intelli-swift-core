package com.fr.bi.web.report.services;

import com.fr.bi.cal.analyze.session.BIWeblet;
import com.fr.bi.fs.BIDAOUtils;
import com.fr.bi.fs.BIReportNode;
import com.fr.bi.manager.PerformancePlugManager;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.tool.BIReadReportUtils;
import com.fr.bi.web.base.utils.BIServiceUtil;
import com.fr.bi.web.base.utils.BIWebUtils;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.stable.collections.lazy.LazyCalculateContainer;
import com.fr.stable.collections.lazy.LazyValueCreator;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BIInitDeziPaneAction extends ActionNoSessionCMD {

    public static final String CMD = "init_dezi_pane";

    private static LazyCalculateContainer<BIReportNodeAndJSON> lazyContainer = new LazyCalculateContainer<BIReportNodeAndJSON>();


    /**
     * 注释
     *
     * @param req 注释
     * @param res 注释
     * @return 注释
     */
    public void dealWithWebPage(HttpServletRequest req, HttpServletResponse res) throws Exception {
        final String reportId = WebUtils.getHTTPRequestParameter(req, BIBaseConstant.REPORT_ID);
        final long userId = ServiceUtils.getCurrentUserID(req);
        BIReportNodeAndJSON reportNodeAndJSON =  PerformancePlugManager.getInstance().isExtremeConcurrency() ?
                lazyContainer.get(new ReportKey(reportId, userId), new LazyValueCreator<BIReportNodeAndJSON>() {
                    @Override
                    public BIReportNodeAndJSON create() throws Exception{
                        return getNodeAndJSON(reportId, userId);
                    }
                }) :  getNodeAndJSON(reportId, userId);
        BIWebUtils.dealWithWebPage(req, res, new BIWeblet(reportNodeAndJSON.getNode()), reportNodeAndJSON.getReportJSON(), reportNodeAndJSON.getNode());
    }

    @Override
    public String getCMD() {
        return CMD;
    }

    /**
     * 注释
     *
     * @param req       注释
     * @param res       注释
     * @return 注释
     */
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        BIServiceUtil.setPreviousUrl(req);
        dealWithWebPage(req, res);
    }

    private BIReportNodeAndJSON getNodeAndJSON(String reportId, long userId) throws Exception{
        BIReportNode node = BIDAOUtils.findByID(Long.parseLong(reportId), userId);
        return new BIReportNodeAndJSON(node, BIReadReportUtils.getBIReportNodeJSON(node));
    }

    private class BIReportNodeAndJSON{
        private BIReportNode node;
        private JSONObject reportJSON;

        public BIReportNodeAndJSON(BIReportNode node, JSONObject reportJSON) {
            this.node = node;
            this.reportJSON = reportJSON;
        }

        public BIReportNode getNode() {
            return node;
        }

        public JSONObject getReportJSON() {
            return reportJSON;
        }
    }

    private class ReportKey{
        private String reportId;
        private long userId;

        public ReportKey(String reportId, long userId) {
            this.reportId = reportId;
            this.userId = userId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            ReportKey reportKey = (ReportKey) o;

            if (userId != reportKey.userId) {
                return false;
            }
            return reportId != null ? ComparatorUtils.equals(reportId, reportKey.reportId) : reportKey.reportId == null;

        }

        @Override
        public int hashCode() {
            int result = reportId != null ? reportId.hashCode() : 0;
            result = 31 * result + (int) (userId ^ (userId >>> 32));
            return result;
        }
    }
}