package com.fr.bi.web.dezi.services;

import com.finebi.cube.api.BICubeManager;
import com.fr.bi.base.BIUser;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.ITableSource;
import com.fr.bi.web.dezi.AbstractBIDeziAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

/**
 * Created by Young's on 2016/1/28.
 */
public class BIGetPreviewTableDataAction extends AbstractBIDeziAction {
    @Override
    public String getCMD() {
        return "get_preview_table_data";
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res,
                          String sessionID) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String tableId = WebUtils.getHTTPRequestParameter(req, "table_id");
        ITableSource source = BIConfigureManagerCenter.getDataSourceManager().getTableSourceByID(new BITableID(tableId), new BIUser(userId));
        JSONObject jo = source.createPreviewJSONFromCube(new ArrayList<String>(), BICubeManager.getInstance().fetchCubeLoader(userId));
        WebUtils.printAsJSON(res, jo);
    }
}