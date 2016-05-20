package com.fr.bi.field.target.target.cal.target;

import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.field.target.calculator.cal.FormulaCalculator;
import com.fr.bi.field.target.target.BIAbstractTarget;
import com.fr.bi.field.target.target.cal.BICalculateTarget;
import com.fr.bi.stable.report.result.TargetCalculator;
import com.fr.bi.stable.utils.BIFormularUtils;
import com.fr.bi.stable.utils.BITravalUtils;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BIFormulaCalculateTarget extends BICalculateTarget {
    private static final long serialVersionUID = -419357785569877543L;
    @BICoreField
    private String expression = StringUtils.EMPTY;

    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        super.parseJSON(jo, userId);
        expression = jo.optJSONObject("_src").optJSONObject("expression").optString("formula_value");
    }


    @Override
    public List<BIAbstractTarget> createCalculateUseTarget(BIAbstractTarget[] targets) {
        List<BIAbstractTarget> list = new ArrayList<BIAbstractTarget>();
        Iterator<String> it = BIFormularUtils.createColumnIndexMap(expression).values().iterator();
        while (it.hasNext()) {
            BIAbstractTarget target = BITravalUtils.getTargetByName(it.next(), targets);
            if (target != null) {
                list.add(target);
            }
        }
        return list;
    }

    @Override
    public TargetCalculator createSummaryCalculator() {
        return new FormulaCalculator(this, expression);
    }
}