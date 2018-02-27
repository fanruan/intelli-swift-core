package com.fr.swift.structure.external.map.intlist.map2;

import com.fr.swift.setting.PerformancePlugManager;
import com.fr.swift.source.ColumnTypeConstants.ClassType;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.external.map.ExternalMap;

import java.util.Comparator;

/**
 * @author wang
 * @date 2016/8/25
 */
public class ExternalIntArrayMapFactory {
    @SuppressWarnings("unchecked")
    public static <K> ExternalMap<K, IntList> getIntListExternalMap(ClassType classType, Comparator<K> comparator, String dataFolder, boolean isKeepDiskFile) {
        long bufferSize = PerformancePlugManager.getInstance().getDiskSortDumpThreshold();
        switch (classType) {
            case INTEGER:
                return (ExternalMap<K, IntList>) new IntegerIntArrayListExternalMap(bufferSize, comparator, dataFolder);
            case DOUBLE:
                return (ExternalMap<K, IntList>) new DoubleIntArrayListExternalMap(bufferSize, comparator, dataFolder);
            case LONG:
                return (ExternalMap<K, IntList>) new LongIntArrayListExternalMap(bufferSize, comparator, dataFolder);
            case STRING:
                return (ExternalMap<K, IntList>) new StringIntArrayListExternalMap(bufferSize, comparator, dataFolder, isKeepDiskFile);
            default:
                return (ExternalMap<K, IntList>) new StringIntArrayListExternalMap(bufferSize, comparator, dataFolder, isKeepDiskFile);
        }
    }
}
