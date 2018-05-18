package com.fr.swift.utils;

import com.finebi.conf.exception.FineEngineException;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.utils.FineTableUtils;
import com.fr.swift.adaptor.encrypt.SwiftEncryption;
import com.fr.swift.conf.business.table2source.TableToSourceConfig;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SourceKey;

/**
 * This class created on 2018/4/10
 *
 * @author Lucifer
 * @description 获取businesstable统一调用
 * @since Advanced FineBI Analysis 1.0
 * todo 看看能否把FineTableUtils的调用给去掉,避免循环的隐患
 */
public class BusinessTableUtils {

    public static FineBusinessTable getTableByFieldId(String fieldId) throws FineEngineException {
        String[] tableInfo = SwiftEncryption.decryptFieldId(fieldId);
        String tableId = tableInfo[0];

        return FineTableUtils.getTableByName(tableId);
    }

    public static FineBusinessTable getTableByTableName(String tableName) throws FineEngineException {
        return FineTableUtils.getTableByName(tableName);
    }

    public static String getFieldNameByFieldId(String fieldId) {
        return SwiftEncryption.decryptFieldId(fieldId)[1];
    }

    public static ColumnKey getColumnKey(String fieldId) {
        return new ColumnKey(getFieldNameByFieldId(fieldId));
    }

    public static String getSourceIdByFieldId(String fieldId) {
        return TableToSourceConfig.getInstance().getConfigByTableId(SwiftEncryption.decryptFieldId(fieldId)[0]);
    }

    public static SourceKey getSourceKey(String fieldId) {
        return new SourceKey(getSourceIdByFieldId(fieldId));
    }

    public static String getSourceIdByTableId(String tableId) {
        return TableToSourceConfig.getInstance().getConfigByTableId(tableId);
    }
}
