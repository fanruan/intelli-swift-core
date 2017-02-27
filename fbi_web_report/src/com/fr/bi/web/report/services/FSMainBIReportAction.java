package com.fr.bi.web.report.services;

import com.fr.bi.cal.analyze.session.BIWeblet;
import com.fr.bi.fs.BIDAOUtils;
import com.fr.bi.fs.BIReportNode;
import com.fr.bi.manager.PerformancePlugManager;
import com.fr.bi.tool.BIReadReportUtils;
import com.fr.bi.web.base.utils.BIWebUtils;
import com.fr.fs.control.UserControl;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.stable.collections.lazy.LazyCalculateContainer;
import com.fr.stable.collections.lazy.LazyValueCreator;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 查看BI的service
 *
 * @author Daniel-pc
 */
public class FSMainBIReportAction extends ActionNoSessionCMD {

    public static final String CMD = "bi_init";

    private static LazyCalculateContainer<BIReportNodeAndSetting> lazyContainer = new LazyCalculateContainer<BIReportNodeAndSetting>();

    @Override
    public String getCMD() {
        return CMD;
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res)
            throws Exception {
        final String reportId = WebUtils.getHTTPRequestParameter(req, "id");
        final long currentLoginUserId = ServiceUtils.getCurrentUserID(req);
        if (currentLoginUserId < 0 && currentLoginUserId != UserControl.getInstance().getSuperManagerID()) {
            return;
        }
        String createUserId = WebUtils.getHTTPRequestParameter(req, "createBy");
        final long templateCreateUserId = Long.parseLong(createUserId);
        BIReportNodeAndSetting reportNodeAndSetting = PerformancePlugManager.getInstance().isExtremeConcurrency() ?
                lazyContainer.get(new ReportKey(reportId, templateCreateUserId), new LazyValueCreator<BIReportNodeAndSetting>() {
                    @Override
                    public BIReportNodeAndSetting create() throws Exception {
                        return getNodeAndJSON(reportId, templateCreateUserId);
                    }
                }) : getNodeAndJSON(reportId, templateCreateUserId);
        BIReportNode node = reportNodeAndSetting.getNode().clone();
        BIWebUtils.dealWithWebPage(req, res, new BIWeblet(node), new JSONObject(reportNodeAndSetting.getReportSetting()), node);
    }


    private BIReportNodeAndSetting getNodeAndJSON(String reportId, long createBy) throws Exception {
        BIReportNode node = BIDAOUtils.findByID(Long.parseLong(reportId), createBy);
        if (node == null) {
            throw new RuntimeException("can't find the report! might be delete or move!");
        }
        return new BIReportNodeAndSetting(node, BIReadReportUtils.getBIReportNodeSetting(node));
    }

    private class BIReportNodeAndSetting {
        private BIReportNode node;
        private String reportSetting;

        public BIReportNodeAndSetting(BIReportNode node, String reportSetting) {
            this.node = node;
            this.reportSetting = reportSetting;
        }

        public BIReportNode getNode() {
            return node;
        }

        public String getReportSetting() {
            return reportSetting;
        }
    }

    private class ReportKey {
        private String reportId;
        private long userId;

        public ReportKey(String reportId, long userId) {
            this.reportId = reportId;
            this.userId = userId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
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