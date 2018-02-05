package com.fr.swift.structure.external.map.intlist.map2;

import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.external.map.ExternalMapIO;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * Created by wang on 2016/9/2.
 */
public class IntegerIntArrayListExternalMap extends IntArrayListExternalMap<Integer> {
    public IntegerIntArrayListExternalMap(Comparator comparator, String dataFolderAbsPath) {
        super(comparator, dataFolderAbsPath);
    }

    public IntegerIntArrayListExternalMap(long bufferSize, Comparator comparator, String dataFolderAbsPath) {
        super(bufferSize, comparator, dataFolderAbsPath);
    }

    @Override
    public ExternalMapIO getExternalMapIO(String id_filePath) {
        return new IntegerIntArrayListMapIO(id_filePath);
    }

    public ExternalMapIO<Integer, IntList> getMemMapIO(TreeMap<Integer, IntList> currentContainer) {
        return new StringMemMapIO(currentContainer);
    }

    private class StringMemMapIO extends MemIntArrayExternalMapIO<Integer> {
        public StringMemMapIO(TreeMap<Integer, IntList> currentContainer) {
            super(currentContainer);
        }
    }
}
