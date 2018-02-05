package com.fr.swift.structure.external.map.intlist.map2;

import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.external.map.ExternalMapIO;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * Created by wang on 2016/9/2.
 */
public class DoubleIntArrayListExternalMap extends IntArrayListExternalMap<Double> {
    public DoubleIntArrayListExternalMap(Comparator comparator, String dataFolderAbsPath) {
        super(comparator, dataFolderAbsPath);
    }

    public DoubleIntArrayListExternalMap(long bufferSize, Comparator comparator, String dataFolderAbsPath) {
        super(bufferSize, comparator, dataFolderAbsPath);
    }

    @Override
    public ExternalMapIO getExternalMapIO(String id_filePath) {
        return new DoubleIntArrayListMapIO(id_filePath);
    }

    public ExternalMapIO<Double, IntList> getMemMapIO(TreeMap<Double, IntList> currentContainer) {
        return new StringMemMapIO(currentContainer);
    }

    private class StringMemMapIO extends MemIntArrayExternalMapIO<Double> {
        public StringMemMapIO(TreeMap<Double, IntList> currentContainer) {
            super(currentContainer);
        }
    }
}
