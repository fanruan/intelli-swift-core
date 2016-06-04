package com.fr.bi.web.report.services;

import com.fr.bi.fs.BIDAOUtils;
import com.fr.bi.fs.BIReportNode;

import com.fr.bi.stable.utils.algorithem.BISortUtils;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.List;

/**
 * 获取分享给我的列表
 * TODO 需要整合代码
 *
 * @author Daniel
 */
public class BIGetShare2MeReportListAction extends ActionNoSessionCMD {

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        //callback 用于判断是否是用JSONP方式跨域使用
        String callback = WebUtils.getHTTPRequestParameter(req, "callback");
        long userId = ServiceUtils.getCurrentUserID(req);
        List<BIReportNode> nodeList = BIDAOUtils.getBIReportNodesByShare2User(userId);
        BISortUtils.sortByModifyTime(nodeList);
        JSONArray ja = new JSONArray();
        Iterator<BIReportNode> iter = nodeList.iterator();
        while (iter.hasNext()) {
            BIReportNode node = iter.next();
            ja.put(node.createJSONConfig());
        }
        if (callback != null) {
            WebUtils.printAsString(res, callback + "('" + ja.toString() + "')");
        } else {
            WebUtils.printAsString(res, ja.toString());
        }
    }

    @Override
    public String getCMD() {
        return "get_share_2_me_report_list";
    }

}