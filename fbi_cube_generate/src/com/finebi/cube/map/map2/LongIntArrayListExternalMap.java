package com.finebi.cube.map.map2;

import com.finebi.cube.map.ExternalMapIO;
import com.fr.stable.collections.array.IntArray;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * Created by wang on 2016/9/2.
 */
public class LongIntArrayListExternalMap extends IntArrayListExternalMap<Long>{
    public LongIntArrayListExternalMap(Comparator comparator, String dataFolderAbsPath) {
        super(comparator, dataFolderAbsPath);
    }

    public LongIntArrayListExternalMap(long bufferSize, Comparator comparator, String dataFolderAbsPath) {
        super(bufferSize, comparator, dataFolderAbsPath);
    }

    @Override
    public ExternalMapIO getExternalMapIO(String id_filePath) {
        return (ExternalMapIO) new LongIntArrayListMapIO(id_filePath);
    }

    public ExternalMapIO<Long, IntArray> getMemMapIO(TreeMap<Long, IntArray> currentContainer) {
        return new StringMemMapIO(currentContainer);
    }

    private class StringMemMapIO extends MemIntArrayExternalMapIO<Long> {
        public StringMemMapIO(TreeMap<Long, IntArray> currentContainer) {
            super(currentContainer);
        }
    }
}
