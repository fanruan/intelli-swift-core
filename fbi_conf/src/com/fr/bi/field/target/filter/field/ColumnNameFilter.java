package com.fr.bi.field.target.filter.field;

import com.fr.bi.field.dimension.calculator.NoneDimensionCalculator;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.Table;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.util.ArrayList;

/**
 * Created by 小灰灰 on 2016/3/17.
 */
public class ColumnNameFilter extends ColumnFieldFilter{
    private String columnName;
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        super.parseJSON(jo, userId);
        this.columnName = jo.optString("field_name", StringUtils.EMPTY);
    }
    /**
     * 指标上加的过滤
     *
     * @param target
     * @param loader
     * @param userID
     * @return
     */
    @Override
    public GroupValueIndex createFilterIndex(Table target, ICubeDataLoader loader, long userID) {
        if (filterValue != null) {
            return filterValue.createFilterIndex(new NoneDimensionCalculator(new BIField(target, columnName) , new ArrayList<BITableSourceRelation>()), target, loader, userID);
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        ColumnNameFilter that = (ColumnNameFilter) o;

        return columnName != null ? ComparatorUtils.equals(columnName, that.columnName) : that.columnName == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (columnName != null ? columnName.hashCode() : 0);
        return result;
    }
}
