/**
 *
 */
package com.fr.bi.test.etloperatordealer;

import com.finebi.cube.api.ICubeColumnDetailGetter;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.data.source.operator.add.rowcal.correspondperiod.CorrespondPeriodResultDealer;
import com.fr.bi.stable.data.db.BIDataValue;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import java.lang.reflect.Constructor;

/**
 * @author Daniel
 */
public class testCorresponperiod extends TestCase {

    public void testCorrespon() {
        corresponperiodTest(2000, 1500);
        corresponperiodTest(2000, 1500);
        corresponperiodTest(1000, 1000);
        corresponperiodTest(100, 80);
        corresponperiodTest(3000, 280);
        corresponperiodTest(3000, 2800);
        corresponperiodTest(30, 28);
    }

    private void corresponperiodTest(int rowCount, int group) {
        Double[] groups = new Double[group];
        for (int i = 0; i < group; i++) {
            groups[i] = Math.random() * rowCount;
            if (i == rowCount / 2) {
                groups[i] = null;
            }
        }
        final Double[] values = new Double[rowCount];
        Integer[] groupColumns = new Integer[rowCount];
        int start = 0;
        for (int i = 0; i < rowCount; i++) {
            int plus = (int) (Math.random() * 4);
            start = start + plus + 1;
            groupColumns[i] = start;
            if (i == rowCount / 2) {
                groupColumns[i] = null;
            }
        }

        for (int i = 0; i < rowCount; i++) {
            values[i] = groups[(int) (Math.random() * group)];
        }
        final Double[] result = new Double[rowCount];
        for (int i = 0; i < rowCount; i++) {
            if (groupColumns[i] == null) {
                continue;
            }
            int v = groupColumns[i] - 1;
            for (int k = i; k > 0 && groupColumns[k] != null && groupColumns[k] >= v; k--) {
                if (groupColumns[k - 1] != null && groupColumns[k - 1] == v) {
                    result[i] = values[k - 1];
                    break;
                }
            }
        }

        methodWrapper(rowCount, values, groupColumns, result);

    }

    private void methodWrapper(int rowCount, final Double[] values, final Integer[] groupColumns, final Double[] result) {
        IMocksControl control = EasyMock.createControl();
        ICubeTableService ti = control.createMock(ICubeTableService.class);
        ti.getRowCount();
        EasyMock.expectLastCall().andReturn(rowCount).anyTimes();
        IndexKey key1 = new IndexKey("a");
        IndexKey key2 = new IndexKey("b");
        ti.getColumnDetailReader(key1);
        EasyMock.expectLastCall().andReturn(new ICubeColumnDetailGetter(){

            @Override
            public Object getValue(int row) {
                return values[row];
            }
        }).anyTimes();
        ti.getColumnDetailReader(key2);
        EasyMock.expectLastCall().andReturn(new ICubeColumnDetailGetter(){

            @Override
            public Object getValue(int row) {
                return groupColumns[row];
            }
        }).anyTimes();
        control.replay();

        Traversal<BIDataValue> t = new Traversal<BIDataValue>() {
            int i = 0;

            @Override
            public void actionPerformed(BIDataValue data) {
                assertEquals(result[i++], (Double) data.getValue());
            }
        };

        Constructor<CorrespondPeriodResultDealer> con;
        CorrespondPeriodResultDealer dealer = null;
        GroupValueIndex gvi = GVIFactory.createAllShowIndexGVI(rowCount);
        try {
            con = CorrespondPeriodResultDealer.class.getDeclaredConstructor(BIKey.class, Traversal.class, BIKey.class);
            con.setAccessible(true);
            dealer = con.newInstance(key1, t, key2);
            dealer.dealWith(ti, gvi, 0);
        } catch (Exception ignore) {

        }
    }


}