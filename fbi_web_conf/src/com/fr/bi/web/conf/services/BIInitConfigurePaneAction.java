package com.fr.bi.web.conf.services;

import com.fr.base.ExcelUtils;
import com.fr.bi.web.base.utils.BIServiceUtil;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.bi.web.conf.services.session.BIConfWeblet;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.GeneralUtils;
import com.fr.general.VT4FR;
import com.fr.general.web.ParameterConsts;
import com.fr.web.core.SessionDealWith;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

public class BIInitConfigurePaneAction extends AbstractBIConfigureAction {

    public static final String CMD = "init_configure_pane";

    @Override
    public String getCMD() {
        return CMD;
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String sessionID = SessionDealWith.generateSessionID(req, res, new BIConfWeblet(userId));

        HashMap<String, String> data = new HashMap<String, String>();
        data.put(ParameterConsts.SESSION_ID, sessionID);
        //jar包版本
        data.put("__v__", GeneralUtils.readBuildNO());
        //多sheet支持
        data.put("supportSheets", String.valueOf(VT4FR.WORK_BOOK.support()));
        data.put("supportExcelVersion", ExcelUtils.checkPOIJarExist() ? "2007" : "2003");
//        data.put("supportMultiSheet", "");
        BIServiceUtil.setPreviousUrl(req);
        WebUtils.writeOutTemplate("/com/fr/bi/web/html/bi_conf.html", res, data);
    }

}