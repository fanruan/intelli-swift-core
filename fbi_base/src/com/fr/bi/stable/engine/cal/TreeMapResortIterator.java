package com.fr.bi.stable.engine.cal;

import com.finebi.cube.api.ICubeValueEntryGetter;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.stable.collections.array.IntArray;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by 小灰灰 on 2017/3/28.
 */
public class TreeMapResortIterator implements DimensionIterator{
    private ICubeValueEntryGetter getter;
    //保存分组序号与行号的map
    private Map<Integer, IntArray> indexArrayMap;
    private Iterator<Map.Entry<Integer, IntArray>> iterator;
    private int lastGroup;

    protected TreeMapResortIterator(ICubeValueEntryGetter getter, Map<Integer, IntArray> indexArrayMap) {
        this.getter = getter;
        this.indexArrayMap = indexArrayMap;
        this.iterator = indexArrayMap.entrySet().iterator();
    }

    @Override
    public void remove() {
        iterator.remove();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Map.Entry<Object, GroupValueIndex> next() {
        final Map.Entry<Integer, IntArray> fEntry = iterator.next();
        lastGroup = fEntry.getKey();
        return new Map.Entry<Object, GroupValueIndex>() {
            @Override
            public Object getKey() {
                return getter.getGroupValue(fEntry.getKey());
            }

            @Override
            public GroupValueIndex getValue() {
                return GVIFactory.createGroupValueIndexBySimpleIndex(fEntry.getValue());
            }

            @Override
            public GroupValueIndex setValue(GroupValueIndex value) {
                return null;
            }

            @Override
            public boolean equals(Object o) {
                return false;
            }

            @Override
            public int hashCode() {
                return 0;
            }
        };
    }
    @Override
    public int getCurrentGroup() {
        return lastGroup;
    }

    //这个能regain，但是这种情况一般几乎不占内存，就不要释放了，返回false吧
    @Override
    public boolean canReGainGroupValueIndex(){
        return false;
    }

    @Override
    public boolean isReturnFinalGroupValueIndex() {
        return true;
    }

    @Override
    public GroupValueIndex getGroupValueIndexByGroupIndex(int groupIndex) {
        return GVIFactory.createGroupValueIndexBySimpleIndex(indexArrayMap.get(groupIndex));
    }
}
