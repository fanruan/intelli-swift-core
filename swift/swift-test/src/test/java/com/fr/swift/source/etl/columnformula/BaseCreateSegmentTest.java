package com.fr.swift.source.etl.columnformula;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.impl.BitSetMutableBitMap;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.relation.CubeMultiRelation;
import com.fr.swift.segment.relation.CubeMultiRelationPath;
import com.fr.swift.segment.relation.RelationIndex;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.etl.BaseCreateColumnForSumTest;
import com.fr.swift.source.etl.BaseCreateColumnTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/2/2 0002 10:32
 */
public class BaseCreateSegmentTest {
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
                if(key.getName().equals("column1")) {
                    return new BaseCreateColumnTest().getColumn();
                } else {
                    return new BaseCreateColumnForSumTest().getColumn();
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
                List<SwiftMetaDataColumn> fieldList = new ArrayList<SwiftMetaDataColumn>();
                fieldList.add(new MetaDataColumnBean("column1", 31));
                fieldList.add(new MetaDataColumnBean("column2", 31));
                SwiftMetaData metaData = new SwiftMetaDataBean("table1", fieldList);
                return metaData;
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
