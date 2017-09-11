package com.fr.bi.field.target.target;

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.field.BIBusinessField;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.fr.bi.field.target.calculator.sum.CountCalculator;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.data.BIFieldID;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.db.ICubeFieldSource;


import com.fr.bi.stable.data.source.CubeTableSource;

import com.fr.bi.report.result.TargetCalculator;

import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.util.HashSet;
import java.util.Set;


public class BICounterTarget extends BISummaryTarget {

    private String distinct_field_name;
    private static BILogger LOGGER = BILoggerFactory.getLogger(BICounterTarget.class);

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
            String distinct_field_id = obj.getString("fieldId");
            /**
             * Connery：
             */
            BusinessField field = BIModuleUtils.getAnalysisBusinessFieldById(new BIFieldID(distinct_field_id));
            if (field == null) {
                String tableId = obj.getString("tableId");
                BusinessField column = new BIBusinessField(new BIFieldID(distinct_field_id));
                BusinessTable tableBelongTo = BIModuleUtils.getAnalysisBusinessTableById(new BITableID(tableId));
                column.setTableBelongTo(tableBelongTo);
                this.column = column;
                this.distinct_field_name = StringUtils.EMPTY;
                return;
            }
            if (!contain(field)) {
                this.distinct_field_name = StringUtils.EMPTY;
                return;
            }
            this.distinct_field_name = field.getFieldName();
        }
    }

    private boolean contain(BusinessField field) {
        if (field != null && field.getTableBelongTo() != null) {
            CubeTableSource table = field.getTableBelongTo().getTableSource();
            if (table != null) {
                Set<ICubeFieldSource> fieldSources = table.getFacetFields(new HashSet<CubeTableSource>());
                for (ICubeFieldSource fieldSource : fieldSources) {
                    if (ComparatorUtils.equals(fieldSource.getFieldName(), field.getFieldName())) {
                        return true;
                    }
                }
                LOGGER.warn("{} doesn't contain field:{}", table.getTableName(), field.getFieldName());
            }
            return false;
        } else {
            return false;
        }
    }


    //BUG 这里不需要刷新column
    @Override
    public void refreshColumn() {
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

    public SumType getSumType() {
        return StringUtils.isNotEmpty(distinct_field_name) ? SumType.GVI : SumType.PLUS;
    }

    @Override
    public TargetCalculator createSummaryCalculator() {
        return new CountCalculator(this, distinct_field_name);
    }

    @Override
    public int getSummaryType() {

        return BIReportConstant.SUMMARY_TYPE.COUNT;
    }
}