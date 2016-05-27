package com.fr.bi.field.filtervalue.string.rangefilter;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.field.filtervalue.string.StringFilterValueUtils;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.report.result.LightNode;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.json.JSONObject;

/**
 * Created by Young's on 2016/5/20.
 */
public class StringINUserFilterValue extends StringRangeFilterValue {
    @BICoreField
    protected String fieldId;

    @Override
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, CubeTableSource target, ICubeDataLoader loader, long userId) {
        addLogUserInfo();
        GroupValueIndex gvi = super.createFilterIndex(dimension, target, loader, userId);
        ICubeTableService ti = loader.getTableIndex(target.getTableSource());
        return gvi == null ? GVIFactory.createAllEmptyIndexGVI()
                : gvi.NOT(loader.getTableIndex(target.getTableSource()).getRowCount()).AND(ti.getAllShowIndex());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        StringINUserFilterValue that = (StringINUserFilterValue) o;

        return fieldId != null ? fieldId.equals(that.fieldId) : that.fieldId == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (fieldId != null ? fieldId.hashCode() : 0);
        return result;
    }

    @Override
    public boolean isMatchValue(String v) {
        return !valueSet.contains(v);
    }

    @Override
    public boolean showNode(LightNode node, TargetGettingKey targetKey, ICubeDataLoader loader) {
        addLogUserInfo();
        String value = StringFilterValueUtils.toString(node.getShowValue());
        if (valueSet.getValues() == null || valueSet.getValues().isEmpty()) {
            return false;
        }
        return isMatchValue(value);
    }

    protected void addLogUserInfo() {
        if (this.fieldId != null && BIConfigureManagerCenter.getCubeConfManager().getLoginField() != null) {
            try {
                Object fieldValue = BIConfigureManagerCenter.getCubeConfManager().getLoginFieldValue(user.getUserId());
                if (fieldValue != null) {
                    valueSet.getValues().add(fieldValue.toString());
                }
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        super.parseJSON(jo, userId);
        if (jo.has("filter_value")) {
            this.fieldId = jo.getString("filter_value");
        }
    }
}
