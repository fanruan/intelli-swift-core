package com.finebi.cube.engine.map.map2;

import com.finebi.cube.engine.map.*;
import com.fr.bi.manager.PerformancePlugManager;
import com.fr.bi.stable.constant.DBConstant;

import java.util.Comparator;

/**
 * Created by wang on 2016/8/25.
 */
public class ExternalIntArrayMapFactory {
    public static IntArrayListExternalMap getIntListExternalMap(int columnType, Comparator comparator, String dataFolder) {
        long bufferSize = PerformancePlugManager.getInstance().getDiskSortDumpThreshold();
        switch (columnType) {
            case DBConstant.CLASS.INTEGER:
                return new IntegerIntArrayListExternalMap(bufferSize,comparator,dataFolder);
            case DBConstant.CLASS.DOUBLE:
                return new DoubleIntArrayListExternalMap(bufferSize,comparator,dataFolder);
            case DBConstant.CLASS.LONG:
                return new LongIntArrayListExternalMap(bufferSize,comparator,dataFolder);
            case DBConstant.CLASS.STRING :
                return new StringIntArrayListExternalMap(bufferSize,comparator,dataFolder);
            default:
                return new StringIntArrayListExternalMap(bufferSize,comparator,dataFolder);
        }
    }
}
