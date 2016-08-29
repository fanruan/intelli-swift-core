package com.finebi.cube.engine.map;

import com.fr.bi.manager.PerformancePlugManager;
import com.fr.bi.stable.constant.DBConstant;

import java.util.Comparator;

/**
 * Created by wang on 2016/8/25.
 */
public class ExternalIntListMapFactory {
    public static IntListExternalMap getIntListExternalMap(int columnType, Comparator comparator, String dataFolder) {
        long bufferSize = PerformancePlugManager.getInstance().getDiskSortDumpThreshold();
        switch (columnType) {
            case DBConstant.CLASS.INTEGER:
                return new IntegerIntListExternalMap(bufferSize,comparator,dataFolder);
            case DBConstant.CLASS.DOUBLE:
                return new DoubleIntListExternalMap(bufferSize,comparator,dataFolder);
            case DBConstant.CLASS.LONG:
                return new LongIntListExternalMap(bufferSize,comparator,dataFolder);
            case DBConstant.CLASS.STRING :
                return new StringIntListExternalMap(bufferSize,comparator,dataFolder);
            default:
                return new StringIntListExternalMap(bufferSize,comparator,dataFolder);
        }
    }
}
