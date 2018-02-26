package com.fr.swift.generate;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.ColumnTypeConstants.ClassType;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaDataColumn;

/**
 * @author anchore
 * @date 2018/2/26
 */
class PrivateUtil {
    public static ClassType getClassType(DataSource dataSource, ColumnKey columnKey) throws SwiftMetaDataException {
        SwiftMetaDataColumn metaColumn = dataSource.getMetadata().getColumn(columnKey.getName());
        return ColumnTypeUtils.sqlTypeToClassType(
                metaColumn.getType(),
                metaColumn.getPrecision(),
                metaColumn.getScale()
        );
    }
}