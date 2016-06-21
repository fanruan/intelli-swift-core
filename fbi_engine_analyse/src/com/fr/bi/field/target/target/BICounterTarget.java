package com.fr.bi.field.target.target;

import com.finebi.cube.conf.field.BIBusinessField;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.conf.table.BusinessTableHelper;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.fr.bi.field.target.calculator.sum.CountCalculator;
import com.fr.bi.stable.data.BIFieldID;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.db.IPersistentTable;
import com.fr.bi.stable.data.db.PersistentField;
import com.fr.bi.stable.report.result.TargetCalculator;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

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
            /**
             * Connery：
             */
            BusinessField field = BIModuleUtils.getBusinessFieldById(new BIFieldID(distinct_field_id));
            if (field == null) {
                String tableId = obj.getString("table_id");
                BusinessField column = new BIBusinessField(new BIFieldID(distinct_field_id));
                BusinessTable tableBelongTo = BIModuleUtils.getBusinessTableById(new BITableID(tableId));
                column.setTableBelongTo(tableBelongTo);
                this.column = column;
                this.distinct_field_name = StringUtils.EMPTY;
                return;
            }
            IPersistentTable table = field.getTableBelongTo().getTableSource().getPersistentTable();
            PersistentField c = table.getField(field.getFieldName());
            if (c == null) {
                this.distinct_field_name = StringUtils.EMPTY;
                return;
            }
            this.distinct_field_name = field.getFieldName();
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