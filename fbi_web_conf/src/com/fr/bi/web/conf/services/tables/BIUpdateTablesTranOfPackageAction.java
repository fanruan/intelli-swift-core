package com.fr.bi.web.conf.services.tables;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;

/**
 * 转义可能出现重名，需要再保存一次
 * Created by Young's on 2017/2/16.
 */
public class BIUpdateTablesTranOfPackageAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String transStr = WebUtils.getHTTPRequestParameter(req, "translations");
        JSONObject transJO = new JSONObject(transStr);
        Iterator<String> ids = transJO.keys();
        while (ids.hasNext()) {
            String id = ids.next();
            BICubeConfigureCenter.getAliasManager().getTransManager(userId).setTransName(id, transJO.getString(id));
        }
        BICubeConfigureCenter.getAliasManager().persistData(userId);
    }

    @Override
    public String getCMD() {
        return "update_tables_tran_of_package";
    }
}
