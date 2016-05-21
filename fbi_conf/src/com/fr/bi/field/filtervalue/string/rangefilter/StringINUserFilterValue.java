package com.fr.bi.field.filtervalue.string.rangefilter;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.report.widget.BIDataColumn;
import com.fr.bi.field.filtervalue.string.StringFilterValueUtils;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.report.result.LightNode;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.fs.base.entity.User;
import com.fr.fs.control.UserControl;
import com.fr.json.JSONObject;

/**
 * Created by Young's on 2016/5/20.
 */
public class StringINUserFilterValue extends StringRangeFilterValue {

    protected BIDataColumn column = null;

    @Override
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, Table target, ICubeDataLoader loader, long userId) {
        addLogUserInfo(loader);
        GroupValueIndex gvi = super.createFilterIndex(dimension, target, loader, userId);
        ICubeTableService ti = loader.getTableIndex(target);
        return gvi == null ? GVIFactory.createAllEmptyIndexGVI()
                : gvi.NOT(loader.getTableIndex(target).getRowCount()).AND(ti.getAllShowIndex());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        StringINUserFilterValue that = (StringINUserFilterValue) o;

        return column != null ? column.equals(that.column) : that.column == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (column != null ? column.hashCode() : 0);
        return result;
    }

    @Override
    public boolean isMatchValue(String v) {
        return !valueSet.contains(v);
    }

    @Override
    public boolean showNode(LightNode node, TargetGettingKey targetKey, ICubeDataLoader loader) {
        addLogUserInfo(loader);
        String value = StringFilterValueUtils.toString(node.getShowValue());
        if (valueSet.getValues() == null || valueSet.getValues().isEmpty()) {
            return false;
        }
        return isMatchValue(value);
    }

    protected void addLogUserInfo(ICubeDataLoader loader) {
        if (this.column != null && BIConfigureManagerCenter.getCubeConfManager().getLoginInfoInTableField() != null) {
            try {
                User frUser = UserControl.getInstance().getUser(user.getUserId());
                Object fieldValue = BIConfigureManagerCenter.getCubeConfManager().getLoginInfoInTableField().getFieldValue(frUser.getUsername(), column.createColumnKey(), loader);
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
        if(jo.has("filter_value")) {
            JSONObject filterValue = jo.getJSONObject("filter_value");
            JSONObject value = filterValue.getJSONObject("value");
            if(value.has("login_user")) {
                valueSet.getValues().add(UserControl.getInstance().getUser(userId).getUsername());
            } else {
                BIDataColumn dataColumn = new BIDataColumn();
                dataColumn.parseJSON(value);
                this.column = dataColumn;
            }
        }
    }
}
