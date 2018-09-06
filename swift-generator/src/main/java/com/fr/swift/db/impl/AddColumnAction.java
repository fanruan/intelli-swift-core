package com.fr.swift.db.impl;

import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.bean.SwiftMetaDataBean;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.db.Table;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.util.Util;

import java.util.ArrayList;
import java.util.List;

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

        List<SegmentKey> segKeys = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class).getSegmentKeys(table.getSourceKey());
        for (SegmentKey segKey : segKeys) {
            if (segKey.getStoreType() == StoreType.MEMORY) {
                new SegmentTransfer(segKey, getHistorySegKey(segKey)).transfer();
            }
        }

        alterMeta(table);
    }

    private void alterMeta(Table table) {
        SwiftMetaData oldMeta = table.getMetadata();
        try {
            List<SwiftMetaDataColumn> columnMetas = new ArrayList<SwiftMetaDataColumn>(oldMeta.getColumnCount() + 1);
            for (int i = 0; i < oldMeta.getColumnCount(); i++) {
                columnMetas.add(oldMeta.getColumn(i + 1));
            }
            columnMetas.add(relatedColumnMeta);
            SwiftMetaData newMeta = new SwiftMetaDataBean(oldMeta.getTableName(), columnMetas);
            CONF_SVC.updateMetaData(table.getSourceKey().getId(), newMeta);
        } catch (SwiftMetaDataException e) {
            SwiftLoggers.getLogger().warn("alter meta failed, {}: {}", Util.getRootCauseMessage(e));
        }
    }

    private static SegmentKey getHistorySegKey(SegmentKey realtimeSegKey) {
        return new SegmentKeyBean(realtimeSegKey.getTable().getId(), realtimeSegKey.getUri(), realtimeSegKey.getOrder(), StoreType.FINE_IO, realtimeSegKey.getSwiftSchema());
    }
}