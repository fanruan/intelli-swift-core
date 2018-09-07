package com.fr.swift.source.relation;

import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.RelationSourceType;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.util.Util;

/**
 * @author yee
 * @date 2018/5/2
 */
public class FieldRelationSource extends RelationSourceImpl {
    private static final long serialVersionUID = 3089847771095685104L;
    @CoreField
    private ColumnKey columnKey;

    public FieldRelationSource(ColumnKey columnKey) {
        super(columnKey.getRelation().getPrimarySource(),
                columnKey.getRelation().getForeignSource(),
                columnKey.getRelation().getPrimaryFields(),
                columnKey.getRelation().getForeignFields());
        this.columnKey = columnKey;
    }

    public FieldRelationSource() {
    }

    public ColumnKey getColumnKey() {
        return columnKey;
    }

    public void setColumnKey(ColumnKey columnKey) {
        this.columnKey = columnKey;
        RelationSource source = columnKey.getRelation();
        Util.requireNonNull(source);
        setPrimarySource(source.getPrimarySource());
        setPrimaryFields(source.getPrimaryFields());
        setForeignFields(source.getForeignFields());
        setForeignSource(source.getForeignSource());
    }

    public RelationSource getRelationSource() {
        return columnKey.getRelation();
    }

    @Override
    public RelationSourceType getRelationType() {
        return RelationSourceType.FIELD_RELATION;
    }
}
