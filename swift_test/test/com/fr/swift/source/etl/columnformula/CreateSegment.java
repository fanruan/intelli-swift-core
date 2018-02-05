package com.fr.swift.source.etl.columnformula;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.impl.BitSetMutableBitMap;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.relation.RelationIndex;
import com.fr.swift.source.*;
import com.fr.swift.source.etl.CreateColumn;
import com.fr.swift.source.etl.CreateColumnForSum;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/2/2 0002 10:32
 */
public class CreateSegment {
    public Segment getSegment() {
        return new Segment() {
            @Override
            public void flush() {

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
            public RelationIndex getRelation(DataSource f) {
                return null;
            }

            @Override
            public IResourceLocation getLocation() {
                return null;
            }

            @Override
            public Column getColumn(ColumnKey key) {
                if(key.getName().equals("column1")) {
                    return new CreateColumn().getColumn();
                } else {
                    return new CreateColumnForSum().getColumn();
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
                fieldList.add(new MetaDataColumn("column1", 31));
                fieldList.add(new MetaDataColumn("column2", 31));
                SwiftMetaData metaData = new SwiftMetaDataImpl("table1", fieldList);
                return metaData;
            }

            @Override
            public void release() {

            }
        };
    }
}
