package com.fr.swift.cloud.structure.external.map.intpairs;

import com.fr.swift.cloud.setting.PerformancePlugManager;
import com.fr.swift.cloud.source.ColumnTypeConstants.ClassType;
import com.fr.swift.cloud.structure.IntPair;
import com.fr.swift.cloud.structure.external.map.ExternalMap;
import com.fr.swift.cloud.util.Crasher;

import java.util.Comparator;
import java.util.List;

/**
 * @author anchore
 * @date 2018/1/5
 */
public class IntPairsExtMaps {
    @SuppressWarnings("unchecked")
    public static <K> ExternalMap<K, List<IntPair>> newExternalMap(ClassType classType, Comparator<K> c, String path) {
        long bufferSize = PerformancePlugManager.getInstance().getDiskSortDumpThreshold();
        switch (classType) {
            case INTEGER:
                return (ExternalMap<K, List<IntPair>>)
                        new Int2IntPairsExtMap(bufferSize, (Comparator<Integer>) c, path);
            case LONG:
            case DATE:
                return (ExternalMap<K, List<IntPair>>)
                        new Long2IntPairsExtMap(bufferSize, (Comparator<Long>) c, path);
            case DOUBLE:
                return (ExternalMap<K, List<IntPair>>)
                        new Double2IntPairsExtMap(bufferSize, (Comparator<Double>) c, path);
            case STRING:
                return (ExternalMap<K, List<IntPair>>)
                        new String2IntPairsExtMap(bufferSize, (Comparator<String>) c, path);
            default:
        }
        return Crasher.crash("unsupported classType: " + classType);
    }
}
