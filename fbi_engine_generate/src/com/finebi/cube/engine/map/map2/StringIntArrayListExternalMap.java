package com.finebi.cube.engine.map.map2;

import com.finebi.cube.engine.map.ExternalMapIO;
import com.fr.stable.collections.array.IntArray;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * Created by wang on 2016/9/2.
 */
public class StringIntArrayListExternalMap extends IntArrayListExternalMap<String>{
    public StringIntArrayListExternalMap(Comparator comparator, String dataFolderAbsPath) {
        super(comparator, dataFolderAbsPath);
    }

    public StringIntArrayListExternalMap(long bufferSize, Comparator comparator, String dataFolderAbsPath) {
        super(bufferSize, comparator, dataFolderAbsPath);
    }

    @Override
    public ExternalMapIO getExternalMapIO(String id_filePath) {
        return (ExternalMapIO) new StringIntArrayListMapIO(id_filePath);
    }

    public ExternalMapIO<String, IntArray> getMemMapIO(TreeMap<String, IntArray> currentContainer) {
        return new StringMemMapIO(currentContainer);
    }

    private class StringMemMapIO extends MemIntArrayExternalMapIO<String> {
        public StringMemMapIO(TreeMap<String, IntArray> currentContainer) {
            super(currentContainer);
        }
    }
}
