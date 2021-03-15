package com.fr.swift.cloud.structure.external.map.intlist;

import com.fr.swift.cloud.setting.PerformancePlugManager;
import com.fr.swift.cloud.source.ColumnTypeConstants.ClassType;
import com.fr.swift.cloud.structure.array.IntList;
import com.fr.swift.cloud.structure.external.map.ExternalMap;
import com.fr.swift.cloud.util.Crasher;

import java.util.Comparator;

/**
 * @author wang
 * @date 2016/8/25
 */
public class IntListExternalMapFactory {
    @SuppressWarnings("unchecked")
    public static <K> ExternalMap<K, IntList> getIntListExternalMap(ClassType classType, Comparator<K> comparator, String dataFolder, boolean isKeepDiskFile) {
        long bufferSize = PerformancePlugManager.getInstance().getDiskSortDumpThreshold();
        switch (classType) {
            case DOUBLE:
                return (ExternalMap<K, IntList>) new DoubleIntListExternalMap(bufferSize, comparator, dataFolder);
            case INTEGER:
                return (ExternalMap<K, IntList>) new IntegerIntListExternalMap(bufferSize, comparator, dataFolder);
            case LONG:
            case DATE:
                return (ExternalMap<K, IntList>) new LongIntListExternalMap(bufferSize, comparator, dataFolder);
            case STRING:
                return (ExternalMap<K, IntList>) new StringIntListExternalMap(bufferSize, comparator, dataFolder, isKeepDiskFile);
            default:
                return Crasher.crash(String.format("no external map for type %s", classType));
        }
    }
}
