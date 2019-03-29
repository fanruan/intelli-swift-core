package com.fr.swift.segment;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.segment.event.SegmentEvent;
import com.fr.swift.segment.operator.insert.BaseBlockImporter;
import com.fr.swift.segment.operator.insert.SwiftInserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.alloter.RowInfo;
import com.fr.swift.source.alloter.SegmentInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;

/**
 * @author anchore
 * @date 2018/6/5
 */
@SwiftBean(name = "incrementer")
@SwiftScope("prototype")
public class Incrementer<A extends SwiftSourceAlloter<?, RowInfo>> extends BaseBlockImporter<A> {

    public Incrementer(DataSource dataSource, A alloter) {
        super(dataSource, alloter);
    }

    @Override
    protected Inserting getInserting(SegmentKey segKey) {
        Segment seg = SegmentUtils.newSegment(segKey);
        return new Inserting(SwiftInserter.ofAppendMode(seg), seg, seg.isReadable() ? seg.getRowCount() : 0);
    }

    @Override
    protected void handleFullSegment(SegmentInfo segInfo) {
        // 增量块已满，transfer掉
        SegmentKey segKey = newSegmentKey(segInfo);
        SwiftEventDispatcher.fire(SegmentEvent.TRANSFER_REALTIME, segKey);
    }
}