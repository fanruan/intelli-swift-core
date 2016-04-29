package com.fr.bi.web.report.services;

import com.fr.bi.cal.analyze.session.BIWeblet;
import com.fr.bi.cal.stable.utils.BIReportUtils;
import com.fr.bi.fs.BIDAOUtils;
import com.fr.bi.fs.BIReportNode;
import com.fr.bi.web.base.utils.BIWebUtils;
import com.fr.fs.control.UserControl;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
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

    @Override
    public String getCMD() {
        return CMD;
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res)
            throws Exception {
        String id = WebUtils.getHTTPRequestParameter(req, "id");
        long currentLoginUserId = ServiceUtils.getCurrentUserID(req);
        String createUserId = WebUtils.getHTTPRequestParameter(req, "createBy");
        long templateCreateUserId = Long.parseLong(createUserId);
        BIReportNode node = null;
        if (id != null) {
            node = BIDAOUtils.findByID(Long.parseLong(id), templateCreateUserId);
        }

        dealWithEntryResourceRequest(req, res, currentLoginUserId, node);
    }

    private void dealWithEntryResourceRequest(HttpServletRequest req,
                                              HttpServletResponse res,
                                              long userId,
                                              BIReportNode node) throws Exception {
        if (userId < 0 && userId != UserControl.getInstance().getSuperManagerID()) {
            return;
        }
        if (node == null) {
            throw new Exception("can't find the report! might be delete or move!");
        }
        JSONObject reportSetting = BIReportUtils.getBIReportNodeJSON(node);

        BIWebUtils.dealWithWebPage(req, res, new BIWeblet(node), reportSetting, node);

    }
}