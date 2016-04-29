package com.fr.bi.fe.service;

import com.fr.bi.fe.engine.share.BIShareOtherNode;
import com.fr.bi.fe.engine.share.HSQLBIShareDAO;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 获取当前用户分享的模板，和被分享的状态
 * Created by sheldon on 15-1-21.
 */
public class BIExcelGetShareReportAction extends ActionNoSessionCMD {
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID( req );
        List list = HSQLBIShareDAO.getInstance().findSharedNodeByUser(userId);
        JSONArray reportList = new JSONArray();
        for(int i=0; i<list.size(); i++ ) {
            BIShareOtherNode node = (BIShareOtherNode) list.get(i);
            JSONObject nodeJo = new JSONObject();

            reportList.put( nodeJo );
        }

        WebUtils.printAsJSON(res, reportList);
    }

    @Override
    public String getCMD() {
        return "get_excel_share_report";
    }
}