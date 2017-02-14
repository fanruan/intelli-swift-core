package com.fr.bi.field.dimension.filter.field;

import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.field.dimension.filter.AbstractDimensionFilter;
import com.fr.bi.stable.report.result.BINode;
import com.fr.bi.stable.report.result.TargetCalculator;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 2016/6/22.
 */
public class EmptyDimensionTargetValueFilter extends AbstractDimensionFilter {
    @Override
    public List<String> getUsedTargets() {
        return new ArrayList<String>();
    }

    @Override
    public boolean showNode(BINode node, Map<String, TargetCalculator> targetsMap, ICubeDataLoader loader) {
        return true;
    }

    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {

    }

    @Override
    public boolean canCreateDirectFilter() {
        return true;
    }
}
