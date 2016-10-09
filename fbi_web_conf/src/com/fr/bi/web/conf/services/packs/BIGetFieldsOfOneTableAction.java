package com.fr.bi.web.conf.services.packs;

import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.conf.table.BusinessTableHelper;
import com.fr.bi.exception.BIFieldAbsentException;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.AbstractTableSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by roy on 16/9/13.
 */
public class BIGetFieldsOfOneTableAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String tableId = WebUtils.getHTTPRequestParameter(req, "id");
        JSONObject jo = new JSONObject();
        CubeTableSource source = BusinessTableHelper.getTableDataSource(new BITableID(tableId));
        ((AbstractTableSource) source).reGetBiTable();
        if (source.getPersistentTable() == null) {
            JSONObject errorJson = new JSONObject();
            errorJson.put("none_table", true);
            WebUtils.printAsJSON(res, errorJson);
            return;
        }
        ((AbstractTableSource) source).getFields();
        JSONObject data = source.createJSON();
        formatTableDataFields(tableId, data);
        jo.put("table_data", data);
        WebUtils.printAsJSON(res, jo);
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
                    nFields.put(field);
                } catch (BIFieldAbsentException exception) {
                    BILoggerFactory.getLogger().error(exception.getMessage(), exception);
                }

            }
            newFields.put(nFields);
        }
        tableData.put("fields", newFields);
    }

    @Override
    public String getCMD() {
        return "get_fields_of_one_table";
    }
}
