package com.fr.swift.structure.external.map.intpairs;

import com.fr.swift.structure.IntPair;
import com.fr.swift.structure.external.map.ExternalMapIO;

import java.util.Comparator;
import java.util.List;

/**
 * @author anchore
 * @date 2018/1/5
 */
class Long2IntPairsExtMap extends BaseIntPairsExtMap<Long> {
    public Long2IntPairsExtMap(Comparator<Long> comparator, String dataFolderAbsPath) {
        super(comparator, dataFolderAbsPath);
    }

    public Long2IntPairsExtMap(Comparator<Long> comparator) {
        super(comparator);
    }

    public Long2IntPairsExtMap(long containerSize, Comparator<Long> comparator, String diskContainerPath) {
        super(containerSize, comparator, diskContainerPath);
    }

    public Long2IntPairsExtMap(long containerSize, Comparator<Long> comparator, String diskContainerPath, boolean isKeepDiskFile) {
        super(containerSize, comparator, diskContainerPath, isKeepDiskFile);
    }

    @Override
    public ExternalMapIO<Long, List<IntPair>> getExternalMapIO(String id_filePath) {
        return new Long2IntPairsExtMapIo(id_filePath);
    }
}
