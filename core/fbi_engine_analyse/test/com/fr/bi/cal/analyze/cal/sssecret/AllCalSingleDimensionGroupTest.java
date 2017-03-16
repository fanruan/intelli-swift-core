package com.fr.bi.cal.analyze.cal.sssecret;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.cal.analyze.cal.result.AllCalNode;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.RoaringGroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.chart.chartglyph.Object3D;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by loy on 16/7/13.
 */
public class AllCalSingleDimensionGroupTest extends TestCase {

    AllCalSingleDimensionGroup allCal = null;
    AllCalSingleDimensionGroup allCalChild = null;
    AllCalNode root;

    AllCalNode child1;
    AllCalNode child11;
    AllCalNode child12;
    AllCalNode child2;
    AllCalNode child21;
    AllCalNode child22;

    @Before
    public void setUp() throws Exception {
        GroupValueIndex rootGvi = createGvi(0, 1, 2, 3);

        root = new AllCalNode(null, null);
        root.setGroupValueIndex(rootGvi);

        child1 = new AllCalNode(null, "江苏");
        child1.setGroupValueIndex(createGvi(0, 2));
        child11 = new AllCalNode(null, "南京");
        child11.setGroupValueIndex(createGvi(0));
        child1.addChild(child11);
        child12 = new AllCalNode(null, "无锡");
        child12.setGroupValueIndex(createGvi(2));
        child1.addChild(child12);
        root.addChild(child1);

        child2 = new AllCalNode(null, "浙江");
        child2.setGroupValueIndex(createGvi(1, 3));
        child21 = new AllCalNode(null, "杭州");
        child21.setGroupValueIndex(createGvi(1));
        child2.addChild(child21);
        child22 = new AllCalNode(null, "温州");
        child22.setGroupValueIndex(createGvi(3));
        child2.addChild(child22);
        root.addChild(child2);

        allCal = new AllCalSingleDimensionGroup(root);
        allCalChild = AllCalSingleDimensionGroup.createInstanceWithCache(allCal, createGvi(1, 3), 1);
    }

    private GroupValueIndex createGvi(int ... indexs){
        RoaringGroupValueIndex gvi = new RoaringGroupValueIndex();
        for (int i : indexs){
            gvi.addValueByIndex(i);
        }
        return gvi;
    }

    @Test
    public void testGetChildIndexByValue() throws Exception {
        int index = allCal.getChildIndexByValue(child1);
        assertEquals(index, 0);

        int index2 = allCalChild.getChildIndexByValue(child21);
        assertEquals(index2, 0);
    }

    @Test
    public void testGetChildDimensionGroup() throws Exception {
        NoneDimensionGroup ng = allCal.getChildDimensionGroup(1);
        assertTrue(ng.getRoot().getGroupValueIndex().equals(child2.getGroupValueIndex()));

        NoneDimensionGroup ng2 = allCalChild.getChildDimensionGroup(1);
        assertTrue(ng2.getRoot().getGroupValueIndex().equals(child22.getGroupValueIndex()));
    }

    @Test
    public void testGetChildData() throws Exception {
        Object o = allCal.getChildData(0);
        assertEquals(o, "江苏");

        Object o2 = allCalChild.getChildData(0);
        assertEquals(o2, "杭州");
    }

    @Test
    public void testGetChildShowName() throws Exception {
        String name = allCal.getChildShowName(1);
        assertEquals(name, "浙江");

        String name2 = allCalChild.getChildShowName(1);
        assertEquals(name2, "温州");
    }

    @Test
    public void testGetChildNode() throws Exception {
        Node n = allCal.getChildNode(0);
        assertEquals(n, child1);

        Node n2 = allCalChild.getChildNode(0);
        assertEquals(n2, child21);
    }

    @Test
    public void testGetRoot() throws Exception {
        assertEquals(root, allCal.getRoot());

        assertEquals(child2, allCalChild.getRoot());
    }

    @Test
    public void testGetCurrentTotalRow() throws Exception {
        int c = allCal.getCurrentTotalRow();
        assertEquals(c, 2);

        int c2 = allCalChild.getCurrentTotalRow();
        assertEquals(c2, 2);
    }

}