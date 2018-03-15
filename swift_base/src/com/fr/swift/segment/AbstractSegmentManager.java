package com.fr.swift.segment;

import com.fr.general.ComparatorUtils;
import com.fr.swift.config.IConfigSegment;
import com.fr.swift.config.IMetaData;
import com.fr.swift.config.IMetaDataColumn;
import com.fr.swift.config.ISegmentKey;
import com.fr.swift.config.conf.MetaDataConfig;
import com.fr.swift.config.conf.SegmentConfig;
import com.fr.swift.cube.io.Types;
import com.fr.swift.source.MetaDataColumn;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.SwiftMetaDataImpl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by pony on 2017/10/16.
 */
public abstract class AbstractSegmentManager implements SwiftSegmentManager {
    @Override
    public synchronized List<Segment> getSegment(SourceKey sourceKey) {
        // 并发地拿，比如多个column indexer同时进行索引， 要同步下
        List<Segment> segments = new ArrayList<Segment>();
        List<SegmentKey> keys = getSegmentKey(sourceKey.getId());
//                SegmentXmlManager.getManager().getSegmentKeys(sourceKey);
        if (null != keys && !keys.isEmpty()) {
            for (SegmentKey key : keys) {
                try {
                    Segment segment = getSegment(key);
                    if (null != segment) {
                        segments.add(segment);
                    }
                } catch (Exception e) {
                    return segments;
                }
            }
        }
        return segments;
    }

    @Override
    public boolean isSegmentsExist(SourceKey sourceKey) {
        return !getSegmentKey(sourceKey.getId()).isEmpty();
    }

    private List<SegmentKey> getSegmentKey(String sourceKey) {
        IConfigSegment segments = SegmentConfig.getInstance().getSegmentByKey(sourceKey);
        List<SegmentKey> target = new ArrayList<SegmentKey>();
        if (null != segments) {
            Map<String, ISegmentKey> segmentKeys = segments.getSegments();
            Iterator<Map.Entry<String, ISegmentKey>> iterator = segmentKeys.entrySet().iterator();
            while (iterator.hasNext()) {
                ISegmentKey key = iterator.next().getValue();
                Types.StoreType storeType = Types.StoreType.FINE_IO;
                String type = key.getStoreType();
                if (!ComparatorUtils.equalsIgnoreCase(Types.StoreType.FINE_IO.name(), type)) {
                    storeType = Types.StoreType.MEMORY;
                }
                SegmentKey segmentKey = new SegmentKey(key.getName(), URI.create(key.getUri()), key.getSegmentOrder(), storeType);
                segmentKey.setSourceId(key.getSourceId());
                target.add(segmentKey);
            }
            Collections.sort(target, new Comparator<SegmentKey>() {
                @Override
                public int compare(SegmentKey o1, SegmentKey o2) {
                    return o1.getSegmentOrder() - o2.getSegmentOrder();
                }
            });
        }
        return target;
    }

    protected SwiftMetaData getMetaData(String sourceKey) {
        IMetaData iMetaData = MetaDataConfig.getInstance().getMetaDataByKey(sourceKey);
        List<IMetaDataColumn> fieldList = iMetaData.getFieldList();
        List<SwiftMetaDataColumn> fields = new ArrayList<SwiftMetaDataColumn>();
        for (int i = 0, len = fieldList.size(); i < len; i++) {
            IMetaDataColumn column = fieldList.get(i);
            fields.add(new MetaDataColumn(column.getName(), column.getRemark(), column.getType(),
                    column.getPrecision(), column.getScale(), column.getColumnId()));
        }
        return new SwiftMetaDataImpl(iMetaData.getTableName(), iMetaData.getRemark(), iMetaData.getSchema(), fields);
    }
}
