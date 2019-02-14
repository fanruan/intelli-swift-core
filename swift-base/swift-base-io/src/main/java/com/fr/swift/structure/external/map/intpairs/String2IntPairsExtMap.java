package com.fr.swift.structure.external.map.intpairs;

import com.fr.swift.structure.IntPair;
import com.fr.swift.structure.external.map.ExternalMapIO;

import java.util.Comparator;
import java.util.List;

/**
 * @author anchore
 * @date 2018/1/5
 */
class String2IntPairsExtMap extends BaseIntPairsExtMap<String> {
    public String2IntPairsExtMap(Comparator<String> comparator, String dataFolderAbsPath) {
        super(comparator, dataFolderAbsPath);
    }

    public String2IntPairsExtMap(Comparator<String> comparator) {
        super(comparator);
    }

    public String2IntPairsExtMap(long containerSize, Comparator<String> comparator, String diskContainerPath) {
        super(containerSize, comparator, diskContainerPath);
    }

    public String2IntPairsExtMap(long containerSize, Comparator<String> comparator, String diskContainerPath, boolean isKeepDiskFile) {
        super(containerSize, comparator, diskContainerPath, isKeepDiskFile);
    }

    @Override
    public ExternalMapIO<String, List<IntPair>> getExternalMapIO(String id_filePath) {
        return new String2IntPairsExtMapIo(id_filePath);
    }
}
