package com.fr.swift.cloud.segment.collate;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.segment.Segment;
import com.fr.swift.cloud.segment.SegmentKey;
import com.fr.swift.cloud.segment.SegmentService;
import com.fr.swift.cloud.source.alloter.SwiftSourceAlloter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author anchore
 * @date 2018/7/27
 */
public class SwiftFragmentFilter implements FragmentFilter {

    /**
     * 碎片块数 >= 5 < 100
     */
    public static final int FRAGMENT_NUMBER = 5;

    public static final int MAX_FRAGMENT_NUMBER = 50;

    private final SegmentService segmentService = SwiftContext.get().getBean(SegmentService.class);

    private SwiftSourceAlloter alloter;

    public SwiftFragmentFilter(SwiftSourceAlloter alloter) {
        this.alloter = alloter;
    }

    @Override
    public List<SegmentKey> filter(Collection<SegmentKey> segKeys) {
        int fragmentSize = alloter.getAllotRule().getCapacity() * 4 / 5;
        List<SegmentKey> fragmentKeys = new ArrayList<SegmentKey>();
        for (SegmentKey segKey : segKeys) {
            Segment seg = segmentService.getSegment(segKey);
            if (isNeed2Collect(seg, fragmentSize)) {
                fragmentKeys.add(segKey);
            }
        }
        if (fragmentKeys.size() >= FRAGMENT_NUMBER) {
            return fragmentKeys;
        }
        return new ArrayList<>();
    }

    /**
     * @param seg
     * @return
     */
    private boolean isNeed2Collect(Segment seg, int fragmentSize) {
        try {
            if (seg.getLocation().getStoreType().isTransient()) {
                return true;
            }
            if (seg.getRowCount() < fragmentSize) {
                return true;
            }
            ImmutableBitMap allShowIndex = seg.getAllShowIndex();
            if (!allShowIndex.isFull()) {
                return allShowIndex.getCardinality() < fragmentSize;
            }
            return false;
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return false;
        } finally {
            seg.release();
        }
    }
}