package com.finebi.cube.structure.column;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.tools.BICubeResourceLocationTestTool;
import com.fr.bi.common.factory.BIFactoryHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2016/4/8.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeDoubleColumnTest extends BICubeColumnBasicTest<Double> {
    @Override
    public ICubeColumnEntityService<Double> getTestTarget() {
        return new BICubeDoubleColumn(BIFactoryHelper.getObject(ICubeResourceDiscovery.class),BICubeResourceLocationTestTool.getBasic("testDoubleCubeColumn"));
    }

    @Override
    public List<Double> getListData() {
        List<Double> lists = new ArrayList<Double>();
        lists.add(Double.valueOf(3));
        lists.add(Double.valueOf(2));
        lists.add(Double.valueOf(5));
        lists.add(Double.valueOf(1));
        lists.add(Double.valueOf(4));
        lists.add(Double.valueOf(8));
        lists.add(Double.valueOf(7));
        return lists;
    }

    @Override
    public void checkCubeColumnGroupSort(ICubeColumnEntityService<Double> column) {
        assertEquals(column.getGroupValue(0), Double.valueOf(1));
        assertEquals(column.getGroupValue(1), Double.valueOf(2));
        assertEquals(column.getGroupValue(2), Double.valueOf(3));
    }
}
