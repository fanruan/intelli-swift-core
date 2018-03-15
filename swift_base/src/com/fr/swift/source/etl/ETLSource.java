package com.fr.swift.source.etl;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.AbstractDataSource;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.ETLDataSource;
import com.fr.swift.source.MetaDataColumn;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.SwiftMetaDataImpl;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.Util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author pony
 * @date 2017/11/27
 * 存配置的时候为了层级少点，只存key，构造的时候只能通过对象构造
 */
public class ETLSource extends AbstractDataSource implements ETLDataSource {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(ETLSource.class);
    @CoreField
    private List<SourceKey> basedKeys;

    private List<DataSource> basedSources;
    @CoreField
    private ETLOperator operator;
    /**
     * 保存了使用的字段以及改名的结果，这边用index，没有用name是因为不能保证基础表的字段没有重名
     */
    private Map<Integer, String> fieldsInfo;

    /**
     * @param basedSources 上层表
     * @param operator     操作
     */
    public ETLSource(List<DataSource> basedSources, ETLOperator operator) {
        this(basedSources, operator, null);
    }

    /**
     * @param basedSources 上层表
     * @param operator     操作
     * @param fieldsInfo   使用的字段
     */
    public ETLSource(List<DataSource> basedSources, ETLOperator operator, Map<Integer, String> fieldsInfo) {
        Util.requireNonNull(basedSources, operator);
        checkFieldsInfoValues(fieldsInfo);
        this.basedSources = basedSources;
        this.operator = operator;
        this.fieldsInfo = fieldsInfo;
        initParentKeys();
    }

    private void checkFieldsInfoValues(Map<Integer, String> fieldsInfo) {
        if (fieldsInfo != null) {
            Set<String> values = new HashSet<String>(fieldsInfo.values());
            if (fieldsInfo.size() != values.size()) {
                Crasher.crash("etl fieldNames must be different");
            }
        }
    }

    @Override
    public ETLOperator getOperator() {
        return operator;
    }

    @Override
    public List<DataSource> getBasedSources() {
        return basedSources;
    }

    private void initParentKeys() {
        basedKeys = new ArrayList<SourceKey>();
        for (DataSource source : basedSources) {
            basedKeys.add(source.getSourceKey());
        }
    }

    @Override
    protected void initMetaData() {
        if (basedSources == null) {
            initBasedSources();
        }
        SwiftMetaData[] metaDatas = new SwiftMetaData[basedSources.size()];
        for (int i = 0; i < metaDatas.length; i++) {
            metaDatas[i] = basedSources.get(i).getMetadata();
        }
        List<SwiftMetaDataColumn> originColumns = getOriginColumns(metaDatas);
        List<SwiftMetaDataColumn> columnList = operator.getColumns(metaDatas);
        columnList.addAll(originColumns);
        metaData = new SwiftMetaDataImpl(getSourceKey().getId(), columnList);
        checkFieldsInfo();
    }

    private void checkFieldsInfo() {
        if (fieldsInfo != null) {
            List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
            for (Map.Entry<Integer, String> entry : fieldsInfo.entrySet()) {
                try {
                    int columnIndex = entry.getKey() + 1;
                    //改名这边不需要转义了
                    columnList.add(new MetaDataColumn(entry.getValue(), null, metaData.getColumnType(columnIndex),
                            metaData.getPrecision(columnIndex), metaData.getScale(columnIndex), metaData.getColumnId(columnIndex)));
                } catch (SwiftMetaDataException e) {
                    LOGGER.error("field missed " + entry.getKey());
                }
            }
            metaData = new SwiftMetaDataImpl(getSourceKey().getId(), columnList);
        }
    }

    private void initBasedSources() {
        //todo 配置里根据key初始化parents
    }

    private List<SwiftMetaDataColumn> getOriginColumns(SwiftMetaData[] metaDatas) {
        //如果是新增列,metadata需包含基础表的字段信息
        List<SwiftMetaDataColumn> columns = new ArrayList<SwiftMetaDataColumn>();
        try {
            OperatorType type = operator.getOperatorType();
            if (type.isAddColumn()) {
                for (SwiftMetaData basedMeta : metaDatas) {
                    for (int i = 0; i < basedMeta.getColumnCount(); i++) {
                        int index = i + 1;
                        columns.add(new MetaDataColumn(basedMeta.getColumnName(index), basedMeta.getColumnRemark(index),
                                basedMeta.getColumnType(index), basedMeta.getPrecision(index), basedMeta.getScale(index), basedMeta.getColumnId(index)));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(operator.getOperatorType() + "get origin meta failed");
        }
        return columns;
    }
}
