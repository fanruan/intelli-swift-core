package com.fr.swift.conf.business.field;

import com.finebi.common.internalimp.config.fieldinfo.FieldInfoImpl;
import com.finebi.common.structure.config.entryinfo.EntryInfo;
import com.finebi.common.structure.config.fieldinfo.FieldInfo;
import com.finebi.conf.constant.BICommonConstants;
import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.fr.engine.bi.config.Field;
import com.fr.engine.bi.config.SimpleField;
import com.fr.engine.constant.Type;
import com.fr.swift.adaptor.encrypt.SwiftEncryption;

import java.util.List;

/**
 * @author yee
 * @date 2018/4/8
 */
public class FieldInfoHelper {
    public static FieldInfo createFieldInfo(EntryInfo entryInfo, FineBusinessTable table) {
        return createDefaultFieldInfo(entryInfo, table);
    }

    private static FieldInfo createDefaultFieldInfo(EntryInfo entryInfo, FineBusinessTable table) {
        List<FineBusinessField> fields = table.getFields();
        Field[] infoFields = new Field[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            FineBusinessField businessField = fields.get(i);
            infoFields[i] = new SimpleField(SwiftEncryption.encryptFieldId(table.getName(), businessField.getName()), businessField.getName(), convert2Type(businessField.getType()), true, businessField.getFieldGroupType());
        }
        return FieldInfoImpl.create(entryInfo.getID(), infoFields);
    }


    private static Type convert2Type(int fieldType) {
        switch (fieldType) {
            case BICommonConstants.COLUMN.NUMBER:
                return Type.Decimal;
            case BICommonConstants.COLUMN.STRING:
                return Type.String;
            case BICommonConstants.COLUMN.DATE:
                return Type.Date;
            default:
                return Type.String;
        }
    }

}
