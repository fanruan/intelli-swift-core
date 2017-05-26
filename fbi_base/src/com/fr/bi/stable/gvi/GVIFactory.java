package com.fr.bi.stable.gvi;

import com.fr.bi.stable.structure.array.IntList;
import com.fr.stable.collections.array.IntArray;

/**
 * Created by GUY on 2015/3/11.
 */
public class GVIFactory {

    public static GroupValueIndex createAllShowIndexGVI(int rowCount) {
        return new AllShowRoaringGroupValueIndex(rowCount);
    }

    public static GroupValueIndex createAllEmptyIndexGVI() {
        return new RoaringGroupValueIndex();
    }

    /**
     * 根据简单索引创建分组值索引
     *@param list 数据
     */
    public static GroupValueIndex createGroupValueIndexBySimpleIndex(IntList list) {
        if (list.size() == 1){
            return new IDGroupValueIndex(list.get(0));
        }
        if(list.size() == 0){
            return createAllEmptyIndexGVI();
        }
        return RoaringGroupValueIndex.createGroupValueIndex(list);
    }

    /**
     * 根据简单索引创建分组值索引
     *@param list 数据
     */
    public static GroupValueIndex createGroupValueIndexBySimpleIndex(IntArray list) {
        if (list.size == 1){
            return new IDGroupValueIndex(list.get(0));
        }
        if(list.size == 0){
            return createAllEmptyIndexGVI();
        }
        return RoaringGroupValueIndex.createGroupValueIndex(list.toArray());
    }

    public static GroupValueIndex createGroupValueIndexBySimpleIndex(int... list) {
        if (list.length == 1){
            return new IDGroupValueIndex(list[0]);
        }
        if(list.length == 0){
            return createAllEmptyIndexGVI();
        }
        return RoaringGroupValueIndex.createGroupValueIndex(list);
    }

    public static GroupValueIndex createGroupValueIndexByDataInput(BIByteDataInput bytes) {
        return GroupValueIndexCreator.createGroupValueIndex(bytes);
    }
}