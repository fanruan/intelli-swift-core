package com.fr.bi.web.base.services;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.web.base.AbstractBIBaseAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by GUY on 2015/4/3.
 */
public class BIGetTableAction extends AbstractBIBaseAction {
    /**
     */
    @Override
    public String getCMD() {
        return "get_table";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String id = WebUtils.getHTTPRequestParameter(req, "id");

        WebUtils.printAsJSON(res, BICubeConfigureCenter.getDataSourceManager().getTableSource(new BITableID(id)).createJSON());
    }
}