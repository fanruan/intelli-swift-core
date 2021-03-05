package com.fr.swift.cloud.query.info.element.dimension;

import com.fr.swift.cloud.query.formula.Formula;
import com.fr.swift.cloud.query.group.Group;
import com.fr.swift.cloud.query.group.info.IndexInfoImpl;
import com.fr.swift.cloud.query.info.bean.type.DimensionType;
import com.fr.swift.cloud.query.sort.Sort;
import com.fr.swift.cloud.segment.Segment;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.segment.column.impl.base.DetailFormulaColumn;

/**
 * Created by pony on 2018/5/11.
 */
public class GroupFormulaDimension extends GroupDimension {
    //    private String formula;
    private Formula formula;

    public GroupFormulaDimension(int index, Group group, Sort sort, Formula formula) {
        super(index, formula.getColumnKeys() == null ? null : formula.getColumnKeys()[0], group, sort, new IndexInfoImpl(true, false));
        this.formula = formula;
    }

//    public String getFormula() {
//        return formula;
//    }

    @Override
    public Column getColumn(Segment segment) {
        return new DetailFormulaColumn(formula, segment);
    }

    @Override
    public DimensionType getDimensionType() {
        return DimensionType.GROUP_FORMULA;
    }
}
