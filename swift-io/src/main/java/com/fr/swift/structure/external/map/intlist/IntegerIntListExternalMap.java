package com.fr.swift.structure.external.map.intlist;

import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.external.map.ExternalMapIO;

import java.util.Comparator;

/**
 * Created by wang on 2016/9/2.
 */
class IntegerIntListExternalMap extends BaseIntListExternalMap<Integer> {
    public IntegerIntListExternalMap(Comparator comparator, String dataFolderAbsPath) {
        super(comparator, dataFolderAbsPath);
    }

    public IntegerIntListExternalMap(long bufferSize, Comparator comparator, String dataFolderAbsPath) {
        super(bufferSize, comparator, dataFolderAbsPath);
    }

    @Override
    public ExternalMapIO<Integer, IntList> getExternalMapIO(String idFilePath) {
        return new IntegerIntListMapIO(idFilePath);
    }
}