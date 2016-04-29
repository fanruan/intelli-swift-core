package com.fr.bi.web.conf.services;


import com.fr.bi.web.conf.AbstractBIConfigureAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 小灰灰 on 2014/9/11.
 * 保存当前table设置的excel
 */
public class BISaveExcelViewAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
//    	long userId = ServiceUtils.getCurrentUserID(req);
//    	BIInterfaceAdapter.getBIConnectionAdapter().saveExcelView(userId);
    }

    @Override
    public String getCMD() {
        return "save_excel_view";
    }
}