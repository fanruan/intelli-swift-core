package com.fr.swift.structure.external.map.intlist.map2;

import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.external.map.ExternalMapIO;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * Created by wang on 2016/9/2.
 */
public class LongIntArrayListExternalMap extends IntArrayListExternalMap<Long> {
    public LongIntArrayListExternalMap(Comparator comparator, String dataFolderAbsPath) {
        super(comparator, dataFolderAbsPath);
    }

    public LongIntArrayListExternalMap(long bufferSize, Comparator comparator, String dataFolderAbsPath) {
        super(bufferSize, comparator, dataFolderAbsPath);
    }

    @Override
    public ExternalMapIO getExternalMapIO(String id_filePath) {
        return new LongIntArrayListMapIO(id_filePath);
    }

    public ExternalMapIO<Long, IntList> getMemMapIO(TreeMap<Long, IntList> currentContainer) {
        return new StringMemMapIO(currentContainer);
    }

    private class StringMemMapIO extends MemIntArrayExternalMapIO<Long> {
        public StringMemMapIO(TreeMap<Long, IntList> currentContainer) {
            super(currentContainer);
        }
    }
}
