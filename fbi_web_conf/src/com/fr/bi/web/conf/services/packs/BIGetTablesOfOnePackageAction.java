package com.fr.bi.web.conf.services.packs;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BISystemPackageConfigurationProvider;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.pack.data.BIBusinessPackage;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.conf.table.BusinessTableHelper;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.exception.BIFieldAbsentException;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.bridge.StableFactory;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BIGetTablesOfOnePackageAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "get_tables_of_one_package";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
        String packageId = WebUtils.getHTTPRequestParameter(req, "id");
        long userId = ServiceUtils.getCurrentUserID(req);
        BISystemPackageConfigurationProvider mgr = StableFactory.getMarkedObject(BISystemPackageConfigurationProvider.XML_TAG, BISystemPackageConfigurationProvider.class);


        JSONObject tableData = new JSONObject();
        JSONObject jo = new JSONObject();

        if (mgr.containPackageID(userId, new BIPackageID(packageId))) {
            BIBusinessPackage pack = (BIBusinessPackage) mgr.getPackage(userId, new BIPackageID(packageId));
            JSONObject packJSON = pack.createJSON();
            JSONArray tables = packJSON.getJSONArray("tables");
            for (int i = 0; i < tables.length(); i++) {
                String tableId = tables.getJSONObject(i).getString("id");
                CubeTableSource source = BusinessTableHelper.getTableDataSource(new BITableID(tableId));
                JSONObject data = source.createJSON();
                data.put("id", tableId);
                formatTableDataFields(tableId, data);
                tableData.put(tableId, data);
            }
        }

        jo.put("table_data", tableData);
        jo.put("excel_views", BIConfigureManagerCenter.getExcelViewManager().createJSON(userId));
        WebUtils.printAsJSON(res, jo);
    }

    /**
     * 通过业务包表来获得字段信息。
     * 避免数据库连接不上，导致字段丢失
     *
     * @return
     */
    public void actionCMDPassed(HttpServletRequest req,
                                HttpServletResponse res) throws Exception {
        String packageId = WebUtils.getHTTPRequestParameter(req, "id");
        long userId = ServiceUtils.getCurrentUserID(req);
        BISystemPackageConfigurationProvider mgr = StableFactory.getMarkedObject(BISystemPackageConfigurationProvider.XML_TAG, BISystemPackageConfigurationProvider.class);


        JSONObject tableData = new JSONObject();
        JSONObject jo = new JSONObject();

        if (mgr.containPackageID(userId, new BIPackageID(packageId))) {
            BIBusinessPackage pack = (BIBusinessPackage) mgr.getPackage(userId, new BIPackageID(packageId));
            Iterator<BusinessTable> it = pack.getBusinessTables().iterator();
            while (it.hasNext()) {
                BusinessTable businessTable = it.next();
                String tableId = businessTable.getID().getIdentityValue();
                CubeTableSource source = BusinessTableHelper.getTableDataSource(new BITableID(tableId));
                JSONObject data = source.createJSON();
                data.put("id", tableId);
                data.remove("fields");
                data.put("fields", fieldJSON(businessTable));
                tableData.put(tableId, data);
            }
        }

        jo.put("table_data", tableData);
        jo.put("excel_views", BIConfigureManagerCenter.getExcelViewManager().createJSON(userId));
        WebUtils.printAsJSON(res, jo);
    }

    private JSONArray fieldJSON(BusinessTable businessTable) {
        JSONArray ja = new JSONArray();
        List<JSONObject> stringList = new ArrayList<JSONObject>();
        List<JSONObject> numberList = new ArrayList<JSONObject>();
        List<JSONObject> dateList = new ArrayList<JSONObject>();
        Iterator<BusinessField> it = businessTable.getFields().iterator();
        while (it.hasNext()) {
            try {
                stringList.add(it.next().createJSON());
            } catch (Exception e) {
                BILoggerFactory.getLogger(BIGetTablesOfOnePackageAction.class).error(e.getMessage(), e);
            }
        }
        ja.put(stringList).put(numberList).put(dateList);
        return ja;
    }

    /**
     * @param tableId
     * @param tableData
     * @throws Exception
     */
    private void formatTableDataFields(String tableId, JSONObject tableData) throws Exception {
        JSONArray fields = tableData.getJSONArray("fields");
        JSONArray newFields = new JSONArray();
        for (int i = 0; i < fields.length(); i++) {
            JSONArray fs = fields.getJSONArray(i);
            JSONArray nFields = new JSONArray();
            for (int j = 0; j < fs.length(); j++) {
                JSONObject field = fs.getJSONObject(j);
                BusinessTable table = BusinessTableHelper.getBusinessTable(new BITableID(tableId));
                try {
                    field.put("id", BusinessTableHelper.getSpecificField(table, field.getString("field_name")).getFieldID().getIdentityValue());
                    field.put("table_id", tableId);
                    field.put("is_usable", BusinessTableHelper.getSpecificField(table, field.getString("field_name")).isUsable());
//                    field.put("isCircle", ((BIBusinessField)BusinessTableHelper.getSpecificField(table, field.getString("field_name"))).isCircle());
                    nFields.put(field);
                } catch (BIFieldAbsentException exception) {
                    BILoggerFactory.getLogger().error(exception.getMessage(), exception);
                }

            }
            newFields.put(nFields);
        }
        tableData.put("fields", newFields);
    }

}
