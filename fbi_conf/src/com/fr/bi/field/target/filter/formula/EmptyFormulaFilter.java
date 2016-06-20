package com.fr.bi.field.target.filter.formula;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.json.JSONObject;

/**
 * Created by User on 2016/6/3.
 */
public class EmptyFormulaFilter extends FormulaFilter {

    @Override
    public JSONObject createJSON() throws Exception {
        return new JSONObject().put(BIJSONConstant.JSON_KEYS.FILTER_TYPE, BIReportConstant.FILTER_TYPE.EMPTY_FORMULA);
    }

    @Override
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, BusinessTable target, ICubeDataLoader loader, long userId) {
        return GVIFactory.createAllShowIndexGVI(loader.getTableIndex(target.getTableSource()).getRowCount());
    }

    @Override
    public GroupValueIndex createFilterIndex(BusinessTable target, ICubeDataLoader loader, long userID) {
        return GVIFactory.createAllShowIndexGVI(loader.getTableIndex(target.getTableSource()).getRowCount());
    }
}
