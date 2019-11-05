package com.fr.swift.db.impl;


import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.db.Table;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 */
public class ModifyColumnAction extends BaseAlterTableAction {
    public ModifyColumnAction(SwiftMetaDataColumn relatedColumnMeta) {
        super(relatedColumnMeta);
    }

    @Override
    public void alter(Table table) throws SQLException {
        if (!existsColumn(table.getMetadata())) {
            SwiftLoggers.getLogger().warn("column {} is not present in {}, will modify nothing", relatedColumnMeta, table);
            return;
        }

        SwiftMetaData oldMeta = table.getMetadata();
        try {
            List<SwiftMetaDataColumn> columnMetas = new ArrayList<SwiftMetaDataColumn>(oldMeta.getColumnCount());
            for (int i = 0; i < oldMeta.getColumnCount(); i++) {
                SwiftMetaDataColumn columnMeta = oldMeta.getColumn(i + 1);
                columnMetas.add(columnMeta.getName().equals(relatedColumnMeta.getName()) ? relatedColumnMeta : columnMeta);
            }
            SwiftMetaData newMeta = new SwiftMetaDataBean(
                    oldMeta.getId(),
                    oldMeta.getSwiftSchema(),
                    oldMeta.getSchemaName(),
                    oldMeta.getTableName(),
                    oldMeta.getRemark(), columnMetas);
            META_SVC.updateMetaData(table.getSourceKey().getId(), newMeta);
        } catch (SwiftMetaDataException e) {
            SwiftLoggers.getLogger().error("alter meta failed", e);
        }
    }
}
