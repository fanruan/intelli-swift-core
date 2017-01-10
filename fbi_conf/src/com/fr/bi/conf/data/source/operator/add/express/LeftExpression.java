/**
 *
 */
package com.fr.bi.conf.data.source.operator.add.express;

import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.base.BIBasicCore;
import com.fr.bi.base.BICore;
import com.fr.bi.base.BICoreGenerator;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.common.persistent.xml.BIIgnoreField;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.utils.DateUtils;
import com.fr.json.JSONObject;

import java.text.ParseException;

/**
 * @author Daniel
 */
public class LeftExpression implements Expression {

    @BICoreField
    private boolean isUnitLeft = false;
    @BICoreField
    private Object value;
    @BIIgnoreField
    private transient Object tValue;

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        value = jo.optString("other", null);
        isUnitLeft = jo.getBoolean("showOther");
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("showOther", isUnitLeft);
        if (isUnitLeft) {
            jo.put("other", value);
        }
        return jo;
    }

    @Override
    public BICore fetchObjectCore() {

        try {
            return new BICoreGenerator(this).fetchObjectCore();
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return BIBasicCore.EMPTY_CORE;
    }

    @Override
    public Object get(ICubeTableService ti, int row, int columnType) {
        return isUnitLeft ? getTransValue(columnType) : null;
    }

    private Object getTransValue(int columnType) {
        if (tValue == null) {
            try {
                if (columnType == DBConstant.COLUMN.DATE) {
                    tValue = DateUtils.parse((String) value).getTime();
                } else if (columnType == DBConstant.COLUMN.NUMBER) {
                    tValue = Double.valueOf((String) value);
                } else {
                    tValue = value;
                }
            } catch (ParseException e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
            }
        }
        return tValue;
    }
}