package com.finebi.cube.engine.map;


import com.fr.bi.stable.structure.collection.list.IntList;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * Created by FineSoft on 2015/7/9.
 */
public class StringIntListExternalMap extends IntListExternalMap<String> {
    public StringIntListExternalMap(Comparator comparator, String dataFolderAbsPath) {
        super(comparator, dataFolderAbsPath);
    }

    public StringIntListExternalMap(Long bufferSize, Comparator comparator, String dataFolder) {
        super(bufferSize, comparator, dataFolder);
    }

    @Override
    public ExternalMapIO<String, IntList> getExternalMapIO(String id_filePath) {
        return (ExternalMapIO) new StringIntListExternalMapIO(id_filePath);
    }

    @Override
    public ExternalMapIO<String, IntList> getMemMapIO(TreeMap<String, IntList> currentContainer) {
        return new StringMemMapIO(currentContainer);
    }

    private class StringMemMapIO extends MemExternalMapIO<String> {
        public StringMemMapIO(TreeMap<String, IntList> currentContainer) {
            super(currentContainer);
        }
    }
}