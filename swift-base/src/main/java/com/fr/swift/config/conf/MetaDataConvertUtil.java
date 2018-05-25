package com.fr.swift.config.conf;

import com.fr.swift.config.IMetaData;
import com.fr.swift.config.IMetaDataColumn;
import com.fr.swift.config.conf.service.SwiftConfigServiceProvider;
import com.fr.swift.config.pojo.MetaDataColumnPojo;
import com.fr.swift.config.pojo.SwiftMetaDataPojo;
import com.fr.swift.config.unique.MetaDataColumnUnique;
import com.fr.swift.config.unique.SwiftMetaDataUnique;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.MetaDataColumn;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.SwiftMetaDataImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/3/16
 */
public class MetaDataConvertUtil {
    public static SwiftMetaData getSwiftMetaDataBySourceKey(String sourceKey) {
        IMetaData iMetaData = SwiftConfigServiceProvider.getInstance().getMetaDataByKey(sourceKey);
        return toSwiftMetadata(iMetaData);
    }

    public static IMetaData convert2ConfigMetaData(SwiftMetaData metaData) throws SwiftMetaDataException {
        List<MetaDataColumnUnique> columns = new ArrayList<MetaDataColumnUnique>();
        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            SwiftMetaDataColumn column = metaData.getColumn(i);
            columns.add(new MetaDataColumnUnique(column.getType(), column.getName(), column.getRemark(), column.getPrecision(), column.getScale(), column.getColumnId()));
        }
        return new SwiftMetaDataUnique(metaData.getSchemaName(), metaData.getTableName(), metaData.getRemark(), columns);
    }

    public static <T extends IMetaDataColumn> SwiftMetaData toSwiftMetadata(IMetaData<T> iMetaData) {
        if (iMetaData instanceof SwiftMetaDataPojo) {
            return new SwiftMetaDataImpl((SwiftMetaDataPojo) iMetaData);
        }
        List<SwiftMetaDataColumn> columnMetas = new ArrayList<SwiftMetaDataColumn>();
        for (IMetaDataColumn columnMeta : iMetaData.getFieldList()) {
            columnMetas.add(new MetaDataColumn(columnMeta.getName(), columnMeta.getRemark(),
                    columnMeta.getType(), columnMeta.getPrecision(), columnMeta.getScale(),
                    columnMeta.getColumnId()
            ));
        }
        return new SwiftMetaDataImpl(iMetaData.getTableName(), iMetaData.getRemark(), iMetaData.getSchema(), columnMetas);
    }

    public static <T extends IMetaDataColumn> SwiftMetaDataPojo toSwiftMetadataPojo(IMetaData<T> iMetaData) {
        if (iMetaData instanceof SwiftMetaDataPojo) {
            return (SwiftMetaDataPojo) iMetaData;
        }
        List<MetaDataColumnPojo> columnMetas = new ArrayList<MetaDataColumnPojo>();
        for (T columnMeta : iMetaData.getFieldList()) {
            columnMetas.add(new MetaDataColumnPojo(columnMeta.getType(), columnMeta.getName(), columnMeta.getRemark(),
                    columnMeta.getPrecision(), columnMeta.getScale(),
                    columnMeta.getColumnId()
            ));
        }
        return new SwiftMetaDataPojo(iMetaData.getSchema(), iMetaData.getTableName(), iMetaData.getRemark(), columnMetas);
    }
}
