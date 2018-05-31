package com.fr.swift.query.info.dimension;

import com.fr.swift.query.filter.info.FilterInfo;
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

    public GroupFormulaDimension(int index, SourceKey sourceKey, Group group, Sort sort, FilterInfo filterInfo, String formula) {
        super(index, sourceKey, null, group, sort, filterInfo);
        this.formula = formula;
    }

    @Override
    public Column getColumn(Segment segment) {
        return new DetailFormulaColumn(formula, segment);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        GroupFormulaDimension that = (GroupFormulaDimension) o;

        return formula != null ? formula.equals(that.formula) : that.formula == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (formula != null ? formula.hashCode() : 0);
        return result;
    }
}
