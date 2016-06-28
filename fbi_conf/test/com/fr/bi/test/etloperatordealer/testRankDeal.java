/**
 *
 */
package com.fr.bi.test.etloperatordealer;

import com.finebi.cube.api.ICubeColumnDetailGetter;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.data.source.operator.add.rowcal.rank.RankDealer;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.operation.sort.comp.ComparatorFacotry;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Daniel
 */
public class testRankDeal extends TestCase {

    public void testRank() {
        int asc = BIReportConstant.TARGET_TYPE.CAL_VALUE.RANK_TPYE.ASC;
        int desc = BIReportConstant.TARGET_TYPE.CAL_VALUE.RANK_TPYE.DESC;
        rankTest(asc, 2000, 1500);
        rankTest(desc, 2000, 1500);
        rankTest(desc, 1000, 1000);
        rankTest(asc, 100, 80);
    }

    private void rankTest(int type, int rowCount, int group) {
        Comparator<Double> c = null;
        if (type == BIReportConstant.TARGET_TYPE.CAL_VALUE.RANK_TPYE.DESC) {
            c = ComparatorFacotry.DOUBLE_DESC;
        } else {
            c = ComparatorFacotry.DOUBLE_ASC;
        }
        Double[] groups = new Double[group];
        for (int i = 0; i < group; i++) {
            groups[i] = Math.random() * rowCount;
            if (i == rowCount / 2) {
                groups[i] = null;
            }
        }
        final Double[] values = new Double[rowCount];
        for (int i = 0; i < rowCount; i++) {
            values[i] = groups[(int) (Math.random() * group)];
        }
        Double[] sorted = values.clone();
        Arrays.sort(sorted, c);
        int rank = 1;
        Map<Double, Integer> resultMap = new HashMap<Double, Integer>();
        final Map<Double, Integer> acceptResultMap = new HashMap<Double, Integer>();
        Double v = sorted[0];
        for (int i = 0; i < rowCount; i++) {
            if (v != sorted[i]) {
                rank = i + 1;
            }
            resultMap.put(sorted[i], rank);
            v = sorted[i];
        }

        methodWrapper(type, rowCount, values, resultMap, acceptResultMap);
    }

    private void methodWrapper(int type, int rowCount, final Double[] values, Map<Double, Integer> resultMap, final Map<Double, Integer> acceptResultMap) {
        IMocksControl control = EasyMock.createControl();
        ICubeTableService ti = control.createMock(ICubeTableService.class);
        ti.getRowCount();
        EasyMock.expectLastCall().andReturn(rowCount).anyTimes();
        ti.getColumnDetailReader(null);
        EasyMock.expectLastCall().andReturn(new ICubeColumnDetailGetter(){

            @Override
            public Object getValue(int row) {
                return values[row];
            }
        }).anyTimes();
        control.replay();

        Traversal<BIDataValue> t = new Traversal<BIDataValue>() {
            int i = 0;

            @Override
            public void actionPerformed(BIDataValue data) {
                acceptResultMap.put(values[i++], (Integer) data.getValue());
            }
        };

        Constructor<RankDealer> con;
        RankDealer dealer = null;
        GroupValueIndex gvi = GVIFactory.createAllShowIndexGVI(rowCount);
        try {
            con = RankDealer.class.getDeclaredConstructor(BIKey.class, int.class, Traversal.class);
            con.setAccessible(true);
            dealer = con.newInstance(null, type, t);
            dealer.dealWith(ti, gvi);
            assertEquals(resultMap, acceptResultMap);
        } catch (Exception e) {
            assertTrue(false);
        }
    }


}