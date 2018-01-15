package com.fr.swift.adaptor.transformer;

import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.internalimp.field.FineBusinessFieldImp;
import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.SwiftMetaData;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018-1-3 10:18:46
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class FieldFactory {

    public static List<FineBusinessField> transformColumns2Fields(SwiftMetaData swiftMetaData) throws SwiftMetaDataException {
        List<FineBusinessField> fineBusinessFieldList = new ArrayList<FineBusinessField>();
        for (int i = 1; i <= swiftMetaData.getColumnCount(); i++) {
            String columnName = swiftMetaData.getColumnName(i);
            int columnType = swiftMetaData.getColumnType(i);
            String columnRemark = swiftMetaData.getColumnRemark(i);
            int precision = swiftMetaData.getPrecision(i);
            int scale = swiftMetaData.getScale(i);
            FineBusinessField fineBusinessField = new FineBusinessFieldImp(columnName, ColumnTypeUtils.sqlTypeToClassType(columnType, precision, scale), precision, columnRemark, FineEngineType.Cube);
            fineBusinessFieldList.add(fineBusinessField);
        }
        return fineBusinessFieldList;
    }
}
