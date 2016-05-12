package com.fr.bi.conf.data.source.operator.add;

import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

/**
 * Created by 小灰灰 on 2016/5/9.
 */
public class ETLFormularOperator extends FieldFormulaOperator{
    public ETLFormularOperator(long userId) {
        super(userId);
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = super.createJSON();
        JSONObject item = new JSONObject();
        item.put("formula", expression);
        jo.put("item", item);
        jo.put("add_column_type", BIJSONConstant.ETL_ADD_COLUMN_TYPE.FORMULA);
        return jo;
    }

    /**
     * 将JSON对象转换成java对象
     *
     * @param jsonObject json对象
     * @throws Exception 报错
     */
    @Override
    public void parseJSON(JSONObject jsonObject) throws Exception {
        super.parseJSON(jsonObject);
        if (jsonObject.has("item")){
            JSONObject item = jsonObject.getJSONObject("item");
            expression = item.optString("formula", StringUtils.EMPTY);
        }
    }
}
