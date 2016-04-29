package com.fr.bi.fe.service;

import com.fr.bi.fe.engine.share.BIShareOtherNode;
import com.fr.bi.fe.engine.share.HSQLBIShareDAO;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 设置分享的模板的被分享状态
 * Created by sheldon on 15-1-21.
 */
public class BIExcelSetShareReportStatusAction extends ActionNoSessionCMD {
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        long mode_id = WebUtils.getHTTPRequestIntParameter(req, "mode_id");
        boolean isShared = Boolean.valueOf(WebUtils.getHTTPRequestParameter(req, "isShared"));

        BIShareOtherNode node = HSQLBIShareDAO.getInstance().findSharedNodeByUserAndMode(userId, mode_id);
        if( node != null ) {
            node.setShared( isShared );
            HSQLBIShareDAO.getInstance().saveOrUpdate( node );

            WebUtils.printAsString(res, "true");
        }
    }

    @Override
    public String getCMD() {
        return "set_share_report_status";
    }
}