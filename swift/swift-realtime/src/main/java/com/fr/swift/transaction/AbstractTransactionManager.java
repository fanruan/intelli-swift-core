package com.fr.swift.transaction;


import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.SwiftMetaData;
import com.fr.third.springframework.stereotype.Service;

/**
 * This class created on 2018/6/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Service
public abstract class AbstractTransactionManager implements TransactionManager<Segment> {

    private Segment realSegment;

    private SwiftMetaData metaData;

    private ImmutableBitMap oldAllShowIndex;

    @Override
    public void setOldAttatch(Segment realSegment) {
        this.realSegment = realSegment;
        this.metaData = realSegment.getMetaData();
    }

    @Override
    public void start() {
        try {
            oldAllShowIndex = realSegment.getAllShowIndex();
        } catch (Exception e) {
            oldAllShowIndex = BitMaps.newAllShowBitMap(0);
        }
    }

    @Override
    public void commit() {

    }

    @Override
    public void rollback() {
        //回滚不改数据和索引，只改allShowIndex
        realSegment.putAllShowIndex(oldAllShowIndex);
    }

    @Override
    public void close() {

    }
}
