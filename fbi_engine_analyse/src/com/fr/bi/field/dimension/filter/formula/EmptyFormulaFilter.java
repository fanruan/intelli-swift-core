package com.fr.bi.field.dimension.filter.formula;

import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.field.dimension.filter.AbstractDimensionFilter;
import com.fr.bi.stable.report.result.LightNode;
import com.fr.bi.stable.report.result.TargetCalculator;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 2016/6/22.
 */
public class EmptyFormulaFilter extends AbstractDimensionFilter {
    @Override
    public List<String> getUsedTargets() {
        return new ArrayList<String>();
    }

    @Override
    public boolean needParentRelation() {
        return false;
    }

    @Override
    public boolean showNode(LightNode node, Map<String, TargetCalculator> targetsMap, ICubeDataLoader loader) {
        return true;
    }

    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {

    }
}
