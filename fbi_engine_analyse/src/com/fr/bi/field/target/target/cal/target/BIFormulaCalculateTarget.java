package com.fr.bi.field.target.target.cal.target;

import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.field.target.calculator.cal.FormulaCalculator;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.field.target.target.TargetType;
import com.fr.bi.field.target.target.cal.BICalculateTarget;
import com.fr.bi.report.result.TargetCalculator;
import com.fr.bi.stable.utils.BIFormulaUtils;
import com.fr.bi.stable.utils.BITravalUtils;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class BIFormulaCalculateTarget extends BICalculateTarget {
    private static final long serialVersionUID = -419357785569877543L;
    @BICoreField
    private String expression = StringUtils.EMPTY;

    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        super.parseJSON(jo, userId);
        JSONObject expressionJo = jo.optJSONObject("_src").optJSONObject("expression");
        if (expressionJo != null) {
            expression = expressionJo.optString("formulaValue");
        }

    }


    @Override
    public List<BISummaryTarget> createCalculateUseTarget(BISummaryTarget[] targets) {
        List<BISummaryTarget> list = new ArrayList<BISummaryTarget>();
        Iterator<String> it = BIFormulaUtils.createColumnIndexMap(expression).values().iterator();
        while (it.hasNext()) {
            BISummaryTarget target = BITravalUtils.getTargetByName(it.next(), targets);
            if (target != null) {
                list.add(target);
            }
        }
        return list;
    }

    @Override
    public Collection<String> getCalculateUseTargetIDs() {
        return BIFormulaUtils.createColumnIndexMap(expression).values();
    }

    public TargetType getType(){
        return TargetType.FORMULA;
    }

    @Override
    public TargetCalculator createSummaryCalculator() {
        return new FormulaCalculator(this, expression);
    }
}