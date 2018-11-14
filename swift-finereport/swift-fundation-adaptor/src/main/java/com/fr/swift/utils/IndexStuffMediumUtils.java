package com.fr.swift.utils;

import com.fr.general.ComparatorUtils;
import com.fr.swift.provider.IndexStuffMedium;

import java.util.Map;

/**
 * This class created on 2018/5/16
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class IndexStuffMediumUtils {

    public static boolean isEqual(IndexStuffMedium indexStuffMedium, String key) {
        switch (indexStuffMedium.getIndexStuffType()) {
            case TABLE:
                return ComparatorUtils.equals(String.valueOf(indexStuffMedium.getData()), key);
            case GLOABLE:
            case PACKAGE:
                Map<String, String> packageIdMap = (Map<String, String>) indexStuffMedium.getData();
                return packageIdMap.containsKey(key);
            default:
                return false;
        }
    }
}
