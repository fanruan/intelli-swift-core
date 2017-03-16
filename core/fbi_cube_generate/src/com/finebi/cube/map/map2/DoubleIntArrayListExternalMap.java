package com.finebi.cube.map.map2;

import com.finebi.cube.map.ExternalMapIO;
import com.fr.stable.collections.array.IntArray;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * Created by wang on 2016/9/2.
 */
public class DoubleIntArrayListExternalMap extends IntArrayListExternalMap<Double>{
    public DoubleIntArrayListExternalMap(Comparator comparator, String dataFolderAbsPath) {
        super(comparator, dataFolderAbsPath);
    }

    public DoubleIntArrayListExternalMap(long bufferSize, Comparator comparator, String dataFolderAbsPath) {
        super(bufferSize, comparator, dataFolderAbsPath);
    }

    @Override
    public ExternalMapIO getExternalMapIO(String id_filePath) {
        return (ExternalMapIO) new DoubleIntArrayListMapIO(id_filePath);
    }

    public ExternalMapIO<Double, IntArray> getMemMapIO(TreeMap<Double, IntArray> currentContainer) {
        return new StringMemMapIO(currentContainer);
    }

    private class StringMemMapIO extends MemIntArrayExternalMapIO<Double> {
        public StringMemMapIO(TreeMap<Double, IntArray> currentContainer) {
            super(currentContainer);
        }
    }
}
