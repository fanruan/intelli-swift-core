package com.fr.swift.structure.external.map.intpairs;

import com.fr.swift.structure.IntPair;
import com.fr.swift.structure.external.map.ExternalMapIO;

import java.util.Comparator;
import java.util.List;

/**
 * @author anchore
 * @date 2018/1/5
 */
class Double2IntPairsExtMap extends BaseIntPairsExtMap<Double> {
    public Double2IntPairsExtMap(Comparator<Double> comparator, String dataFolderAbsPath) {
        super(comparator, dataFolderAbsPath);
    }

    public Double2IntPairsExtMap(Comparator<Double> comparator) {
        super(comparator);
    }

    public Double2IntPairsExtMap(long containerSize, Comparator<Double> comparator, String diskContainerPath) {
        super(containerSize, comparator, diskContainerPath);
    }

    public Double2IntPairsExtMap(long containerSize, Comparator<Double> comparator, String diskContainerPath, boolean isKeepDiskFile) {
        super(containerSize, comparator, diskContainerPath, isKeepDiskFile);
    }

    @Override
    public ExternalMapIO<Double, List<IntPair>> getExternalMapIO(String id_filePath) {
        return new Double2IntPairsExtMapIo(id_filePath);
    }
}
