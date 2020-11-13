package com.fr.swift.db.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.entity.SwiftMetaDataEntity;
import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.db.Table;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentService;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.impl.base.ResourceDiscovery;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.util.FileUtil;
import com.fr.swift.util.Util;

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
                int currentDir = CubeUtil.getCurrentDir(segKey.getTable());
                FileUtil.delete(new CubePathBuilder(segKey).asAbsolute().setTempDir(currentDir).setColumnId(dropColumn.getColumnId()).build());
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