package com.fr.bi.cal.analyze.cal.sssecret;

import com.fr.stable.StringUtils;

/**
 * Created by 小灰灰 on 2017/7/31.
 */
public class EmptySingleDimensionGroup implements ISingleDimensionGroup {

    private Object[] data;

    public EmptySingleDimensionGroup(Object[] data) {

        this.data = data;
    }

    @Override
    public int getChildIndexByValue(Object value) {

        return 0;
    }

    @Override
    public NoneDimensionGroup getChildDimensionGroup(int row) {

        if (row == 0) {
            return NoneDimensionGroup.EMPTY;
        }
        return NoneDimensionGroup.NULL;
    }

    @Override
    public Object getChildData(int row) {

        return null;
    }

    @Override
    public String getChildShowName(int row) {
        return StringUtils.EMPTY;
    }

    @Override
    public void release() {

    }

    @Override
    public Object[] getData() {

        return data;
    }
}
