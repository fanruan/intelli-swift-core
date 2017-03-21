package com.fr.bi.web.conf.services.tables;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.conf.table.BusinessTableHelper;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.control.UserControl;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by Young's on 2017/3/17.
 */
public class BIGetTablesInfoByIdsAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String tableIds = WebUtils.getHTTPRequestParameter(req, "ids");

        JSONArray ids = new JSONArray(tableIds);
        JSONObject jo = new JSONObject();
        long userId = UserControl.getInstance().getSuperManagerID();
        for (int i = 0; i < ids.length(); i++) {
            String id = ids.getString(i);
            BusinessTable table = BusinessTableHelper.getBusinessTable(new BITableID(id));
            JSONObject tableJO = table.createJSON();
            List<BusinessField> fieldList = table.getFields();
            JSONArray fields = new JSONArray();
            for (BusinessField field : fieldList) {
                JSONObject fieldJO = field.createJSON();
                fieldJO.put(BIJSONConstant.JSON_KEYS.TRAN_NAME, BICubeConfigureCenter.getAliasManager().getAliasName(field.getFieldID().getIdentityValue(), userId));
                fields.put(fieldJO);
            }
            tableJO.put("fields", fields);
            tableJO.put(BIJSONConstant.JSON_KEYS.TRAN_NAME, BICubeConfigureCenter.getAliasManager().getAliasName(table.getID().getIdentityValue(), userId));
            jo.put(id, tableJO);
        }

        WebUtils.printAsJSON(res, jo);
    }

    @Override
    public String getCMD() {
        return "get_tables_info_by_ids";
    }
}
