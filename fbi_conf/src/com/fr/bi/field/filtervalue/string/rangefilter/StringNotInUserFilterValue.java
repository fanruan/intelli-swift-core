package com.fr.bi.field.filtervalue.string.rangefilter;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.conf.field.BIBusinessField;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.field.filtervalue.string.StringFilterValueUtils;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.report.result.LightNode;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.fs.control.UserControl;
import com.fr.json.JSONObject;

/**
 * Created by Young's on 2016/5/20.
 */
public class StringNotInUserFilterValue extends StringRangeFilterValue {
    protected BusinessField column = null;

    @BICoreField
    private String CLASS_TYPE = "StringNotInUserFilterValue";

    @Override
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, BusinessTable target, ICubeDataLoader loader, long userId) {
        addLogUserInfo();
        GroupValueIndex gvi = super.createFilterIndex(dimension, target, loader, userId);
        ICubeTableService ti = loader.getTableIndex(target.getTableSource());
        return gvi == null ? GVIFactory.createAllEmptyIndexGVI()
                : gvi.NOT(loader.getTableIndex(target.getTableSource()).getRowCount()).AND(ti.getAllShowIndex());
    }

    @Override
    public boolean isMatchValue(String v) {
        return valueSet.contains(v);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        StringNotInUserFilterValue that = (StringNotInUserFilterValue) o;

        return column != null ? column.equals(that.column) : that.column == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (column != null ? column.hashCode() : 0);
        return result;
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
        if (this.column != null && BIConfigureManagerCenter.getCubeConfManager().getLoginField() != null) {
            try {
                Object[] values = BIConfigureManagerCenter.getCubeConfManager().getLoginFieldValue(column, user.getUserId());
                if (values != null) {
                    for(int i = 0; i < values.length; i++) {
                        valueSet.getValues().add(values[i].toString());
                    }
                }
            } catch (Exception e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        super.parseJSON(jo, userId);
        if (jo.has("filter_value")) {
            JSONObject filterValue = jo.getJSONObject("filter_value");
            JSONObject value = filterValue.getJSONObject("value");
            if (value.has("login_user")) {
                valueSet.getValues().add(UserControl.getInstance().getUser(userId).getUsername());
            } else {
                BusinessField dataColumn = new BIBusinessField();
                dataColumn.parseJSON(value);
                this.column = dataColumn;
            }
        }
    }
}
