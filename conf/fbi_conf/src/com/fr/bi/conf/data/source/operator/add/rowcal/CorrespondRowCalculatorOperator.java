package com.fr.bi.conf.data.source.operator.add.rowcal;

import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.json.JSONObject;

/**
 * Created by 小灰灰 on 2016/5/10.
 * 环期
 */
public abstract class CorrespondRowCalculatorOperator extends RowCalculatorOperator {
    @BICoreField
    protected BIKey periodKey;
    public JSONObject createJSON() throws Exception {
        JSONObject jo =  super.createJSON();
        JSONObject item = jo.getJSONObject("item");
        item.put("period", this.periodKey.getKey());
        return jo;
    }

    @Override
    public void parseJSON(JSONObject jsonObject) throws Exception {
        super.parseJSON(jsonObject);
        JSONObject item = jsonObject.getJSONObject("item");
        this.periodKey = new IndexKey(item.getString("period"));
    }
}
