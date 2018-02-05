package com.fr.swift.source.etl.selfrelation;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.impl.BitSetMutableBitMap;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.relation.RelationIndex;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.etl.CreateColumn;
import com.fr.swift.source.etl.CreateColumn2;

/**
 * Created by Handsome on 2018/1/19 0019 11:55
 */
public class CreateSegmentForSelfRelation {

    public Segment getSegment() {
        return new Segment() {
            @Override
            public void flush() {

            }

            @Override
            public RelationIndex getRelation(DataSource f) {
                return null;
            }

            @Override
            public Types.StoreType getStoreType() {
                return null;
            }

            @Override
            public int getRowCount() {
                return 9;
            }

            @Override
            public void putRowCount(int rowCount) {

            }

            @Override
            public IResourceLocation getLocation() {
                return null;
            }

            @Override
            public Column getColumn(ColumnKey key) {
                if(key.getName().equals("ID")) {
                    return new CreateColumnForSelfRelation1().getColumn();
                } else if(key.getName().equals("NAME")) {
                    return new CreateColumnForSelfRelation2().getColumn();
                }else if(key.getName().equals("VALUE")) {
                    return new CreateColumnForSelfRelation3().getColumn();
                }else if(key.getName().equals("region")) {
                    return new CreateColumnForSelfRelation4().getColumn();
                }else if(key.getName().equals("mmm")) {
                    return new CreateColumnForSelfRelation5().getColumn();
                }else if(key.getName().equals("ddd")) {
                    return new CreateColumnForSelfRelation6().getColumn();
                }else{
                    return new CreateColumnForSelfRelation7().getColumn();
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
        };
    }
}
