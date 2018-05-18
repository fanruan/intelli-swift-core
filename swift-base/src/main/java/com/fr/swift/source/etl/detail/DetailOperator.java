package com.fr.swift.source.etl.detail;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;
import com.fr.swift.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pony
 * @date 2018/1/9
 * etl第一步，选字段
 * 看成是主表与新增表的组合，外加使用部分字段
 */
public class DetailOperator extends AbstractOperator {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(DetailOperator.class);
    /**
     * 主表的字段
     */
    @CoreField
    private List<ColumnKey[]> fields;
    /**
     * 子表的字段
     */
    @CoreField
    private List<ColumnKey> baseFields;

    private List<SwiftMetaData> baseMetas;

    /**
     * @param fields    每个基础表的字段,不包括最子表
     * @param baseMetas 基础表的metadata,不包括最子表
     */
    public DetailOperator(List<ColumnKey[]> fields, List<ColumnKey> baseFields, List<SwiftMetaData> baseMetas) {
        Util.requireNonNull(fields, baseMetas);
        this.fields = fields;
        this.baseFields = baseFields;
        this.baseMetas = baseMetas;
    }


    public List<ColumnKey[]> getFields() {
        return fields;
    }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] metaDatas) {
        List<SwiftMetaDataColumn> columns = new ArrayList<SwiftMetaDataColumn>();
        for (int i = 0; i < fields.size(); i++) {
            SwiftMetaData metaData = baseMetas.get(i);
            ColumnKey[] columnKeys = fields.get(i);
            Util.requireNonNull(columnKeys);
            for (ColumnKey columnKey : columnKeys) {
                try {
                    SwiftMetaDataColumn column = metaData.getColumn(columnKey.getName());
                    columns.add(column);
                } catch (SwiftMetaDataException e) {
                    LOGGER.error("the field " + columnKey.getName() + " get meta failed", e);
                }
            }
        }
        return columns;
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.DETAIL;
    }

    @Override
    public List<SwiftMetaDataColumn> getBaseColumns(SwiftMetaData[] metaDatas) {
        List<SwiftMetaDataColumn> columns = new ArrayList<SwiftMetaDataColumn>();
        SwiftMetaData basedMeta = metaDatas[0];
        try {
            for (ColumnKey key : baseFields) {
                columns.add(basedMeta.getColumn(key.getName()));
            }
        } catch (Exception e) {
            LOGGER.error(getOperatorType() + "get origin meta failed");
        }
        return columns;
    }

    @Override
    public List<String> getNewAddedName() {
        List<String> addColumnNames = new ArrayList<String>();
        for (ColumnKey[] columnKeys : fields) {
            Util.requireNonNull(columnKeys);
            for (ColumnKey columnKey : columnKeys) {
                addColumnNames.add(columnKey.getName());
            }
        }
        return addColumnNames;
    }
}
