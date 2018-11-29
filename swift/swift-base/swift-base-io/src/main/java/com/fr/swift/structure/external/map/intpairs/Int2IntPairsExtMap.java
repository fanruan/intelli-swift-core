package com.fr.swift.structure.external.map.intpairs;

import com.fr.swift.structure.IntPair;
import com.fr.swift.structure.external.map.ExternalMapIO;

import java.util.Comparator;
import java.util.List;

/**
 * @author anchore
 * @date 2018/1/5
 */
class Int2IntPairsExtMap extends BaseIntPairsExtMap<Integer> {
    public Int2IntPairsExtMap(Comparator<Integer> comparator, String dataFolderAbsPath) {
        super(comparator, dataFolderAbsPath);
    }

    public Int2IntPairsExtMap(Comparator<Integer> comparator) {
        super(comparator);
    }

    public Int2IntPairsExtMap(long containerSize, Comparator<Integer> comparator, String diskContainerPath) {
        super(containerSize, comparator, diskContainerPath);
    }

    public Int2IntPairsExtMap(long containerSize, Comparator<Integer> comparator, String diskContainerPath, boolean isKeepDiskFile) {
        super(containerSize, comparator, diskContainerPath, isKeepDiskFile);
    }

    @Override
    public ExternalMapIO<Integer, List<IntPair>> getExternalMapIO(String id_filePath) {
        return new Int2IntPairsExtMapIo(id_filePath);
    }
}
