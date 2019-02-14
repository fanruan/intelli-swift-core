package com.fr.swift.segment.collate;

import com.fr.swift.SwiftContext;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.source.alloter.SwiftSourceAlloter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 2018/7/27
 */
public class SwiftFragmentCollectRule implements FragmentCollectRule {


    /**
     * 碎片块数 》= 10
     */
    public static final int FRAGMENT_NUMBER = 10;

    private final SwiftSegmentManager localSegments = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);

    private SwiftSourceAlloter alloter;

    public SwiftFragmentCollectRule(SwiftSourceAlloter alloter) {
        this.alloter = alloter;
    }

    @Override
    public List<SegmentKey> collect(List<SegmentKey> segKeys) {
        int fragmentSize = alloter.getAllotRule().getCapacity() * 2 / 3;
        List<SegmentKey> fragmentKeys = new ArrayList<SegmentKey>();
        for (SegmentKey segKey : segKeys) {
            Segment seg = localSegments.getSegment(segKey);
            if (isNeed2Collect(seg, fragmentSize)) {
                fragmentKeys.add(segKey);
            }
        }
        if (fragmentKeys.size() >= FRAGMENT_NUMBER) {
            return fragmentKeys;
        }
        return new ArrayList<SegmentKey>();
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
        }
    }
}