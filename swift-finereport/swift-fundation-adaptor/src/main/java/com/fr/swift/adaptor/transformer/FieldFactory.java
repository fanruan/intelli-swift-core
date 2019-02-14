package com.fr.swift.adaptor.transformer;

import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.constant.BIConfConstants;
import com.finebi.conf.internalimp.field.FineBusinessFieldImp;
import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.fr.engine.utils.StringUtils;
import com.fr.general.ComparatorUtils;
import com.fr.swift.adaptor.encrypt.SwiftEncryption;
import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.ColumnTypeUtils;
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
        List<String> columnRemarks = new ArrayList<String>();
        for (int i = 1; i <= swiftMetaData.getColumnCount(); i++) {
            String columnName = swiftMetaData.getColumnName(i);
            String columnRemark = swiftMetaData.getColumnRemark(i);
            if (escapeMap != null && escapeMap.containsKey(columnName)) {
                columnRemark = escapeMap.get(columnName);
            }
            if (columnRemark == null || ComparatorUtils.equals(columnRemark, StringUtils.EMPTY)) {
                columnRemark = columnName;
            }

            columnRemark = checkColumnRemark(columnRemarks, columnRemark);

            columnRemarks.add(columnRemark);
            String tableId = businessTableId == null ? swiftMetaData.getTableName() : businessTableId;
            FineBusinessFieldImp fineBusinessField = new FineBusinessFieldImp(SwiftEncryption.encryptFieldId(tableId, columnName), columnName, columnRemark);
            fineBusinessField.setEngineType(FineEngineType.Cube);
            fineBusinessField.setType(toBiType(ColumnTypeUtils.getColumnType(swiftMetaData.getColumn(i))));
            fineBusinessField.setUsable(true);
            fineBusinessFieldList.add(fineBusinessField);
        }
        return fineBusinessFieldList;
    }


    private static String checkColumnRemark(List<String> columnRemarks, String remark) {
        String tmpRemark = remark;
        int count = 1;
        while (columnRemarks.contains(tmpRemark)) {
            tmpRemark = remark + count++;
        }
        return tmpRemark;
    }

    public static List<SwiftMetaDataColumn> transformFields2Column(List<FineBusinessField> fineBusinessFieldList) {
        List<SwiftMetaDataColumn> swiftMetaDataColumnList = new ArrayList<SwiftMetaDataColumn>();
        for (FineBusinessField fineBusinessField : fineBusinessFieldList) {
            //String name, String remark, int sqlType, int precision, int scale
            SwiftMetaDataColumn metaDataColumn = new MetaDataColumnBean(fineBusinessField.getName(), fineBusinessField.getTransferName(), fineBusinessField.getType(), fineBusinessField.getSize(), fineBusinessField.getType());
            swiftMetaDataColumnList.add(metaDataColumn);
        }
        return swiftMetaDataColumnList;
    }

    public static ColumnType toColumnType(int biType) {
        switch (biType) {
            case BIConfConstants.CONF.COLUMN.NUMBER:
                return ColumnType.NUMBER;
            case BIConfConstants.CONF.COLUMN.DATE:
                return ColumnType.DATE;
            default:
                return ColumnType.STRING;
        }
    }

    public static int toBiType(ColumnType biType) {
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
