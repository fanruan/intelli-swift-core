package com.fr.swift.source.etl.selfrelation;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.source.core.MD5Utils;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by Handsome on 2018/1/22 0022 10:56
 */
public class OneUnionRelationOperator extends AbstractOperator {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(OneUnionRelationOperator.class);
    @CoreField
    private String idColumnName;
    @CoreField
    private List<String> showColumns;
    @CoreField
    private LinkedHashMap<String, Integer> columns;
    @CoreField
    private int columnType;
    @CoreField
    private String columnName;
    @CoreField
    private List<SwiftMetaDataColumn> columnList;
    @CoreField
    private String[] addedColumns;

    public OneUnionRelationOperator(String idColumnName, List<String> showColumns, LinkedHashMap<String, Integer> columns, int columnType, String columnName) {
        this.idColumnName = idColumnName;
        this.showColumns = showColumns;
        this.columns = columns;
        this.columnType = columnType;
        this.columnName = columnName;
    }

    public String getIdColumnName() {
        return idColumnName;
    }

    public List<String> getShowColumns() {
        return showColumns;
    }

    public LinkedHashMap<String, Integer> getColumns() {
        return columns;
    }

    public int getColumnType() {
        return columnType;
    }

    public String getColumnName() {
        return columnName;
    }

    @Override
    public List<String> getNewAddedName() {
        return Arrays.asList(addedColumns);
    }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] metaDatas) {

        Iterator<Map.Entry<String, Integer>> it;
        columnList = new ArrayList<SwiftMetaDataColumn>();
        for (SwiftMetaData metaData : metaDatas) {
            SwiftMetaData table = metaDatas[0];
            try {
                int size = table.getColumn(this.idColumnName).getPrecision();
//                for (int j = 0; j < table.getColumnCount(); j++) {
//                    columnList.add(table.getColumn(table.getColumnName(j + 1)));
//                }
                for (String s : this.showColumns) {
                    it = columns.entrySet().iterator();
                    while (it.hasNext()) {
                        Entry<String, Integer> entry = it.next();
                        columnList.add(new MetaDataColumnBean(s + "-" + entry.getKey(), this.columnType, size,
                                MD5Utils.getMD5String(new String[]{(s + "-" + entry.getKey() + this.columnType)})));
                    }
                }
            } catch (SwiftMetaDataException e) {
                LOGGER.error("getting meta's column information failed", e);
            }

        }

        addedColumns = new String[columnList.size()];
        int index = 0;
        for (SwiftMetaDataColumn temp : columnList) {
            addedColumns[index++] = temp.getName();
        }
        return columnList;
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.ONE_UNION_RELATION;
    }
}
