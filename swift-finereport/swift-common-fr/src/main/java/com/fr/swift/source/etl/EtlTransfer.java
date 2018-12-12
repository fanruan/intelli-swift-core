package com.fr.swift.source.etl;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.util.Util;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author pony
 * @date 2017/12/5
 */
public class EtlTransfer implements SwiftSourceTransfer {

    private ETLTransferOperator operator;
    private SwiftMetaData metaData;
    private List<SwiftMetaData> basedMetas;
    private List<Segment[]> basedSegments;
    private Map<Integer, String> fieldsInfo;

    public EtlTransfer(ETLTransferOperator operator, SwiftMetaData metaData, List<SwiftMetaData> basedMetas, List<Segment[]> basedSegments, Map<Integer, String> fieldsInfo) {
        Util.requireNonNull(operator, metaData, basedMetas, basedMetas);
        this.operator = operator;
        this.metaData = metaData;
        this.basedMetas = basedMetas;
        this.basedSegments = basedSegments;
        this.fieldsInfo = fieldsInfo;
    }

    @Override
    public SwiftResultSet createResultSet() {
        SwiftResultSet resultSet = operator.createResultSet(metaData, basedMetas, basedSegments);
        if (fieldsInfo == null || fieldsInfo.isEmpty()) {
            return resultSet;
        }
        int[] indexes = new int[fieldsInfo.size()];
        Iterator<Integer> it = fieldsInfo.keySet().iterator();
        int index = 0;
        while (it.hasNext()) {
            indexes[index++] = it.next();
        }
        return new RowShiftSwiftResultSet(resultSet, indexes);
    }

    private class RowShiftSwiftResultSet implements SwiftResultSet {
        private SwiftResultSet resultSet;
        private int[] indices;

        public RowShiftSwiftResultSet(SwiftResultSet resultSet, int[] indexes) {
            this.resultSet = resultSet;
            this.indices = indexes;
        }

        @Override
        public void close() throws SQLException {
            resultSet.close();
        }

        @Override
        public boolean hasNext() throws SQLException {
            return resultSet.hasNext();
        }

        @Override
        public int getFetchSize() {
            return 0;
        }

        @Override
        public SwiftMetaData getMetaData() throws SQLException {
            return resultSet.getMetaData();
        }

        @Override
        public Row getNextRow() throws SQLException {
            return new ShiftRow(resultSet.getNextRow(), indices);
        }
    }

    private class ShiftRow implements Row, Serializable {
        private static final long serialVersionUID = -537161590337548026L;
        private Row row;
        private int[] shiftIndex;

        private ShiftRow(Row row, int[] shiftIndex) {
            this.row = row;
            this.shiftIndex = shiftIndex;
        }

        @Override
        public <V> V getValue(int index) {
            return row.getValue(shiftIndex[index]);
        }

        @Override
        public int getSize() {
            return shiftIndex.length;
        }
    }
}
