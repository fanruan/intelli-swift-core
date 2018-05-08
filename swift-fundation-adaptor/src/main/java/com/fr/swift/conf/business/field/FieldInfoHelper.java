package com.fr.swift.conf.business.field;

import com.finebi.common.internalimp.config.fieldinfo.FieldInfoImpl;
import com.finebi.common.structure.config.entryinfo.EntryInfo;
import com.finebi.common.structure.config.fieldinfo.FieldInfo;
import com.fr.engine.bi.config.Field;
import com.fr.engine.bi.config.SimpleField;
import com.fr.engine.constant.Type;
import com.fr.swift.adaptor.encrypt.SwiftEncryption;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;


/**
 * @author yee
 * @date 2018/4/8
 */
public class FieldInfoHelper {
    public static FieldInfo createFieldInfo(EntryInfo entryInfo, SwiftMetaData table) throws SwiftMetaDataException {
        return createDefaultFieldInfo(entryInfo, table);
    }

    private static FieldInfo createDefaultFieldInfo(EntryInfo entryInfo, SwiftMetaData table) throws SwiftMetaDataException {
        int columnCount = table.getColumnCount();
        Field[] infoFields = new Field[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            SwiftMetaDataColumn businessField = table.getColumn(i);
            infoFields[i - 1] = new SimpleField(SwiftEncryption.encryptFieldId(table.getTableName(), businessField.getName()), businessField.getName(), convert2Type(ColumnTypeUtils.getColumnType(businessField)), true);
        }
        return FieldInfoImpl.create(entryInfo.getID(), infoFields);
    }


    private static Type convert2Type(ColumnTypeConstants.ColumnType type) {
        switch (type) {
            case NUMBER:
                return Type.Decimal;
            case STRING:
                return Type.String;
            case DATE:
                return Type.Date;
            default:
                return Type.String;
        }
    }

}
