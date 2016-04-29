package com.finebi.cube.engine.map;


import com.fr.bi.stable.structure.collection.list.IntList;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * Created by FineSoft on 2015/7/16.
 */
public class LongIntListExternalMap extends IntListExternalMap<Long> {
    public LongIntListExternalMap(Comparator comparator,String dataFolder) {
        super(comparator,dataFolder);
    }
    public LongIntListExternalMap(Long bufferSize, Comparator comparator,String dataFolder) {
        super(bufferSize, comparator,dataFolder);
    }
    @Override
    public ExternalMapIO<Long, IntList> getExternalMapIO(String id_filePath) {
        return (ExternalMapIO)new LongIntListExternalMapIO(id_filePath);
    }

    @Override
    public ExternalMapIO<Long, IntList> getMemMapIO(TreeMap<Long, IntList> currentContainer) {
        return new StringMemMapIO(currentContainer);
    }

    private class StringMemMapIO extends MemExternalMapIO<Long> {
        public StringMemMapIO(TreeMap<Long, IntList> currentContainer) {
            super(currentContainer);
        }
    }
}