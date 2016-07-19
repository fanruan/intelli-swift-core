package com.fr.bi.stable.gvi;

import com.fr.bi.stable.constant.CubeConstant;
import com.fr.bi.stable.structure.collection.list.IntList;

/**
 * Created by GUY on 2015/3/11.
 */
public class GVIFactory {

    public static GroupValueIndex createAllShowIndexGVI(int rowCount) {
        return RoaringGroupValueIndex.createAllShowGroupValueIndex(rowCount);
    }

    public static GroupValueIndex createAllEmptyIndexGVI() {
        return RoaringGroupValueIndex.createAllEmptyGroupValueIndex();
    }

    /**
     * 根据简单索引创建分组值索引
     *
     * @param size     大小
     * @param intMpp   索引map
     * @param rowCount 行数
     * @return 索引map
     */
    public static GroupValueIndex[] createGroupVauleIndexesBySimpleIndex(int size, IntList intMpp, long rowCount) {
    	return createSmallGroupGroupValueIndexBySimpleIndex(size, intMpp, rowCount);
    }

    /**
     * 根据简单索引创建分组值索引
     *
     * @param rowCount 行数
     */
    public static GroupValueIndex createGroupValueIndexBySimpleIndex(IntList list) {
        if(list.size() == 0){
        	return RoaringGroupValueIndex.createAllEmptyGroupValueIndex();
        }
    	return RoaringGroupValueIndex.createGroupValueIndex(list.toArray());
    }

    private static GroupValueIndex[] createSmallGroupGroupValueIndexBySimpleIndex(int size, IntList intMpp, long rowCount) {
        GroupValueIndex[] group_indexs = createByValues(size);
        for (int i = 0; i < rowCount; i++) {
            int v =  intMpp.get(i);
            if (v != CubeConstant.NULLINDEX) {
            	group_indexs[v].addValueByIndex(i);
            }
        }
        return group_indexs;
    }

    /**
     * 根据值创建索引
     *
     * @param size     大小
     * @param rowCount 行数
     * @return 索引
     */
    private static GroupValueIndex[] createByValues(int size) {
        GroupValueIndex[] group_indexs = new GroupValueIndex[size];
        for (int i = 0; i < size; i++) {
            group_indexs[i] = new RoaringGroupValueIndex();
        }
        return group_indexs;
    }
}