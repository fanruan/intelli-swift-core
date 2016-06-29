package com.fr.bi.conf.data.source.operator.add.rowcal.correspondperiod;

import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.data.source.operator.add.rowcal.CorrespondRowCalculatorOperator;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.engine.cal.ResultDealer;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

/**
 * Created by 小灰灰 on 2016/5/9.
 * 同期
 */
public class CorrespondMonthPeriodRowCalculatorOperator extends CorrespondRowCalculatorOperator {
    @BICoreField
    private static final String XML_TAG="CorrespondMonthPeriodRowCalculatorOperator";
    @BICoreField
    private BIKey monthKey;


    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo =  super.createJSON();
        JSONObject item = jo.getJSONObject("item");
        item.put("monthSeason", this.monthKey.getKey());
        JSONArray group = new JSONArray();
        for (int i = 0; i < this.dimension.length - 1; i ++){
            group.put(this.dimension[i].getKey());
        }
        item.put("group", group);
        return jo;
    }

    @Override
    public void parseJSON(JSONObject jsonObject) throws Exception {
        super.parseJSON(jsonObject);
        JSONObject item = jsonObject.getJSONObject("item");
        this.monthKey = new IndexKey(item.getString("monthSeason"));
        if (item.has("group")){
            JSONArray ja = item.getJSONArray("group");
            this.dimension = new IndexKey[ja.length() + 1];
            for (int i = 0; i < ja.length(); i++){
                this.dimension[i] = new IndexKey(ja.getString(i));
            }
        } else {
            this.dimension = new IndexKey[1] ;
        }
        this.dimension[this.dimension.length - 1] = this.monthKey;
    }


    @Override
    protected ResultDealer createResultDealer(Traversal<BIDataValue> travel, int startCol) {
        return new CorrespondPeriodResultDealer(key, travel, periodKey, startCol);
    }

    @Override
    protected String getAddColumnType() {
        return BIJSONConstant.ETL_ADD_COLUMN_TYPE.EXPR_CPP;
    }

    @Override
    public String xmlTag() {
        return XML_TAG;
    }

}

