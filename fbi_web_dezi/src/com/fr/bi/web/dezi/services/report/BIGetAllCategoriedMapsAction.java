package com.fr.bi.web.dezi.services.report;

import com.fr.base.MapXMLHelper;
import com.fr.bi.web.dezi.AbstractBIDeziAction;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BIGetAllCategoriedMapsAction extends AbstractBIDeziAction {

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res,
                          String sessionID) throws Exception {
        MapXMLHelper helper = MapXMLHelper.getInstance();
        WebUtils.printAsJSON(res, helper.asJsonOfCategoriedMaps());
    }

    @Override
    public String getCMD() {
        return "get_all_categoried_maps";
    }

}