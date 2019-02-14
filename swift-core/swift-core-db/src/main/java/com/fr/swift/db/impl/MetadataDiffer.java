package com.fr.swift.db.impl;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.util.Optional;
import com.fr.swift.util.Util;
import com.fr.swift.util.function.Consumer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 2018/8/20
 */
public class MetadataDiffer {
    private SwiftMetaData oldMeta, newMeta;

    private boolean hasDiff = false;

    private List<SwiftMetaDataColumn> addedColumn = new ArrayList<SwiftMetaDataColumn>(),
            droppedColumn = new ArrayList<SwiftMetaDataColumn>();

    public MetadataDiffer(SwiftMetaData oldMeta, SwiftMetaData newMeta) {
        this.oldMeta = oldMeta;
        this.newMeta = newMeta;
        init();
    }

    private void init() {
        try {
            diff(oldMeta, newMeta, new Consumer<SwiftMetaDataColumn>() {
                @Override
                public void accept(SwiftMetaDataColumn swiftMetaDataColumn) {
                    addedColumn.add(swiftMetaDataColumn);
                    if (!hasDiff) {
                        hasDiff = true;
                    }
                }
            });
            diff(newMeta, oldMeta, new Consumer<SwiftMetaDataColumn>() {
                @Override
                public void accept(SwiftMetaDataColumn swiftMetaDataColumn) {
                    droppedColumn.add(swiftMetaDataColumn);
                    if (!hasDiff) {
                        hasDiff = true;
                    }
                }
            });
        } catch (SwiftMetaDataException e) {
            SwiftLoggers.getLogger().warn("metadata differ encountered problem: {}", Util.getRootCauseMessage(e));
        }
    }

    private static void diff(SwiftMetaData oldMeta, SwiftMetaData newMeta, Consumer<SwiftMetaDataColumn> columnMetaHandler) throws SwiftMetaDataException {
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

    private static Optional<SwiftMetaDataColumn> getColumn(SwiftMetaData meta, String columnName) {
        try {
            return Optional.of(meta.getColumn(columnName));
        } catch (SwiftMetaDataException e) {
            return Optional.empty();
        }
    }

    private static boolean hasSameType(SwiftMetaDataColumn meta1, SwiftMetaDataColumn meta2) {
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