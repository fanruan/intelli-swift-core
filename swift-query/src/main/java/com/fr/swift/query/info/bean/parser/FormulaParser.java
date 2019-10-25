package com.fr.swift.query.info.bean.parser;

import com.fr.swift.query.formula.Formula;
import com.fr.swift.query.formula.impl.ToDateFormatFormula;
import com.fr.swift.query.formula.impl.ToDateFormula;
import com.fr.swift.query.info.bean.element.FormulaBean;
import com.fr.swift.query.info.bean.element.ToDateFormatFormulaBean;
import com.fr.swift.query.info.bean.element.ToDateFormulaBean;
import com.fr.swift.segment.column.ColumnKey;

/**
 * @author yee
 * @date 2019-07-24
 */
public class FormulaParser {
    public static Formula parse(FormulaBean formulaBean) {
        switch (formulaBean.getType()) {
            case TO_DATE:
                ToDateFormulaBean toDate = (ToDateFormulaBean) formulaBean;
                Long time = toDate.getTime();
                if (time != null) {
                    return new ToDateFormula(toDate.getFormula(), time);
                }
                return new ToDateFormula(toDate.getFormula(), new ColumnKey(toDate.getColumns()[0]));
            case TO_DATE_FORMAT:
                ToDateFormatFormulaBean toDateFormat = (ToDateFormatFormulaBean) formulaBean;
                String format = toDateFormat.getFormat();
                Long time1 = toDateFormat.getTime();
                if (null != time1) {
                    return new ToDateFormatFormula(toDateFormat.getFormula(), time1, format);
                }
                return new ToDateFormatFormula(toDateFormat.getFormula(), new ColumnKey(toDateFormat.getColumns()[0]), format);
            default:
                return null;
        }
    }
}
