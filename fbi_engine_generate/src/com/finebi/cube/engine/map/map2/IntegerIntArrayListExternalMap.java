package com.finebi.cube.engine.map.map2;

import com.finebi.cube.engine.map.ExternalMapIO;
import com.fr.bi.stable.structure.collection.list.IntArrayList;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * Created by wang on 2016/9/2.
 */
public class IntegerIntArrayListExternalMap extends IntArrayListExternalMap<Integer>{
    public IntegerIntArrayListExternalMap(Comparator comparator, String dataFolderAbsPath) {
        super(comparator, dataFolderAbsPath);
    }

    public IntegerIntArrayListExternalMap(long bufferSize, Comparator comparator, String dataFolderAbsPath) {
        super(bufferSize, comparator, dataFolderAbsPath);
    }

    @Override
    public ExternalMapIO getExternalMapIO(String id_filePath) {
        return (ExternalMapIO) new IntegerIntArrayListMapIO(id_filePath);
    }

    public ExternalMapIO<Integer, IntArrayList> getMemMapIO(TreeMap<Integer, IntArrayList> currentContainer) {
        return new StringMemMapIO(currentContainer);
    }

    private class StringMemMapIO extends MemIntArrayExternalMapIO<Integer> {
        public StringMemMapIO(TreeMap<Integer, IntArrayList> currentContainer) {
            super(currentContainer);
        }
    }
}
