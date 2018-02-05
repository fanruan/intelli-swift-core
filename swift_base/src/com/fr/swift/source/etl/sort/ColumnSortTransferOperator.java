package com.fr.swift.source.etl.sort;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.ETLTransferOperator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class created on 2018-1-31 17:26:31
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class ColumnSortTransferOperator implements ETLTransferOperator {

    private Map<String, Integer> fieldsSortedMap;

    public ColumnSortTransferOperator(Map<String, Integer> fieldsSortedMap) {
        this.fieldsSortedMap = fieldsSortedMap;
    }

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(ColumnSortTransferOperator.class);

    @Override
    public SwiftResultSet createResultSet(SwiftMetaData metaData, List<SwiftMetaData> basedMetas, List<Segment[]> basedSegments) {
        Segment[] basedSegment = basedSegments.get(0);

        Set<String> key = fieldsSortedMap.keySet();
        Iterator<String> keyIt = key.iterator();
        String sortField = null;
        int sortType = 1;
        while (keyIt.hasNext()) {
            sortField = keyIt.next();
            sortType = fieldsSortedMap.get(sortField);
            break;
        }
        final int sortedType = sortType;

        List<SortData> sortDataList = new ArrayList<SortData>();

        for (Segment segment : basedSegment) {
            int rowCount = segment.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                SortData sortData = new SortData(i, segment.getColumn(new ColumnKey(sortField)).getDetailColumn().get(i));
                sortDataList.add(sortData);
            }
        }
        Collections.sort(sortDataList, new Comparator<SortData>() {
            @Override
            public int compare(SortData data1, SortData data2) {
                if (sortedType == 1) {
                    return String.valueOf(data1.getObject()).compareTo(String.valueOf(data2.getObject()));
                } else {
                    return -String.valueOf(data1.getObject()).compareTo(String.valueOf(data2.getObject()));
                }
            }
        });

        List<List<Object>> dataLists = new ArrayList<List<Object>>();
        for (Segment segment : basedSegment) {
            for (int i = 0; i < sortDataList.size(); i++) {
                int index = sortDataList.get(i).getIndex();
                List<Object> dataList = new ArrayList<Object>();
                try {
                    for (int j = 1; j <= metaData.getColumnCount(); j++) {
                        dataList.add(segment.getColumn(new ColumnKey(metaData.getColumnName(j))).getDetailColumn().get(index));
                    }
                    dataLists.add(dataList);
                } catch (SwiftMetaDataException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        ColumnSortOperatorResultSet columnSortOperatorResultSet = new ColumnSortOperatorResultSet(dataLists, metaData);
        return columnSortOperatorResultSet;
    }

    private class SortData {
        private int index;
        private Object object;

        public SortData(int index, Object object) {
            this.index = index;
            this.object = object;
        }

        public int getIndex() {
            return index;
        }

        public Object getObject() {
            return object;
        }
    }
}
