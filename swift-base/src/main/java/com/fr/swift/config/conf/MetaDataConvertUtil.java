package com.fr.swift.config.conf;

import com.fr.swift.config.IMetaData;
import com.fr.swift.config.IMetaDataColumn;
import com.fr.swift.config.conf.service.SwiftConfigServiceProvider;
import com.fr.swift.config.conf.bean.MetaDataColumnBean;
import com.fr.swift.config.conf.bean.SwiftMetaDataBean;
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
        List<MetaDataColumnBean> columns = new ArrayList<MetaDataColumnBean>();
        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            SwiftMetaDataColumn column = metaData.getColumn(i);
            columns.add(new MetaDataColumnBean(column.getType(), column.getName(), column.getRemark(), column.getPrecision(), column.getScale(), column.getColumnId()));
        }
        return new SwiftMetaDataBean(metaData.getSchemaName(), metaData.getTableName(), metaData.getRemark(), columns);
    }

    public static <T extends IMetaDataColumn> SwiftMetaData toSwiftMetadata(IMetaData<T> iMetaData) {
        if (iMetaData instanceof SwiftMetaDataBean) {
            return new SwiftMetaDataImpl((SwiftMetaDataBean) iMetaData);
        }
        List<SwiftMetaDataColumn> columnMetas = new ArrayList<SwiftMetaDataColumn>();
        for (IMetaDataColumn columnMeta : iMetaData.getFields()) {
            columnMetas.add(new MetaDataColumn(columnMeta.getName(), columnMeta.getRemark(),
                    columnMeta.getType(), columnMeta.getPrecision(), columnMeta.getScale(),
                    columnMeta.getColumnId()
            ));
        }
        return new SwiftMetaDataImpl(iMetaData.getTableName(), iMetaData.getRemark(), iMetaData.getSchema(), columnMetas);
    }

    public static <T extends IMetaDataColumn> SwiftMetaDataBean toSwiftMetadataPojo(IMetaData<T> iMetaData) {
        if (iMetaData instanceof SwiftMetaDataBean) {
            return (SwiftMetaDataBean) iMetaData;
        }
        List<MetaDataColumnBean> columnMetas = new ArrayList<MetaDataColumnBean>();
        for (T columnMeta : iMetaData.getFields()) {
            columnMetas.add(new MetaDataColumnBean(columnMeta.getType(), columnMeta.getName(), columnMeta.getRemark(),
                    columnMeta.getPrecision(), columnMeta.getScale(),
                    columnMeta.getColumnId()
            ));
        }
        return new SwiftMetaDataBean(iMetaData.getSchema(), iMetaData.getTableName(), iMetaData.getRemark(), columnMetas);
    }
}
