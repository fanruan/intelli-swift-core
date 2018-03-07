package com.fr.swift.segment;

import com.fr.base.FRContext;
import com.fr.file.XMLFileManager;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLReadable;
import com.fr.stable.xml.XMLableReader;
import com.fr.swift.cube.io.Types;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.Util;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2017/12/19
 *
 * fixme 配置可用10.0持久化了
 */
public class SegmentXmlManager extends XMLFileManager {

    private static SegmentXmlManager manager;

    private static final String SEGMENT_MANAGER_FILE_NAME = "segmentManager.xml";
    private static final String SEGMENT_MANAGER_TAG = "SegmentManager";
    private static final String SOURCE_KEY_TAG = "SourceKey";
    private ConcurrentHashMap<SourceKey, List<SegmentKey>> source2Segment = new ConcurrentHashMap<SourceKey, List<SegmentKey>>();

    public static SegmentXmlManager getManager() {
        if (null == manager) {
            synchronized (SegmentXmlManager.class) {
                if (manager == null) {
                    manager = new SegmentXmlManager();
                }
            }
        }
        return manager;
    }

    private SegmentXmlManager() {
        readXMLFile();
    }

    @Override
    public String fileName() {
        return SEGMENT_MANAGER_FILE_NAME;
    }

    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isChildNode()) {
            String tag = reader.getTagName();
            if (SOURCE_KEY_TAG.equals(tag)) {
                SourceKey sourceKey = new SourceKey(reader.getAttrAsString("id", StringUtils.EMPTY));
                readSegmentKey(reader, sourceKey, getSegmentKeys(sourceKey));
            }
        }
    }

    private void readSegmentKey(XMLableReader reader, final SourceKey sourceKey, final List<SegmentKey> list) {
        reader.readXMLObject(new XMLReadable() {

            @Override
            public void readXML(XMLableReader reader) {
                if (reader.isChildNode()) {
                    if ("SegmentKey".equals(reader.getTagName())) {
                        SegmentKey key = new SegmentKey();
                        key.setSegmentOrder(reader.getAttrAsInt("order", -1));
                        key.setUri(URI.create(reader.getAttrAsString("uri", StringUtils.EMPTY)));
                        key.setSourceId(sourceKey.getId());
                        key.setStoreType(Types.StoreType.valueOf(reader.getAttrAsString("storeType", Types.StoreType.FINE_IO.name())));

                        list.add(key);
                        source2Segment.put(sourceKey, list);
                    }
                }
            }
        });
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(SEGMENT_MANAGER_TAG);
        Set<SourceKey> sourceKeys = source2Segment.keySet();
        for (SourceKey key : sourceKeys) {
            writer.startTAG(SOURCE_KEY_TAG);
            writer.attr("id", key.getId());
            List<SegmentKey> segmentKeys = source2Segment.get(key);
            for (int i = 0; i < segmentKeys.size(); i++) {
                writer.startTAG("SegmentKey");
                writer.attr("order", segmentKeys.get(i).getSegmentOrder());
                writer.attr("uri", segmentKeys.get(i).getUri().getPath());
                writer.attr("storeType", segmentKeys.get(i).getStoreType().name());
                writer.end();
            }
            writer.end();
        }
        writer.end();
    }

    public List<SegmentKey> getSegmentKeys(SourceKey sourceKey) {
        Util.requireNonNull(sourceKey);
        List<SegmentKey> list = source2Segment.get(sourceKey);
        if (null != list) {
            Collections.sort(list, new Comparator<SegmentKey>() {
                @Override
                public int compare(SegmentKey o1, SegmentKey o2) {
                    return o1.getSegmentOrder() - o2.getSegmentOrder();
                }
            });
            return list;
        }
        return new ArrayList<SegmentKey>();
    }

    public void addSegment(SourceKey sourceKey, SegmentKey segmentKey) throws Exception {
        segmentKey.setSourceId(sourceKey.getId());
        List<SegmentKey> segmentKeys = getSegmentKeys(sourceKey);
        segmentKeys.add(segmentKey);
        source2Segment.put(sourceKey, segmentKeys);
        FRContext.getCurrentEnv().writeResource(this);
    }
}
