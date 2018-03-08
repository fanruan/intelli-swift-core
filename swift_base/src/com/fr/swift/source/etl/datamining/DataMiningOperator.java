package com.fr.swift.source.etl.datamining;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.MetaDataColumn;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;
import com.fr.swift.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/1/17 0017 10:45
 */
public class DataMiningOperator extends AbstractOperator {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(DataMiningOperator.class);
    private List<List<ColumnKey>> lists = new ArrayList<List<ColumnKey>>();

    public DataMiningOperator(List<List<ColumnKey>> lists) {
        this.lists = lists;
    }

    public List<List<ColumnKey>> getColumnKeyList() {
        return this.lists;
    }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] tables) {
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        SwiftMetaData[] smd = new SwiftMetaData[tables.length];
        for (int i = 0; i < smd.length; i++) {
            smd[i] = tables[i];
        }
        for (int i = 0; i < this.lists.size(); i++) {
            List<ColumnKey> list = this.lists.get(i);
            int type = 0;
            int columnSize = 0;
            for (int j = 1; j < list.size(); j++) {
                Util.requireNonNull(list.get(j));
                try {
                    SwiftMetaDataColumn singleColumn = smd[j - 1].getColumn(list.get(j).getName());
                    type = singleColumn.getType();
                    columnSize = Math.max(columnSize, singleColumn.getPrecision());
                } catch (SwiftMetaDataException e) {
                    LOGGER.error("the field " + list.get(j).getName() + " get meta failed", e);
                }
            }
            // TODO  maybe need to consider about type
            columnList.add(new MetaDataColumn(list.get(0).getName(), type, columnSize));
        }
        return columnList;
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.DATAMINING;
    }
}
