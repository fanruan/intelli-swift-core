package com.fr.swift.source.etl;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.AbstractDataSource;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.EtlDataSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
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
public class EtlSource extends AbstractDataSource implements EtlDataSource {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(EtlSource.class);
    @CoreField
    private List<SourceKey> basedKeys;

    private List<DataSource> basedSources;
    @CoreField
    private ETLOperator operator;
    @CoreField
    /**
     * 保存了使用的字段以及改名的结果，这边用index，没有用name是因为不能保证基础表的字段没有重名
     */
    private Map<Integer, String> fieldsInfo;

    /**
     * @param basedSources 上层表
     * @param operator     操作
     */
    public EtlSource(List<DataSource> basedSources, ETLOperator operator) {
        this(basedSources, operator, null);
    }

    /**
     * @param basedSources 上层表
     * @param operator     操作
     * @param fieldsInfo   使用的字段
     */
    public EtlSource(List<DataSource> basedSources, ETLOperator operator, Map<Integer, String> fieldsInfo) {
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
    public List<SourceKey> getBasedSourceKeys() {
        return basedKeys;
    }

    @Override
    public List<DataSource> getBasedSources() {
        return basedSources;
    }

    @Override
    public Map<Integer, String> getFieldsInfo() {
        return fieldsInfo;
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
        List<SwiftMetaDataColumn> originColumns = operator.getBaseColumns(metaDatas);
        List<SwiftMetaDataColumn> addColumnList = operator.getColumns(metaDatas);

        //新增字段放在原始字段之后
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        columnList.addAll(originColumns);
        columnList.addAll(addColumnList);
        metaData = new SwiftMetaDataBean(getSourceKey().getId(), columnList);
        checkFieldsInfo();
    }

    private void checkFieldsInfo() {
        if (fieldsInfo != null) {
            List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
            for (Map.Entry<Integer, String> entry : fieldsInfo.entrySet()) {
                try {
                    int columnIndex = entry.getKey() + 1;
                    columnList.add(new MetaDataColumnBean(entry.getValue(), metaData.getColumnRemark(columnIndex), metaData.getColumnType(columnIndex),
                            metaData.getPrecision(columnIndex), metaData.getScale(columnIndex), metaData.getColumnId(columnIndex)));
                } catch (SwiftMetaDataException e) {
                    LOGGER.error("field missed " + entry.getKey());
                }
            }
            metaData = new SwiftMetaDataBean(getSourceKey().getId(), columnList);
        }
    }

    private void initBasedSources() {
        //todo 配置里根据key初始化parents
    }
}
