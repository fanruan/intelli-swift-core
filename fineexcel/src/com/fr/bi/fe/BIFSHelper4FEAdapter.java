package com.fr.bi.fe;


import com.fr.bi.BIPlate;
import com.fr.bi.fe.engine.share.BIExcelTableMapper;
import com.fr.bi.fe.fs.data.FineExcelUserService;
import com.fr.bi.fe.plate.ExcelPlate;
import com.fr.bi.platform.web.fs.BIFSHelper;
import com.fr.bi.platform.web.services.fs.BIFSSignInAction;
import com.fr.bi.platform.web.services.fs.Service4BIFS;
import com.fr.data.dao.ObjectTableMapper;
import com.fr.fs.base.entity.User;
import com.fr.fs.control.UserControl;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * @author young
 *
 */
public class BIFSHelper4FEAdapter{

    public BIFSHelper4FEAdapter(){
        BIFSHelper.feAdapter = new BIFSHelper.FEAdapter() {
            @Override
            public void dealwithUserJo(JSONObject userJo, long userid) throws Exception {
//                JSONObject jo = FineExcelUserService.getInstance().checkIfBindWeiBOandQQ(userid);
                User user = UserControl.getInstance().getUser(userid);

                userJo.put("mobile", user.getMobile());         //Mobile phone
                userJo.put("email", user.getEmail());           //Email
//                userJo.put("weibo", jo.getString("weibo_id"));  //weibo
//                userJo.put("qq", jo.getString("qq_id"));        //QQ
            }
        };

        BIFSSignInAction.feSignInAdapter = new BIFSSignInAction.FESignInAdapter() {
        };

        Service4BIFS.service4BIFSAdapter = new Service4BIFS.Service4BIFSAdapter(){
            @Override
			public void dealWithWeiboBind(HttpServletRequest req, Map paraMap, long userid) throws Exception{
                //绑定微博
                String state = WebUtils.getHTTPRequestParameter(req, "state");
                boolean bind = true;
                if(state!=null){
                    if(state.equals("sina")){
                        String code = WebUtils.getHTTPRequestParameter(req, "code");
                        bind = FineExcelUserService.getInstance().bindWeibo(userid, code);
                    }else{
                        String code = WebUtils.getHTTPRequestParameter(req, "code");
                        bind = FineExcelUserService.getInstance().bindQQ(userid, code);
                    }
                }
                paraMap.put("bind", bind);
            }
        };
        //初始化biplate 里面的excelplate
        BIPlate.excelPlate = new ExcelPlate();
    }

    public ObjectTableMapper getShareOtherNode() {
        return BIExcelTableMapper.BI_SHARE_OTHER_REPORT_NODE.TABLE_MAPPER;
    }
}