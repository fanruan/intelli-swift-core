/**
 * 
 */
package com.fr.bi.conf.data.source.operator.add.express;

import com.fr.bi.base.BIBasicCore;
import com.fr.bi.base.BICore;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.BICoreGenerator;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.common.persistent.xml.BIIgnoreField;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.utils.DateUtils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.json.JSONObject;

import java.text.ParseException;

/**
 * @author Daniel
 *
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
		if (isUnitLeft){
			jo.put("other", value);
		}
		return jo;
	}

    @Override
    public BICore fetchObjectCore() {

        try {
            return new BICoreGenerator(this).fetchObjectCore();
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return BIBasicCore.EMPTY_CORE;
    }

	@Override
	public Object get(ICubeTableService ti, int row, int columnType) {
		return isUnitLeft ? getTransValue(columnType) : null;
	}
    private Object getTransValue(int columnType) {
        if (tValue == null){
            try {
                tValue = columnType == DBConstant.COLUMN.DATE ? DateUtils.parse((String) value).getTime() : value;
            } catch (ParseException e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }
        return tValue;
    }
}