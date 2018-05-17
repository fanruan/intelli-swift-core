package com.fr.swift.utils;

import com.fr.general.ComparatorUtils;
import com.fr.swift.provider.IndexStuffType;

import java.util.Map;

/**
 * This class created on 2018/5/16
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class IndexStuffTypeUtils {

    public static IndexStuffType gloableType(Map<String, String> packageIdName) {
        return IndexStuffType.GLOABLE.setData(packageIdName);
    }

    public static IndexStuffType tableType(String tableName) {
        return IndexStuffType.TABLE.setData(tableName);
    }

    public static IndexStuffType packageType(Map<String, String> packageIdName) {
        return IndexStuffType.PACKAGE.setData(packageIdName);
    }

    public static boolean isEqual(IndexStuffType indexStuffType, String key) {
        switch (indexStuffType) {
            case TABLE:
                return ComparatorUtils.equals(String.valueOf(indexStuffType.getData()), key);
            case GLOABLE:
            case PACKAGE:
                Map<String, String> packageIdMap = (Map<String, String>) indexStuffType.getData();
                return packageIdMap.containsKey(key);
            default:
                return false;
        }
    }
}
