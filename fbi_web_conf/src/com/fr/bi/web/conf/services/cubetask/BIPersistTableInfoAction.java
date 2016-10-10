package com.fr.bi.web.conf.services.cubetask;

import com.finebi.cube.utils.CubeUpdateUtils;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by kary on 2016/8/22.
 */
public class BIPersistTableInfoAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        WebUtils.printAsJSON(res, new JSONObject().put("tableInfo", CubeUpdateUtils.recordTableAndRelationInfo(userId)));
    }
    @Override
    public String getCMD() {
        return "table_info_get";
    }


}
