package com.fr.swift.adaptor.transformer;

import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.constant.BIConfConstants;
import com.finebi.conf.internalimp.field.FineBusinessFieldImp;
import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.fr.swift.adaptor.encrypt.SwiftEncryption;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.MetaDataColumn;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2018-1-3 10:18:46
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class FieldFactory {

    public static List<FineBusinessField> transformColumns2Fields(SwiftMetaData swiftMetaData, String businessTableId, Map<String, String> escapeMap) throws SwiftMetaDataException {
        List<FineBusinessField> fineBusinessFieldList = new ArrayList<FineBusinessField>();
        for (int i = 1; i <= swiftMetaData.getColumnCount(); i++) {
            String columnName = swiftMetaData.getColumnName(i);
            int columnType = swiftMetaData.getColumnType(i);
            String columnRemark = swiftMetaData.getColumnRemark(i);
            int precision = swiftMetaData.getPrecision(i);
            int scale = swiftMetaData.getScale(i);
            if (escapeMap != null && escapeMap.containsKey(columnName)) {
                columnRemark = escapeMap.get(columnName);
            }
            String tableId = businessTableId == null ? swiftMetaData.getTableName() : businessTableId;
            FineBusinessFieldImp fineBusinessField = new FineBusinessFieldImp(SwiftEncryption.encryptFieldId(tableId, columnName), columnName, columnRemark);
            fineBusinessField.setEngineType(FineEngineType.Cube);
            fineBusinessField.setType(transformSwiftColumnType2BIColumnType(ColumnTypeUtils.sqlTypeToColumnType(columnType, precision, scale)));
            fineBusinessField.setUsable(true);
            fineBusinessFieldList.add(fineBusinessField);
        }
        return fineBusinessFieldList;
    }


    public static List<SwiftMetaDataColumn> transformFields2Column(List<FineBusinessField> fineBusinessFieldList) {

        List<SwiftMetaDataColumn> swiftMetaDataColumnList = new ArrayList<SwiftMetaDataColumn>();
        for (FineBusinessField fineBusinessField : fineBusinessFieldList) {
            //String name, String remark, int sqlType, int precision, int scale
            SwiftMetaDataColumn metaDataColumn = new MetaDataColumn(fineBusinessField.getName(), fineBusinessField.getTransferName(), fineBusinessField.getType(), fineBusinessField.getSize(), fineBusinessField.getType());
            swiftMetaDataColumnList.add(metaDataColumn);
        }
        return swiftMetaDataColumnList;
    }

    public static ColumnType transformBIColumnType2SwiftColumnType(int biType) {
        switch (biType) {
            case BIConfConstants.CONF.COLUMN.NUMBER:
                return ColumnType.NUMBER;
            case BIConfConstants.CONF.COLUMN.DATE:
                return ColumnType.DATE;
            default:
                return ColumnType.STRING;
        }
    }

    public static int transformSwiftColumnType2BIColumnType(ColumnType biType) {
        switch (biType) {
            case NUMBER:
                return BIConfConstants.CONF.COLUMN.NUMBER;
            case DATE:
                return BIConfConstants.CONF.COLUMN.DATE;
            default:
                return BIConfConstants.CONF.COLUMN.STRING;
        }
    }
}
