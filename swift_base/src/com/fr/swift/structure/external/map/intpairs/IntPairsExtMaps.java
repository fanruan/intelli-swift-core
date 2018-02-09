package com.fr.swift.structure.external.map.intpairs;

import com.fr.swift.setting.PerformancePlugManager;
import com.fr.swift.source.ColumnTypeConstants.ClassType;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.external.map.ExternalMap;
import com.fr.swift.util.Crasher;

import java.util.Comparator;
import java.util.List;

/**
 * @author anchore
 * @date 2018/1/5
 */
public class IntPairsExtMaps {
    @SuppressWarnings("unchecked")
    public static <K> ExternalMap<K, List<Pair<Integer, Integer>>> newExternalMap(ClassType classType, Comparator<K> c, String path) {
        long bufferSize = PerformancePlugManager.getInstance().getDiskSortDumpThreshold();
        switch (classType) {
            case INTEGER:
            case LONG:
            case DATE:
                return (ExternalMap<K, List<Pair<Integer, Integer>>>)
                        new Long2IntPairsExtMap(bufferSize, (Comparator<Long>) c, path);
            case DOUBLE:
                return (ExternalMap<K, List<Pair<Integer, Integer>>>)
                        new Double2IntPairsExtMap(bufferSize, (Comparator<Double>) c, path);
            case STRING:
                return (ExternalMap<K, List<Pair<Integer, Integer>>>)
                        new String2IntPairsExtMap(bufferSize, (Comparator<String>) c, path);
            default:
        }
        return Crasher.crash("unsupported classType: " + classType);
    }
}
