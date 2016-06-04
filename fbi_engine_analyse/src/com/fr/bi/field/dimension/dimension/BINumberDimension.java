package com.fr.bi.field.dimension.dimension;

import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.field.dimension.calculator.NumberDimensionCalculator;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.operation.group.BIGroupFactory;
import com.fr.bi.stable.operation.sort.BISortFactory;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.util.List;

public class BINumberDimension extends BIAbstractDimension {

    public BINumberDimension() {
    }

    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        super.parseJSON(jo, userId);
        JSONObject group = jo.optJSONObject("group");
        if(group == null || !group.has("type")){
            group = new JSONObject().put("type", BIReportConstant.GROUP.AUTO_GROUP);
        }
        JSONObject sort = jo.optJSONObject("sort");
        if(sort == null || !sort.has("type")){
            sort = new JSONObject().put("type", BIReportConstant.SORT.NONE);
        }
        this.sort = BISortFactory.parseSort(sort);
        this.group = BIGroupFactory.parseNumberGroup(group);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BINumberDimension)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        return true;
    }

    @Override
    public DimensionCalculator createCalculator(BusinessField column, List<BITableSourceRelation> relations) {
        return new NumberDimensionCalculator(this, column, relations);
    }


    @Override
    public Object getValueByType(Object data) {
        if (group.getType() == BIReportConstant.GROUP.ID_GROUP){
            return data == null ? null : Double.parseDouble(data.toString());
        }
        return data == null ? StringUtils.EMPTY : data.toString();
    }
}