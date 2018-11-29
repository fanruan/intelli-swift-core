package com.fr.swift.structure.external.map.intpairs;

//import com.fr.swift.segment.column.DictionaryEncodedColumn;

import com.fr.swift.structure.IntPair;
import com.fr.swift.structure.external.map.ExternalMap;
import com.fr.swift.structure.external.map.ExternalMapIO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author anchore
 * @date 2018/1/4
 * <p>
 * 用在外排全局序号的map
 * 字典值 -> (segment块号, 字典值在该块的字典序号)
 * // * @see DictionaryEncodedColumn#putGlobalIndex(int, int)
 * <p>
 * ps：你也可以用在别处，符合K-V格式即可
 */
abstract class BaseIntPairsExtMap<K> extends ExternalMap<K, List<IntPair>> {
    BaseIntPairsExtMap(Comparator<K> comparator, String dataFolderAbsPath) {
        super(comparator, dataFolderAbsPath);
    }

    BaseIntPairsExtMap(Comparator<K> comparator) {
        super(comparator);
    }

    BaseIntPairsExtMap(long containerSize, Comparator<K> comparator, String diskContainerPath) {
        super(containerSize, comparator, diskContainerPath);
    }

    BaseIntPairsExtMap(long containerSize, Comparator<K> comparator, String diskContainerPath, boolean isKeepDiskFile) {
        super(containerSize, comparator, diskContainerPath, isKeepDiskFile);
    }

    @Override
    public List<IntPair> combineValue(TreeMap<Integer, List<IntPair>> lists) {
        List<IntPair> result = new ArrayList<IntPair>();
        Iterator<Map.Entry<Integer, List<IntPair>>> itr = lists.entrySet().iterator();
        while (itr.hasNext()) {
            List<IntPair> temp = itr.next().getValue();
            result.addAll(temp);
            itr.remove();
        }
        return result;
    }

    @Override
    public ExternalMapIO<K, List<IntPair>> getMemMapIO(TreeMap<K, List<IntPair>> currentContainer) {
        return new MemIntPairsMapIo<K>(currentContainer);
    }
}
