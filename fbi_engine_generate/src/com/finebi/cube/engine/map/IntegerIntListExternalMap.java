package com.finebi.cube.engine.map;

import com.fr.bi.stable.structure.collection.list.IntList;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * Created by FineSoft on 2015/7/16.
 */
public class IntegerIntListExternalMap extends IntListExternalMap<Integer> {
    public IntegerIntListExternalMap(Comparator comparator, String dataFolder) {
        super(comparator, dataFolder);
    }

    public IntegerIntListExternalMap(Long bufferSize, Comparator comparator, String dataFolder) {
        super(bufferSize, comparator, dataFolder);
    }
//    @Override
//    public ExternalMapIO<Integer, IntList> getExternalMapIO(String id_filePath) {
//        return new IntegerIntListExternalMapIO(id_filePath);
//    }

    @Override
    public ExternalMapIO<Integer, IntList> getExternalMapIO(String id_filePath) {
        return (ExternalMapIO) new IntegerIntListExternalMapIO(id_filePath);
    }

    @Override
    public ExternalMapIO<Integer, IntList> getMemMapIO(TreeMap<Integer, IntList> currentContainer) {
        return new StringMemMapIO(currentContainer);
    }

    private class StringMemMapIO extends MemExternalMapIO<Integer> {
        public StringMemMapIO(TreeMap<Integer, IntList> currentContainer) {
            super(currentContainer);
        }
    }
}