package com.fr.bi.stable.utils;

import com.fr.bi.stable.data.BIField;

/**
 * Created by 小灰灰 on 2016/3/14.
 */
public class BIIDUtils {
    private static final int UUID_LENGTH = 16;

    public static String createFieldID(BIField field) {
        return field.getTableBelongTo().getID().getIdentityValue() + field.getFieldName();
    }

    public static String getFieldNameFromFieldID(String fieldID) {
        return fieldID.substring(UUID_LENGTH);
    }

    public static String getTableIDFromFieldID(String fieldID) {
        return fieldID.substring(0, UUID_LENGTH);
    }

    public static boolean isFakeTable(String tableID) {
        if (tableID.length() == UUID_LENGTH) {
            return false;
        } else {
            return true;
        }
    }
}
