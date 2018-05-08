package com.fr.swift.source.relation;

import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.RelationSourceType;
import com.fr.swift.source.core.CoreField;

/**
 * @author yee
 * @date 2018/5/2
 */
public class FieldRelationSource extends RelationSourceImpl {
    @CoreField
    private ColumnKey columnKey;

    public FieldRelationSource(ColumnKey columnKey) {
        super(columnKey.getRelation().getPrimarySource(),
                columnKey.getRelation().getForeignSource(),
                columnKey.getRelation().getPrimaryFields(),
                columnKey.getRelation().getForeignFields());
        this.columnKey = columnKey;
    }

    public ColumnKey getColumnKey() {
        return columnKey;
    }

    public RelationSource getRelationSource() {
        return columnKey.getRelation();
    }

    @Override
    public RelationSourceType getRelationType() {
        return RelationSourceType.FIELD_RELATION;
    }
}
