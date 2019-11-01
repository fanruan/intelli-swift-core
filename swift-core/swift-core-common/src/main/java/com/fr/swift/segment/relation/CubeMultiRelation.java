package com.fr.swift.segment.relation;

import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SourceKey;

/**
 * @author yee
 * @date 2018/9/7
 */
public interface CubeMultiRelation<Field extends ILogicKeyField<SourceKey, ColumnKey>> {
    Field getPrimaryField();

    Field getForeignField();

    SourceKey getPrimaryTable();

    SourceKey getForeignTable();

    String getKey();

    Field getPrimaryKey();

    Field getForeignKey();
}
