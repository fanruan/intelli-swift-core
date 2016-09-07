package com.finebi.cube.engine.map.map2;

import com.finebi.cube.engine.map.ExternalMapIO;
import com.fr.bi.stable.structure.collection.list.IntArrayList;

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

    public ExternalMapIO<Long, IntArrayList> getMemMapIO(TreeMap<Long, IntArrayList> currentContainer) {
        return new StringMemMapIO(currentContainer);
    }

    private class StringMemMapIO extends MemIntArrayExternalMapIO<Long> {
        public StringMemMapIO(TreeMap<Long, IntArrayList> currentContainer) {
            super(currentContainer);
        }
    }
}
