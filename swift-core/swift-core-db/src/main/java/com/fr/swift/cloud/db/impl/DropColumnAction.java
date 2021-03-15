package com.fr.swift.cloud.db.impl;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.config.entity.SwiftMetaDataEntity;
import com.fr.swift.cloud.cube.CubePathBuilder;
import com.fr.swift.cloud.db.Table;
import com.fr.swift.cloud.exception.meta.SwiftMetaDataException;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.segment.SegmentKey;
import com.fr.swift.cloud.segment.SegmentService;
import com.fr.swift.cloud.segment.column.ColumnKey;
import com.fr.swift.cloud.segment.column.impl.base.ResourceDiscovery;
import com.fr.swift.cloud.source.SwiftMetaData;
import com.fr.swift.cloud.source.SwiftMetaDataColumn;
import com.fr.swift.cloud.util.FileUtil;
import com.fr.swift.cloud.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 2018/8/20
 */
public class DropColumnAction extends BaseAlterTableAction {
    public DropColumnAction(SwiftMetaDataColumn... relatedColumnMeta) {
        super(relatedColumnMeta);
    }

    @Override
    public Table alter(final Table table) {
        if (!existsColumn(table.getMetadata())) {
            SwiftLoggers.getLogger().warn("column {} is not present in {}, will drop nothing", relatedColumnMeta, table);
            return table;
        }
        SwiftLoggers.getLogger().info("drop column {} of {}", relatedColumnMeta, table);

        List<SegmentKey> segKeys = SwiftContext.get().getBean(SegmentService.class).getSegmentKeys(table.getSourceKey());
        for (SwiftMetaDataColumn dropColumn : relatedColumnMeta) {
            for (final SegmentKey segKey : segKeys) {
                if (segKey.getStoreType().isTransient()) {
                    // 删内存
                    ResourceDiscovery.getInstance().releaseColumn(segKey.getSwiftSchema(), segKey.getTable(), new ColumnKey(dropColumn.getName()));
                    // 删备份
                    FileUtil.delete(new CubePathBuilder(segKey).asAbsolute().asBackup().setColumnId(dropColumn.getColumnId()).build());
                    continue;
                }

                // 删history todo 还要删共享存储
                FileUtil.delete(new CubePathBuilder(segKey).asAbsolute().setTempDir(segKey.getSegmentUri()).setColumnId(dropColumn.getColumnId()).build());
            }
        }
        return alterMeta(table);
    }

    private Table alterMeta(Table table) {
        SwiftMetaData oldMeta = table.getMetadata();
        try {
            List<SwiftMetaDataColumn> columnMetas = new ArrayList<>(oldMeta.getColumnCount() - relatedColumnMeta.length);

            for (int i = 1; i <= oldMeta.getColumnCount(); i++) {
                boolean retain = true;
                for (SwiftMetaDataColumn dropColumn : relatedColumnMeta) {
                    if (oldMeta.getColumn(i).getName().equals(dropColumn.getName())) {
                        retain = false;
                        break;
                    }
                }
                if (retain) {
                    columnMetas.add(oldMeta.getColumn(i));
                }
            }
            SwiftMetaData newMeta = new SwiftMetaDataEntity.Builder(oldMeta).setFields(columnMetas).build();
            CONF_SVC.updateMeta(newMeta);
            return new SwiftTable(table.getSourceKey(), newMeta);
        } catch (SwiftMetaDataException e) {
            SwiftLoggers.getLogger().warn("alter meta failed, {}", Util.getRootCauseMessage(e));
        }
        return table;
    }
}