package com.fr.bi.field.dimension.filter.field;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.conf.report.widget.field.filtervalue.FilterValue;
import com.fr.bi.conf.report.widget.field.filtervalue.NFilterValue;
import com.fr.bi.field.dimension.filter.AbstractDimensionFilter;
import com.fr.bi.field.filtervalue.FilterValueFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.report.result.LightNode;
import com.fr.bi.stable.report.result.TargetCalculator;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DimensionTargetValueFilter extends AbstractDimensionFilter {
    @BICoreField
    private FilterValue filterValue;
    @BICoreField
    private String target_id;

    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        //维度过滤暂时用不到登陆用户信息
        if (jo.has("target_id")) {
            this.target_id = jo.getString("target_id");
        }
        filterValue = FilterValueFactory.parseFilterValue(jo, userId);
    }

    /* (non-Javadoc)
     * @see com.fr.bi.report.data.dimension.filter.ResultFilter#getUsedTargets()
     */
    @Override
    public List<String> getUsedTargets() {
        if (target_id == null) {
            return new ArrayList<String>();
        } else {
            List<String> result = new ArrayList<String>();
            result.add(target_id);
            return result;
        }
    }


    @Override
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, BusinessTable target, ICubeDataLoader loader, long userId) {
        return filterValue.createFilterIndex(dimension, target, loader, userId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DimensionTargetValueFilter)) {
            return false;
        }

        DimensionTargetValueFilter that = (DimensionTargetValueFilter) o;

        if (filterValue != null ? !ComparatorUtils.equals(filterValue, that.filterValue) : that.filterValue != null) {
            return false;
        }
        if (target_id != null ? !ComparatorUtils.equals(target_id, that.target_id) : that.target_id != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = filterValue != null ? filterValue.hashCode() : 0;
        result = 31 * result + (target_id != null ? target_id.hashCode() : 0);
        return result;
    }

    @Override
    public boolean canCreateDirectFilter() {
        return filterValue == null ? false : filterValue.canCreateFilterIndex();
    }

    @Override
    public boolean needParentRelation() {
        return filterValue instanceof NFilterValue;
    }

    @Override
    public boolean showNode(LightNode node, Map<String, TargetCalculator> targetsMap, ICubeDataLoader loader) {
        TargetCalculator targetCalculator = targetsMap.get(target_id);
        if (targetCalculator != null) {
            return filterValue.showNode(node, targetsMap.get(target_id).createTargetGettingKey(), loader);
        }
        return filterValue.showNode(node, null, loader);
    }


}