/**
 *
 */
package com.fr.bi.field.dimension.filter.formula;

import com.fr.bi.field.dimension.filter.AbstractDimensionFilter;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.report.result.LightNode;
import com.fr.bi.stable.report.result.SummaryValue;
import com.fr.bi.stable.report.result.TargetCalculator;
import com.fr.bi.stable.utils.BIFormularUtils;
import com.fr.bi.stable.utils.BIMapUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.script.Calculator;
import com.fr.stable.StringUtils;

import java.util.*;


public class FormulaValueFilter extends AbstractDimensionFilter {

    private static Calculator c = Calculator.createCalculator();
    private String expression = StringUtils.EMPTY;

    /* (non-Javadoc)
     * @see com.fr.json.ParseJSON#parseJSON(com.fr.json.JSONObject)
     */
    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        expression = jo.optString("filter_value", StringUtils.EMPTY);
    }

    /* (non-Javadoc)
     * @see com.fr.bi.report.data.dimension.filter.ResultFilter#getUsedTargets()
     */
    @Override
    public List<String> getUsedTargets() {
        ArrayList<String> list = new ArrayList<String>();
        list.addAll(BIFormularUtils.createColumnIndexMap(expression).values());
        return list;
    }

    @Override
    public boolean needParentRelation() {
        return false;
    }

    @Override
    public boolean showNode(LightNode node, Map<String, TargetCalculator> targetsMap, ICubeDataLoader loader) {
        Object res = calCalculateTarget(node, targetsMap);
        if (res instanceof Boolean) {
            return ((Boolean) res).booleanValue();
        }
        return res != null;
    }


    public Object calCalculateTarget(SummaryValue node, Map<String, TargetCalculator> targetsMap) {
        String formula = "=" + expression;
        return BIFormularUtils.getCalculatorValue(c, formula, BIMapUtils.mergeMapByKeyMapValue(targetsMap, node.getSummaryValueMap()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FormulaValueFilter)) {
            return false;
        }

        FormulaValueFilter that = (FormulaValueFilter) o;

        if (!ComparatorUtils.equals(expression, that.expression)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return expression != null ? expression.hashCode() : 0;
    }
}