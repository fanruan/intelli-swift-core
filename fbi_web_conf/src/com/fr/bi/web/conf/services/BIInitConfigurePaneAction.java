package com.fr.bi.web.conf.services;

import com.fr.base.ExcelUtils;
import com.fr.bi.web.base.utils.BIServiceUtil;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.stable.ViewActor;
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
        HashMap<String, String> data = new HashMap<String, String>();
        //多sheet支持
        data.put("supportSheets", String.valueOf(new ViewActor().getBookFUNC().support()));
        data.put("supportExcelVersion", ExcelUtils.checkPOIJarExist() ? "2007" : "2003");
//        data.put("supportMultiSheet", "");
        BIServiceUtil.setPreviousUrl(req);
        WebUtils.writeOutTemplate("/com/fr/bi/web/html/bi_conf.html", res, data);
    }

}