package com.fr.swift.source;

import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.core.CoreField;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by pony on 2018/1/16.
 * 外部数据源增加使用部分字段与类型转换的功能
 */
public abstract class AbstractOuterDataSource extends AbstractDataSource implements OuterDataSource {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(AbstractOuterDataSource.class);

    /**
     * 保存使用的字段与columnType类型,空或者null表示没变
     * 无序，生成metatada的时候按照outermetadata的顺序来
     */
    @CoreField
    protected Map<String, ColumnType> fieldColumnTypes;

    /**
     * 外部数据源的meta
     */
    protected SwiftMetaData outerMetaData;

    public AbstractOuterDataSource() {
    }

    public AbstractOuterDataSource(Map<String, ColumnType> fieldColumnTypes) {
        this.fieldColumnTypes = fieldColumnTypes;
    }

    public Map<String, ColumnType> getFieldColumnTypes() {
        return fieldColumnTypes;
    }

    @Override
    public SwiftMetaData getOuterMetadata() {
        if (outerMetaData == null) {
            initOuterMetaData();
        }
        return outerMetaData;
    }


    protected abstract void initOuterMetaData();

    @Override
    protected final void initMetaData() {
        SwiftMetaData outerMetadata = getOuterMetadata();
        if (fieldColumnTypes == null || fieldColumnTypes.isEmpty()) {
            metaData = outerMetadata;
        } else {
            List<SwiftMetaDataColumn> columns = new ArrayList<SwiftMetaDataColumn>();
            try {
                for (int i = 0; i < outerMetadata.getColumnCount(); i++){
                    int columnIndex = i + 1;
                    SwiftMetaDataColumn outerColumn = outerMetadata.getColumn(columnIndex);
                    String fieldName = outerColumn.getName();
                    if (fieldColumnTypes.containsKey(fieldName)){
                        ColumnType type = fieldColumnTypes.get(fieldName);
                        SwiftMetaDataColumn column = outerColumn;
                        ColumnType outerColumnType = ColumnTypeUtils.getColumnType(outerColumn);
                        if (outerColumnType != type) {
                            column = ColumnTypeUtils.convertColumn(type, outerColumn);
                        }
                        columns.add(column);
                    }
                }
                metaData = new SwiftMetaDataBean(outerMetadata.getTableName(), columns);
            } catch (SwiftMetaDataException e) {
                LOGGER.error("get outerMetadata info failed ", e);
            }
        }
    }
}
