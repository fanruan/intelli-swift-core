package com.fr.swift.segment.event;

import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.event.SwiftEventListener;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentUtils;

import java.util.List;

/**
 * @author anchore
 * @date 2018/12/29
 */
public class IndexHistoryListener implements SwiftEventListener<List<Segment>> {

    static {
        SwiftEventDispatcher.listen(SegmentEvent.INDEX_HISTORY, new IndexHistoryListener());
    }

//    @Override
//    public void on(List<SegmentKey> segKeys) {
//        if (segKeys == null || segKeys.isEmpty()) {
//            return;
//        }
//
//        try {
//            ArrayList<Segment> segs = new ArrayList<Segment>();
//            SourceKey tableKey = segKeys.get(0).getTable();
//            SwiftTablePathBean tablePathBean = SwiftContext.get().getBean(SwiftTablePathService.class).get(tableKey.getId());
//            Integer tmpDir = tablePathBean == null ? 0 : tablePathBean.getTmpDir();
//            SwiftMetaData meta = SwiftDatabase.getInstance().getTable(tableKey).getMetadata();
//
//            for (SegmentKey segKey : segKeys) {
//                Segment seg = SegmentUtils.newSegment(new ResourceLocation(new CubePathBuilder(segKey).setTempDir(tmpDir).build(), segKey.getStoreType()), meta);
//                segs.add(seg);
//            }
//
//            SegmentUtils.indexSegmentIfNeed(segs);
//        } catch (Exception e) {
//            SwiftLoggers.getLogger().error(e);
//        }
//    }

    public static void listen() {
    }

    @Override
    public void on(List<Segment> segs) {
        try {
            SegmentUtils.indexSegmentIfNeed(segs);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }
}