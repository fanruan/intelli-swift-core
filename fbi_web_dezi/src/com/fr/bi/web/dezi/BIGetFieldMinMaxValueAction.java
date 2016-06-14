package com.fr.bi.web.dezi;

import com.finebi.cube.api.BICubeManager;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.field.BusinessFieldHelper;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.BIFieldID;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by User on 2016/6/14.
 */
public class BIGetFieldMinMaxValueAction extends AbstractBIDeziAction {

    public static final String CMD = "dezi_get_field_min_max_value";

    @Override
    public String getCMD() {
        return CMD;
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        ICubeDataLoader loader = BICubeManager.getInstance().fetchCubeLoader(userId);
        String fieldIdString = WebUtils.getHTTPRequestParameter(req, "id");
        BusinessField field = BusinessFieldHelper.getBusinessFieldSource(new BIFieldID(fieldIdString));
        JSONObject jo = new JSONObject();
        if (field.getFieldType() == DBConstant.COLUMN.NUMBER) {
            ICubeTableService ti = loader.getTableIndex(field.getTableBelongTo().getTableSource());
            jo.put(BIJSONConstant.JSON_KEYS.FILED_MAX_VALUE, ti != null ? ti.getMAXValue(loader.getFieldIndex(field)) : 0);
            jo.put(BIJSONConstant.JSON_KEYS.FIELD_MIN_VALUE, ti != null ? ti.getMINValue(loader.getFieldIndex(field)) : 0);
        }
        WebUtils.printAsJSON(res, jo);
    }
}
