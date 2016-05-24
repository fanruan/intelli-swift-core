package com.fr.bi.web.conf.services.packs;

import com.fr.bi.base.BIUser;
import com.fr.bi.conf.base.pack.data.BIBusinessPackage;
import com.fr.bi.conf.base.pack.data.BIPackageID;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.bridge.StableFactory;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BIGetBriefTablesOfOnePackageAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "get_brief_tables_of_one_package";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
        String packageId = WebUtils.getHTTPRequestParameter(req, "id");
        long userId = ServiceUtils.getCurrentUserID(req);
        BISystemPackageConfigurationProvider mgr = StableFactory.getMarkedObject(BISystemPackageConfigurationProvider.XML_TAG, BISystemPackageConfigurationProvider.class);

        BIBusinessPackage pack = (BIBusinessPackage) mgr.getPackage(userId, new BIPackageID(packageId));

        if (pack == null) {
            WebUtils.printAsJSON(res, new JSONObject());
        } else {
            JSONObject tableData = new JSONObject();
            JSONObject packJSON = pack.createJSON();
            JSONArray tables = packJSON.getJSONArray("tables");
            for (int i = 0; i < tables.length(); i++) {
                String tableId = tables.getJSONObject(i).getString("id");
                tableData.put(tableId, BIConfigureManagerCenter.getDataSourceManager().getTableSourceByID(new BITableID(tableId), new BIUser(userId)).createJSON());
            }
            WebUtils.printAsJSON(res, tableData);
        }
    }
}