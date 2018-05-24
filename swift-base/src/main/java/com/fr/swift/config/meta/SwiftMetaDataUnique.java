package com.fr.swift.config.meta;

import com.fr.config.holder.Conf;
import com.fr.config.holder.factory.Holders;
import com.fr.config.holder.impl.ObjectMapConf;
import com.fr.config.utils.UniqueKey;
import com.fr.stable.StringUtils;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.util.Crasher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @Author: Lucifer
 * @Description:
 * @Date: Created in 2018-3-8
 */
public class SwiftMetaDataUnique extends UniqueKey implements SwiftMetaData {
    private Conf<String> schema = Holders.simple(StringUtils.EMPTY);

    private Conf<String> tableName = Holders.simple(StringUtils.EMPTY);

    private Conf<String> remark = Holders.simple(StringUtils.EMPTY);

    private ObjectMapConf<Map<Integer, SwiftMetaDataColumn>> fields = Holders.objMap(new HashMap<Integer, SwiftMetaDataColumn>(), Integer.class, SwiftMetaDataColumn.class);

    public SwiftMetaDataUnique() {
    }

    public SwiftMetaDataUnique(SwiftMetaData meta) throws SwiftMetaDataException {
        this(meta.getSchemaName(), meta.getTableName(), meta.getRemark(), meta.getColumnMetas());
    }

    public SwiftMetaDataUnique(String schema, String tableName, String remark, List<SwiftMetaDataColumn> columns) {
        this.schema.set(schema);
        this.tableName.set(tableName);
        this.remark.set(remark);
        setFields(columns);
    }

    private void setFields(List<SwiftMetaDataColumn> columns) {
        for (int i = 0; i < columns.size(); i++) {
            fields.put(i + 1, new MetaDataColumnUnique(columns.get(i)));
        }
    }

    @Override
    public String getSchemaName() {
        return schema.get();
    }

    @Override
    public String getTableName() {
        return tableName.get();
    }

    @Override
    public String getRemark() {
        return remark.get();
    }

    private Map<Integer, SwiftMetaDataColumn> fields() {
        return fields.get();
    }

    @Override
    public int getColumnCount() {
        return fields().size();
    }

    @Override
    public SwiftMetaDataColumn getColumn(int index) {
        return fields().get(index);
    }

    @Override
    public SwiftMetaDataColumn getColumn(String columnName) {
        for (Entry<Integer, SwiftMetaDataColumn> entry : fields().entrySet()) {
            SwiftMetaDataColumn columnMeta = entry.getValue();
            if (columnMeta.getName().equals(columnName)) {
                return columnMeta;
            }
        }
        return Crasher.crash(columnName + " not found");
    }

    @Override
    public String getColumnName(int index) {
        return getColumn(index).getName();
    }

    @Override
    public String getColumnId(int index) {
        return getColumn(index).getId();
    }

    @Override
    public String getColumnRemark(int index) {
        return getColumn(index).getRemark();
    }

    @Override
    public int getColumnType(int index) {
        return getColumn(index).getType();
    }

    @Override
    public int getPrecision(int index) {
        return getColumn(index).getPrecision();
    }

    @Override
    public int getScale(int index) {
        return getColumn(index).getScale();
    }

    @Override
    public int getColumnIndex(String columnName) {
        for (Entry<Integer, SwiftMetaDataColumn> entry : fields().entrySet()) {
            if (entry.getValue().getName().equals(columnName)) {
                return entry.getKey();
            }
        }
        return Crasher.crash(columnName + " not found");
    }

    @Override
    public String getColumnId(String columnName) {
        return getColumn(columnName).getId();
    }

    @Override
    public List<String> getFieldNames() {
        Map<Integer, SwiftMetaDataColumn> fields = fields();
        List<String> fieldNames = new ArrayList<String>();
        for (int i = 1; i <= fields.size(); i++) {
            fieldNames.add(fields.get(i).getName());
        }
        return fieldNames;
    }

    @Override
    public List<SwiftMetaDataColumn> getColumnMetas() {
        Map<Integer, SwiftMetaDataColumn> fields = fields();
        List<SwiftMetaDataColumn> columns = new ArrayList<SwiftMetaDataColumn>();
        for (int i = 1; i <= fields.size(); i++) {
            columns.add(fields.get(i));
        }
        return columns;
    }
}