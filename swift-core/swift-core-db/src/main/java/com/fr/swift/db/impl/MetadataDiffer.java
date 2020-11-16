package com.fr.swift.db.impl;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.util.Optional;
import com.fr.swift.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author anchore
 * @date 2018/8/20
 */
public class MetadataDiffer {
    private SwiftMetaData oldMeta, newMeta;

    private boolean hasDiff = false;

    private List<SwiftMetaDataColumn> addedColumn = new ArrayList<>(),
            droppedColumn = new ArrayList<>();

    public MetadataDiffer(SwiftMetaData oldMeta, SwiftMetaData newMeta) {
        this.oldMeta = oldMeta;
        this.newMeta = newMeta;
        init();
    }

    private void init() {
        try {
            diff(oldMeta, newMeta, swiftMetaDataColumn -> {
                addedColumn.add(swiftMetaDataColumn);
                if (!hasDiff) {
                    hasDiff = true;
                }
            });
            diff(newMeta, oldMeta, swiftMetaDataColumn -> {
                droppedColumn.add(swiftMetaDataColumn);
                if (!hasDiff) {
                    hasDiff = true;
                }
            });
        } catch (SwiftMetaDataException e) {
            SwiftLoggers.getLogger().warn("metadata differ encountered problem: {}", Util.getRootCauseMessage(e));
        }
    }

    private void diff(SwiftMetaData oldMeta, SwiftMetaData newMeta, Consumer<SwiftMetaDataColumn> columnMetaHandler) throws SwiftMetaDataException {
        for (int i = 0; i < newMeta.getColumnCount(); i++) {
            SwiftMetaDataColumn newColumnMeta = newMeta.getColumn(i + 1);

            Optional<SwiftMetaDataColumn> columnMeta = getColumn(oldMeta, newColumnMeta.getName());
            if (!columnMeta.isPresent()) {
                columnMetaHandler.accept(newColumnMeta);
                continue;
            }
            SwiftMetaDataColumn oldColumnMeta = columnMeta.get();
            if (!hasSameType(oldColumnMeta, newColumnMeta)) {
                SwiftLoggers.getLogger().warn("modify column type from {} to {} is not supported", oldColumnMeta, newColumnMeta);
            }
        }
    }

    private Optional<SwiftMetaDataColumn> getColumn(SwiftMetaData meta, String columnName) {
        try {
            return Optional.of(meta.getColumn(columnName));
        } catch (SwiftMetaDataException e) {
            return Optional.empty();
        }
    }

    private boolean hasSameType(SwiftMetaDataColumn meta1, SwiftMetaDataColumn meta2) {
        return meta1.getType() == meta2.getType() &&
                meta1.getPrecision() == meta2.getPrecision() &&
                meta1.getScale() == meta2.getScale();
    }

    public boolean hasDiff() {
        return hasDiff;
    }

    public List<SwiftMetaDataColumn> getAdded() {
        return addedColumn;
    }

    public List<SwiftMetaDataColumn> getDropped() {
        return droppedColumn;
    }
}