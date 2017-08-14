package com.fr.bi.cal.analyze.cal.sssecret;

/**
 * Created by 小灰灰 on 2017/7/31.
 */
public class EmptySingleDimensionGroup implements ISingleDimensionGroup {

    private Object[] data;

    int deep;

    public EmptySingleDimensionGroup(Object[] data, int deep) {

        this.data = data;
        this.deep = deep;
    }

    @Override
    public int getChildIndexByValue(Object value) {

        return 0;
    }

    @Override
    public NoneDimensionGroup getChildDimensionGroup(int row) {

        if (row == 0 && deep == 0) {
            return NoneDimensionGroup.EMPTY;
        } else if (row == 0) {
            // BI-8175 如果不是第一维度，这是说明没有子节点（主表的维度分组在子表下面没有对应的分组），这时不能简单的返回EMPTY
            //
            return NoneDimensionGroup.NONECHILD;
        }
        return NoneDimensionGroup.NULL;
    }

    @Override
    public Object getChildData(int row) {

        return null;
    }

    @Override
    public String getChildShowName(int row) {

        return null;
    }

    @Override
    public void release() {

    }

    @Override
    public Object[] getData() {

        return data;
    }
}
