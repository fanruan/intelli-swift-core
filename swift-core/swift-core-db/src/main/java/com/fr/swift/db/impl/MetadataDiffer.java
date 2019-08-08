package com.fr.swift.db.impl;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.util.Optional;
import com.fr.swift.util.Util;
import com.fr.swift.util.function.Consumer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author anchore
 * @date 2018/8/20
 */
public class MetadataDiffer {
    private SwiftMetaData oldMeta, newMeta;

    private List<SwiftMetaDataColumn> addedColumns = new ArrayList<SwiftMetaDataColumn>(),
            droppedColumns = new ArrayList<SwiftMetaDataColumn>(),
            modifiedStringPrecisionColumns = new ArrayList<SwiftMetaDataColumn>();

    public MetadataDiffer(SwiftMetaData oldMeta, SwiftMetaData newMeta) {
        this.oldMeta = oldMeta;
        this.newMeta = newMeta;
        init();
    }

    private void init() {
        try {
            diff(oldMeta, newMeta);
        } catch (SwiftMetaDataException e) {
            SwiftLoggers.getLogger().warn("metadata differ encountered problem", e);
        }
    }

    private void diff(SwiftMetaData oldMeta, SwiftMetaData newMeta) throws SwiftMetaDataException {
        Set<String> allColumnNames = new HashSet<String>(oldMeta.getFieldNames()),
                commonColumnNames = new HashSet<String>(),
                addedColumnNames = new HashSet<String>();
        for (String columnName : newMeta.getFieldNames()) {
            if (allColumnNames.add(columnName)) {
                addedColumnNames.add(columnName);
            } else {
                commonColumnNames.add(columnName);
            }
        }
        allColumnNames.removeAll(commonColumnNames);
        allColumnNames.removeAll(addedColumnNames);
        Set<String> droppedColumnNames = allColumnNames;

        for (String addedColumnName : addedColumnNames) {
            addedColumns.add(newMeta.getColumn(addedColumnName));
        }
        for (String droppedColumnName : droppedColumnNames) {
            droppedColumns.add(oldMeta.getColumn(droppedColumnName));
        }

        for (String commonColumnName : commonColumnNames) {
            SwiftMetaDataColumn oldColumnMeta = oldMeta.getColumn(commonColumnName);
            SwiftMetaDataColumn newColumnMeta = newMeta.getColumn(commonColumnName);
            if (!hasSameType(oldColumnMeta, newColumnMeta)) {
                SwiftLoggers.getLogger().warn("modify column type from {} to {} is not supported", oldColumnMeta, newColumnMeta);
            } else if (modifiedStringPrecision(oldColumnMeta, newColumnMeta)) {
                modifiedStringPrecisionColumns.add(newColumnMeta);
            }
        }
    }


    private static boolean hasSameType(SwiftMetaDataColumn meta1, SwiftMetaDataColumn meta2) {
        return meta1.getType() == meta2.getType() &&
                meta1.getPrecision() == meta2.getPrecision() &&
                meta1.getScale() == meta2.getScale();
    }

    private static boolean modifiedStringPrecision(SwiftMetaDataColumn meta1, SwiftMetaDataColumn meta2) {
        ColumnTypeConstants.ClassType classType1 = ColumnTypeUtils.getClassType(meta1);
        return classType1 == ColumnTypeConstants.ClassType.STRING &&
                classType1 == ColumnTypeUtils.getClassType(meta2) &&
                meta1.getPrecision() != meta2.getPrecision();
    }

    public List<SwiftMetaDataColumn> getAdded() {
        return addedColumns;
    }

    public List<SwiftMetaDataColumn> getDropped() {
        return droppedColumns;
    }

    public List<SwiftMetaDataColumn> getModified(){
        return modifiedStringPrecisionColumns;
    }
}