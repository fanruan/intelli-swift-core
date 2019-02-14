package com.fr.swift.manager;


import com.fr.swift.config.service.SwiftTablePathService;
import com.fr.swift.segment.AbstractSegmentManager;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.container.SegmentContainer;
import com.fr.swift.source.SourceKey;

/**
 * @author yee
 * @date 2017/12/18
 */
public class LineSegmentManager extends AbstractSegmentManager {

    public LineSegmentManager() {
        super(SegmentContainer.NORMAL);
    }

    protected LineSegmentManager(SegmentContainer container) {
        super(container);
    }

    @Override
    protected Integer getCurrentFolder(SwiftTablePathService service, SourceKey sourceKey) {
        return service.getTablePath(sourceKey.getId());
    }

    @Override
    protected Segment getSegment(SegmentKey segmentKey, Integer currentFolder) {
        return SegmentUtils.newSegment(segmentKey, currentFolder);
    }

}
