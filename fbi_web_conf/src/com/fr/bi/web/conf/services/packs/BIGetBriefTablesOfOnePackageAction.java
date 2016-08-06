package com.fr.bi.web.conf.services.packs;

import com.finebi.cube.conf.BISystemPackageConfigurationProvider;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.pack.data.BIBusinessPackage;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.finebi.cube.conf.table.BusinessTableHelper;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.bridge.StableFactory;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

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
                JSONObject sourceTable = BusinessTableHelper.getTableDataSource(new BITableID(tableId)).createJSON();
                List<BusinessField> fieldList = BusinessTableHelper.getTableFields(BusinessTableHelper.getBusinessTable(new BITableID(tableId)));
                JSONArray fields = new JSONArray();
                for(int j = 0; j < fieldList.size(); j++) {
                    fields.put(fieldList.get(j).createJSON());
                }
                JSONArray allFields = new JSONArray();
                allFields.put(fields).put(new JSONArray()).put(new JSONArray());
                sourceTable.put("fields", allFields);
                tableData.put(tableId, sourceTable);
            }
            WebUtils.printAsJSON(res, tableData);
        }
    }
}