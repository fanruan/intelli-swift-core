package com.fr.swift.query.info.element.dimension;

import com.fr.swift.query.group.Group;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.impl.base.DetailFormulaColumn;
import com.fr.swift.source.SourceKey;

/**
 * Created by pony on 2018/5/11.
 */
public class GroupFormulaDimension extends GroupDimension {
    private String formula;

    public GroupFormulaDimension(int index, SourceKey sourceKey, Group group, Sort sort, String formula) {
        super(index, sourceKey, null, group, sort);
        this.formula = formula;
    }

    public String getFormula() {
        return formula;
    }

    @Override
    public Column getColumn(Segment segment) {
        return new DetailFormulaColumn(formula, segment);
    }

    @Override
    public DimensionType getDimensionType() {
        return DimensionType.GROUP_FORMULA;
    }
}
