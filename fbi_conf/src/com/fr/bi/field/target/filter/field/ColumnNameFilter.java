package com.fr.bi.field.target.filter.field;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.field.BIBusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.field.dimension.calculator.NoneDimensionCalculator;
import com.fr.bi.stable.data.BIFieldID;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by 小灰灰 on 2016/3/17.
 */
public class ColumnNameFilter extends ColumnFieldFilter {
    private String columnName;

    public void parseJSON(JSONObject jo, long userId) throws Exception {
        super.parseJSON(jo, userId);
        this.columnName = jo.optString("field_name", StringUtils.EMPTY);
    }

    private int getClassType(String fieldName, BusinessTable target){
        Set<ICubeFieldSource> fields = target.getTableSource().getFacetFields(new HashSet<CubeTableSource>());
        Iterator<ICubeFieldSource> it = fields.iterator();
        while (it.hasNext()){
            ICubeFieldSource fieldSource = it.next();
            if(ComparatorUtils.equals(fieldSource.getFieldName(), fieldName)){
                return fieldSource.getClassType();
            }
        }
        return 0;
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
    public GroupValueIndex createFilterIndex(BusinessTable target, ICubeDataLoader loader, long userID) {
        if (filterValue != null) {
            return filterValue.createFilterIndex(new NoneDimensionCalculator(new BIBusinessField(target, new BIFieldID(""), columnName, this.getClassType(columnName, target), 0, true, true, false), new ArrayList<BITableSourceRelation>()), target, loader, userID);
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
