package com.fr.swift.structure.external.map.intlist.map2;

import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.external.map.ExternalMapIO;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * Created by wang on 2016/9/2.
 */
public class StringIntArrayListExternalMap extends IntArrayListExternalMap<String> {
    public StringIntArrayListExternalMap(Comparator comparator, String dataFolderAbsPath) {
        super(comparator, dataFolderAbsPath);
    }

    public StringIntArrayListExternalMap(long bufferSize, Comparator comparator, String dataFolderAbsPath) {
        super(bufferSize, comparator, dataFolderAbsPath);
    }

    public StringIntArrayListExternalMap(long bufferSize, Comparator comparator, String dataFolderAbsPath, boolean isKeepDiskFile) {
        super(bufferSize, comparator, dataFolderAbsPath, isKeepDiskFile);
    }

    @Override
    public ExternalMapIO getExternalMapIO(String id_filePath) {
        return new StringIntArrayListMapIO(id_filePath);
    }

    public ExternalMapIO<String, IntList> getMemMapIO(TreeMap<String, IntList> currentContainer) {
        return new StringMemMapIO(currentContainer);
    }

    private class StringMemMapIO extends MemIntArrayExternalMapIO<String> {
        public StringMemMapIO(TreeMap<String, IntList> currentContainer) {
            super(currentContainer);
        }
    }
}
