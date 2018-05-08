package com.fr.swift.structure.external.map.intlist;

import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.external.map.ExternalMapIO;

import java.util.Comparator;

/**
 * Created by wang on 2016/9/2.
 */
class LongIntListExternalMap extends BaseIntListExternalMap<Long> {
    public LongIntListExternalMap(Comparator comparator, String dataFolderAbsPath) {
        super(comparator, dataFolderAbsPath);
    }

    public LongIntListExternalMap(long bufferSize, Comparator comparator, String dataFolderAbsPath) {
        super(bufferSize, comparator, dataFolderAbsPath);
    }

    @Override
    public ExternalMapIO<Long, IntList> getExternalMapIO(String idFilePath) {
        return new LongIntListMapIO(idFilePath);
    }
}