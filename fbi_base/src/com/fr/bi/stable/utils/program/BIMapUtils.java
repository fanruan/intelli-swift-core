package com.fr.bi.stable.utils.program;

import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;

/**
 * Created by Connery on 2015/12/25.
 */
public class BIMapUtils {
    public static Map unmodifiedCollection(Map collection) {
        if (collection instanceof SortedMap) {
            return Collections.unmodifiableSortedMap((SortedMap) collection);
        } else {
            return Collections.unmodifiableMap(collection);
        }
    }
}