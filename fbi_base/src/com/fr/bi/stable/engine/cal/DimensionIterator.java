package com.fr.bi.stable.engine.cal;

import com.fr.bi.stable.gvi.GroupValueIndex;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by 小灰灰 on 2017/3/24.
 */
public interface DimensionIterator extends Iterator<Map.Entry<Object, GroupValueIndex>> {
    /**
     * 获取当前的groupIndex
     * @return
     */
    int getCurrentGroup();

    /**
     * 有木有办法能根据行号重新获取索引，能获取的索引会释放掉。
     * 如果不需要释放索引的，直接返回false也行
     * @return
     */
    boolean canReGainGroupValueIndex();

    /**
     * 根据分组序号来取索引
     * @param groupIndex
     * @return
     */
    GroupValueIndex getGroupValueIndexByGroupIndex(int groupIndex);
}
