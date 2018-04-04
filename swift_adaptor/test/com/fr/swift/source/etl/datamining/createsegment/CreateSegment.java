package com.fr.swift.source.etl.datamining.createsegment;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.impl.BitSetMutableBitMap;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.relation.CubeLogicColumnKey;
import com.fr.swift.relation.CubeMultiRelation;
import com.fr.swift.relation.CubeMultiRelationPath;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.relation.RelationIndex;
import com.fr.swift.source.MetaDataColumn;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.SwiftMetaDataImpl;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/4/2 0002 09:31
 */
public class CreateSegment {
    public Segment getSegment() {
        return new Segment() {
            @Override
            public void flush() {

            }

            @Override
            public int getRowCount() {
                return 8;
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
            public RelationIndex getRelation(CubeLogicColumnKey f) {
                return null;
            }

            @Override
            public IResourceLocation getLocation() {
                return null;
            }

            @Override
            public Column getColumn(ColumnKey key) {
                if(key.getName().equals("column1")) {
                    return new CreateColumn1().getColumn();
                } else {
                    return new CreateColumn2().getColumn();
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
                MetaDataColumn column1 = new MetaDataColumn("column1", "column1", Types.INTEGER);
                MetaDataColumn column2 = new MetaDataColumn("column2", "column2", Types.INTEGER);
                List<SwiftMetaDataColumn> fieldList = new ArrayList<SwiftMetaDataColumn>();
                fieldList.add(column1);
                fieldList.add(column2);
                return new SwiftMetaDataImpl("table1", "table1", "table1", fieldList);
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
