package com.fr.bi.fe.service;

import com.fr.bi.fe.engine.share.BIShareOtherNode;
import com.fr.bi.fe.engine.share.HSQLBIShareDAO;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by sheldon on 15-1-19.
 */
public class BIGetSharedUrlAction extends ActionNoSessionCMD {
    /**
     * 执行
     *
     * @param req       http请求
     * @param res       http应答
     * @throws Exception
     */
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        int mode_id = WebUtils.getHTTPRequestIntParameter(req, "mode_id");
        long userId = ServiceUtils.getCurrentUserID(req);

        String sharedID = null;
        StringBuffer sharedUrl =  new StringBuffer("抱歉，获取url失败");
//        String boxName = FRContext.getCurrentEnv().getAppName();
        try {
            BIShareOtherNode shareOtherNode = HSQLBIShareDAO.getInstance().findSharedNodeByUserAndMode(userId, mode_id);

            //代表数据库里面没有这条数据，则加进去
            if( shareOtherNode == null ) {
                shareOtherNode = new BIShareOtherNode( userId, mode_id );
            } else {
                //获取分享时，则强制修改分享的状态为true
                shareOtherNode.setShared( true );
            }
            HSQLBIShareDAO.getInstance().saveOrUpdate( shareOtherNode );
            sharedID = shareOtherNode.getShared_id();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        if( sharedID != null ) {
//            sharedUrl = new StringBuffer();
//            sharedUrl.append( ConfigManager.getInstance().getServerAddress() ).append("/").
//                    append(GeneralContext.getCurrentAppNameOfEnv()).append("/").append( ConfigManager.getInstance().getServletMapping() ).
//                    append("?op=fr_excel&cmd=init_shared&shared_id=").append( sharedID );
//        }

        WebUtils.printAsString( res, sharedUrl.toString() );
    }

    /**
     * cmd参数值，例如op=write&cmd=sort
     *
     * @return 返回该请求的cmd参数的值
     */
    @Override
    public String getCMD() {
        return "bi_get_shared_url";
    }
}