package com.fr.swift.structure.external.map.intlist.map2;

import com.fr.swift.setting.PerformancePlugManager;
import com.fr.swift.source.ColumnTypeConstants.ClassType;

import java.util.Comparator;

/**
 * Created by wang on 2016/8/25.
 */
public class ExternalIntArrayMapFactory {
    public static IntArrayListExternalMap getIntListExternalMap(ClassType classType, Comparator comparator, String dataFolder, boolean isKeepDiskFile) {
        long bufferSize = PerformancePlugManager.getInstance().getDiskSortDumpThreshold();
        switch (classType) {
            case INTEGER:
                return new IntegerIntArrayListExternalMap(bufferSize, comparator, dataFolder);
            case DOUBLE:
                return new DoubleIntArrayListExternalMap(bufferSize, comparator, dataFolder);
            case LONG:
                return new LongIntArrayListExternalMap(bufferSize, comparator, dataFolder);
            case STRING:
                return new StringIntArrayListExternalMap(bufferSize, comparator, dataFolder, isKeepDiskFile);
            default:
                return new StringIntArrayListExternalMap(bufferSize, comparator, dataFolder, isKeepDiskFile);
        }
    }
}
