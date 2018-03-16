package com.fr.swift.config.conf;

import com.fr.swift.config.IMetaData;
import com.fr.swift.config.IMetaDataColumn;
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
public class ConfigObjUtil {
    public static SwiftMetaData convert2SwiftMetaData(String sourceKey) {
        IMetaData iMetaData = MetaDataConfig.getInstance().getMetaDataByKey(sourceKey);
        List<IMetaDataColumn> fieldList = iMetaData.getFieldList();
        List<SwiftMetaDataColumn> fields = new ArrayList<SwiftMetaDataColumn>();
        for (int i = 0, len = fieldList.size(); i < len; i++) {
            IMetaDataColumn column = fieldList.get(i);
            fields.add(new MetaDataColumn(column.getName(), column.getRemark(), column.getType(),
                    column.getPrecision(), column.getScale(), column.getColumnId()));
        }
        return new SwiftMetaDataImpl(iMetaData.getTableName(), iMetaData.getRemark(), iMetaData.getSchema(), fields);
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
}
