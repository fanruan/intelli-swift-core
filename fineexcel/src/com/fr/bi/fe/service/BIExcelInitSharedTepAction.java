package com.fr.bi.fe.service;

import com.fr.bi.cal.analyze.session.BIWeblet;
import com.fr.bi.cal.stable.util.BIReportUtils;
import com.fr.bi.fe.engine.share.BIShareOtherNode;
import com.fr.bi.fe.engine.share.HSQLBIShareDAO;
import com.fr.bi.fs.BIReportNode;
import com.fr.bi.web.services.util.BIWebUtils;
import com.fr.fs.FSConfig;
import com.fr.fs.control.UserControl;
import com.fr.fs.control.systemmanager.SystemManagerFavoriteAndADHOC;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.stable.web.Weblet;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by sheldon on 15-1-19.
 * 初始化分享的界面
 */
public class BIExcelInitSharedTepAction extends ActionNoSessionCMD {
    /**
     * 执行
     *
     * @param req http请求
     * @param res http应答
     * @throws Exception 异常
     */
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String sharedId = WebUtils.getHTTPRequestParameter(req, "shared_id");
        if( StringUtils.isEmpty(sharedId) ){
            throw new Exception("没有此分享模板");
        }
        BIShareOtherNode sharedNode = HSQLBIShareDAO.getInstance().findByID(sharedId);
        if( sharedNode == null ) {
            throw new Exception("没有此分享模板");
        } else if( !sharedNode.isShared() ) {
            throw new Exception("该模板已经被取消分享");
        }
        long userId = sharedNode.getUser_id();
        long modeId = sharedNode.getMode_id();
        long templateCreateUserId = userId;

        BIReportNode node = null;
        if (templateCreateUserId == UserControl.getInstance()
                .getSuperManagerID() ) { // 管理员打开的
            try{
                node = SystemManagerFavoriteAndADHOC.getInstance()
                        .findSysBIReportNodeById(modeId);
            } catch(NullPointerException e){
                throw new RuntimeException("找不到该模板!",e);
            }
        } else {
            node = FSConfig.getInstance().getControl().getBIReportDAO()
                    .findByID(modeId);
        }

        if (node == null) {
            throw new Exception("can't find the report! might be delete or move!");
        }

        JSONObject reportSetting = BIReportUtils.getBIReportNodeJSON(node);
        reportSetting.put("id", node.getId());
        BIWebUtils.dealWidthExcelWeblet( req, res, new BIWeblet(userId), reportSetting, templateCreateUserId, node.getReportName(), node.getDescription(), node.getId() );
    }

    /**
     * 注释
     * @param req 注释
     * @param res 注释
     * @param let 注释
     * @return 注释
     */
    public static void dealWidthWeblet(HttpServletRequest req, HttpServletResponse res, Weblet let) throws Exception{
        BIWebUtils.dealWidthWeblet(req, res, let, null, ServiceUtils.getCurrentUserID(req), null, null, null);
    }


    /**
     * cmd参数值，例如op=write&cmd=sort
     *
     * @return 返回该请求的cmd参数的值
     */
    @Override
    public String getCMD() {
        return "init_shared";
    }
}