package com.fr.swift.db.impl;

import com.fr.swift.config.entity.SwiftMetaDataEntity;
import com.fr.swift.db.Table;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;

/**
 * @author anchore
 * @date 2018/8/14
 */
public class AddColumnAction extends BaseAlterTableAction {
    public AddColumnAction(SwiftMetaDataColumn relatedColumnMeta) {
        super(relatedColumnMeta);
    }

    @Override
    public void alter(Table table) {
        if (existsColumn(table.getMetadata())) {
            SwiftLoggers.getLogger().warn("column {} exists in {}, will add nothing", relatedColumnMeta, table);
            return;
        }

        SwiftLoggers.getLogger().info("add column {} to {}", relatedColumnMeta, table);
        alterMeta(table);
    }

    private void alterMeta(Table table) {
        SwiftMetaData oldMeta = table.getMetadata();
        try {
            SwiftMetaData newMeta = new SwiftMetaDataEntity.Builder(oldMeta)
                    .addField(relatedColumnMeta).build();
            CONF_SVC.updateMeta(newMeta);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("alter meta failed: {}", e);
        }
    }
}