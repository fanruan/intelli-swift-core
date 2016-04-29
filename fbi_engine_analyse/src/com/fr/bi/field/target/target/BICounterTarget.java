package com.fr.bi.field.target.target;

import com.fr.bi.base.BIUser;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.field.target.calculator.sum.CountCalculator;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.db.BIColumn;
import com.fr.bi.stable.data.db.DBTable;
import com.fr.bi.stable.report.result.TargetCalculator;
import com.fr.bi.stable.utils.BIIDUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;

public class BICounterTarget extends BISummaryTarget {

    private String distinct_field_name;

    /**
     * 将JSON对象转换成java对象
     *
     * @param jo     json对象
     * @param userId 用户id
     * @throws Exception
     */
    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        super.parseJSON(jo, userId);
        if (jo.has("_src")) {
            JSONObject obj = jo.optJSONObject("_src");
            String distinct_field_id = obj.getString("field_id");
            DBTable table = BIConfigureManagerCenter.getDataSourceManager().getTableSourceByID(new BITableID(BIIDUtils.getTableIDFromFieldID(distinct_field_id)), new BIUser(userId)).getDbTable();
            BIColumn c = table.getBIColumn(BIIDUtils.getFieldNameFromFieldID(distinct_field_id));
            if(c == null){
                this.distinct_field_name = null;
                return;
            }
            this.distinct_field_name = BIIDUtils.getFieldNameFromFieldID(distinct_field_id);
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BICounterTarget)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        BICounterTarget that = (BICounterTarget) o;

        if (distinct_field_name != null ? !ComparatorUtils.equals(distinct_field_name, that.distinct_field_name) : that.distinct_field_name != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (distinct_field_name != null ? distinct_field_name.hashCode() : 0);
        return result;
    }

    @Override
    public TargetCalculator createSummaryCalculator() {
        return new CountCalculator(this, distinct_field_name);
    }
}