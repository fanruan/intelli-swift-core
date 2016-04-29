package com.finebi.cube.engine.map;


import com.fr.bi.stable.structure.collection.list.IntList;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * Created by FineSoft on 2015/7/14.
 */
public class DoubleIntListExternalMap extends IntListExternalMap<Double> {

    public DoubleIntListExternalMap(Comparator comparator, String dataFolder) {
        super(comparator, dataFolder);
    }

    public DoubleIntListExternalMap(Long bufferSize, Comparator comparator, String dataFolder) {
        super(bufferSize, comparator, dataFolder);
    }

    @Override
    public ExternalMapIO<Double, IntList> getExternalMapIO(String id_filePath) {
        return (ExternalMapIO) new DoubleIntListExternalMapIO(id_filePath);
    }

    @Override
    public ExternalMapIO<Double, IntList> getMemMapIO(TreeMap<Double, IntList> currentContainer) {
        return new StringMemMapIO(currentContainer);
    }

    private class StringMemMapIO extends MemExternalMapIO<Double> {
        public StringMemMapIO(TreeMap<Double, IntList> currentContainer) {
            super(currentContainer);
        }
    }
}