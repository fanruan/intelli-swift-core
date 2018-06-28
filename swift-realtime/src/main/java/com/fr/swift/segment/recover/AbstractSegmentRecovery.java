package com.fr.swift.segment.recover;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.HistorySegmentImpl;
import com.fr.swift.segment.RealTimeSegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftDataOperatorProvider;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018/6/22
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public abstract class AbstractSegmentRecovery implements SegmentRecovery {

    protected SwiftSegmentManager localSegmentProvider = SwiftContext.getInstance().getBean("localSegmentProvider", SwiftSegmentManager.class);

    protected SwiftDataOperatorProvider operators = SwiftContext.getInstance().getBean(SwiftDataOperatorProvider.class);

    @Override
    public void recover(SourceKey tableKey) {
        recover(getUnstoredSegmentKeys(tableKey));
    }

    @Override
    public void recoverAll() {
        List<Table> tables;
        try {
            tables = SwiftDatabase.getInstance().getAllTables();
            for (Table table : tables) {
                recover(table.getSourceKey());
            }
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    protected Segment getBackupSegment(Segment realtimeSeg) {
        SwiftMetaData meta = realtimeSeg.getMetaData();
        String realtimeSegPath = realtimeSeg.getLocation().getPath();
        return new HistorySegmentImpl(new ResourceLocation(realtimeSegPath.replace(meta.getSwiftSchema().getDir(), SwiftDatabase.Schema.BACKUP_CUBE.getDir()), Types.StoreType.FINE_IO), meta);
    }

    protected Segment newRealtimeSegment(Segment realtimeSeg) {
        return new RealTimeSegmentImpl(realtimeSeg.getLocation(), realtimeSeg.getMetaData());
    }

    private List<SegmentKey> getUnstoredSegmentKeys(SourceKey tableKey) {
        List<SegmentKey> segKeys = localSegmentProvider.getSegmentKeys(tableKey);
        List<SegmentKey> unstoredSegs = new ArrayList<SegmentKey>();
        if (null != segKeys) {
            for (SegmentKey segKey : segKeys) {
                if (segKey.getStoreType() == Types.StoreType.MEMORY) {
                    unstoredSegs.add(segKey);
                }
            }
        }
        return unstoredSegs;
    }
}
