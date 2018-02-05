package com.fr.swift.manager;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.segment.DecreaseSegmentOperator;
import com.fr.swift.segment.HistorySegmentOperator;
import com.fr.swift.segment.ISegmentOperator;
import com.fr.swift.segment.IncreaseSegmentOperator;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.SwiftSegmentProvider;
import com.fr.swift.source.MetaDataXmlManager;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.Crasher;

import java.util.List;

/**
 * Created by pony on 2017/12/14.
 */
public class LocalSegmentProvider implements SwiftSegmentProvider {
    private static LocalSegmentProvider ourInstance = new LocalSegmentProvider();

    public static LocalSegmentProvider getInstance() {
        return ourInstance;
    }

    private SwiftSegmentManager manager;

    private LocalSegmentProvider() {
        manager = new LineSegmentManager();
    }

    public void registerSwiftSegmentManager(SwiftSegmentManager manager) {
        this.manager = manager;
    }

    @Override
    public Segment getSegment(SegmentKey key) {
        return manager.getSegment(key);
    }

    @Override
    public List<Segment> getSegment(SourceKey sourceKey) {
        return manager.getSegment(sourceKey);
    }

    @Override
    public ISegmentOperator getIndexSegmentOperator(SourceKey sourceKey, SwiftMetaData metaData) {
        try {
            return new HistorySegmentOperator(sourceKey, metaData, manager.getSegment(sourceKey));
        } catch (SwiftMetaDataException e) {
            return Crasher.crash(e);
        }
    }

    /**
     * @deprecated operator调到另外的类持有吧，这个是SegmentProvider，责任分出去
     */
    @Deprecated
    @Override
    public ISegmentOperator getIndexSegmentOperator(SourceKey sourceKey) {
        SwiftMetaData metaData = MetaDataXmlManager.getManager().getMetaData(sourceKey);
        return getIndexSegmentOperator(sourceKey, metaData);
    }

    @Override
    public ISegmentOperator getRealtimeSegmentOperator(SourceKey sourceKey, SwiftMetaData metaData) {
        try {
            return new IncreaseSegmentOperator(sourceKey, metaData, manager.getSegment(sourceKey));
        } catch (SwiftMetaDataException e) {
            return Crasher.crash(e);
        }
    }

    /**
     * @deprecated operator调到另外的类持有吧，这个是SegmentProvider，责任分出去
     */
    @Deprecated
    @Override
    public ISegmentOperator getRealtimeSegmentOperator(SourceKey sourceKey) {
        SwiftMetaData metaData = MetaDataXmlManager.getManager().getMetaData(sourceKey);
        return getRealtimeSegmentOperator(sourceKey, metaData);
    }

    @Override
    public ISegmentOperator getDecreaseSegmentOperator(SourceKey sourceKey, SwiftMetaData metaData) {
        try {
            return new DecreaseSegmentOperator(sourceKey, metaData, manager.getSegment(sourceKey));
        } catch (SwiftMetaDataException e) {
            return Crasher.crash(e);
        }
    }

    /**
     * @deprecated operator调到另外的类持有吧，这个是SegmentProvider，责任分出去
     */
    @Deprecated
    @Override
    public ISegmentOperator getDecreaseSegmentOperator(SourceKey sourceKey) {
        SwiftMetaData metaData = MetaDataXmlManager.getManager().getMetaData(sourceKey);
        return getDecreaseSegmentOperator(sourceKey, metaData);
    }

}
