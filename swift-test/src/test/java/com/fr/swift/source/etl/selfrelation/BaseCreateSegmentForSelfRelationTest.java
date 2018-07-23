package com.fr.swift.source.etl.selfrelation;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.impl.BitSetMutableBitMap;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.relation.CubeMultiRelation;
import com.fr.swift.relation.CubeMultiRelationPath;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.relation.RelationIndex;
import com.fr.swift.source.SwiftMetaData;

/**
 * Created by Handsome on 2018/1/19 0019 11:55
 */
public class BaseCreateSegmentForSelfRelationTest {

    public Segment getSegment() {
        return new Segment() {
            @Override
            public void flush() {

            }

            @Override
            public int getRowCount() {
                return 9;
            }

            @Override
            public void putRowCount(int rowCount) {

            }

            @Override
            public RelationIndex getRelation(CubeMultiRelation f) {
                return null;
            }

            @Override
            public RelationIndex getRelation(CubeMultiRelationPath f) {
                return null;
            }

            @Override
            public RelationIndex getRelation(ColumnKey f, CubeMultiRelationPath relationPath) {
                return null;
            }

            @Override
            public IResourceLocation getLocation() {
                return null;
            }

            @Override
            public boolean isReadable() {
                return false;
            }

            @Override
            public Column getColumn(ColumnKey key) {
                if(key.getName().equals("ID")) {
                    return new BaseCreateColumnForSelfRelation1Test().getColumn();
                } else if(key.getName().equals("NAME")) {
                    return new BaseCreateColumnForSelfRelation2Test().getColumn();
                }else if(key.getName().equals("VALUE")) {
                    return new BaseCreateColumnForSelfRelation3Test().getColumn();
                }else if(key.getName().equals("region")) {
                    return new BaseCreateColumnForSelfRelation4Test().getColumn();
                }else if(key.getName().equals("mmm")) {
                    return new BaseCreateColumnForSelfRelation5Test().getColumn();
                }else if(key.getName().equals("ddd")) {
                    return new BaseCreateColumnForSelfRelation6Test().getColumn();
                }else{
                    return new BaseCreateColumnForSelfRelation7Test().getColumn();
                }
            }

            @Override
            public ImmutableBitMap getAllShowIndex() {
                MutableBitMap bitMap = BitSetMutableBitMap.newInstance();
                for(int i = 0; i < getRowCount(); i++) {
                    bitMap.add(i);
                }
                return bitMap;
            }

            @Override
            public void putAllShowIndex(ImmutableBitMap bitMap) {

            }

            @Override
            public SwiftMetaData getMetaData() {
                return null;
            }

            @Override
            public void release() {

            }

            @Override
            public boolean isHistory() {
                return false;
            }
        };
    }
}
