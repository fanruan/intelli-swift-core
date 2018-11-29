package com.fr.swift.structure.external.map.intlist;

import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.external.map.ExternalMapIO;

import java.util.Comparator;

/**
 * Created by wang on 2016/9/2.
 */
class StringIntListExternalMap extends BaseIntListExternalMap<String> {
    public StringIntListExternalMap(Comparator comparator, String dataFolderAbsPath) {
        super(comparator, dataFolderAbsPath);
    }

    public StringIntListExternalMap(long bufferSize, Comparator comparator, String dataFolderAbsPath) {
        super(bufferSize, comparator, dataFolderAbsPath);
    }

    public StringIntListExternalMap(long bufferSize, Comparator comparator, String dataFolderAbsPath, boolean isKeepDiskFile) {
        super(bufferSize, comparator, dataFolderAbsPath, isKeepDiskFile);
    }

    @Override
    public ExternalMapIO<String, IntList> getExternalMapIO(String idFilePath) {
        return new StringIntListMapIO(idFilePath);
    }
}