/**
 *
 */
package com.fr.bi.test.etloperatordealer;

import com.fr.bi.base.key.BIKey;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.data.source.operator.add.rowcal.accumulate.AccumulateResultDealer;
import com.fr.bi.stable.data.db.BIDataValue;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import java.lang.reflect.Constructor;

/**
 * @author Daniel
 */
public class testAccmulateDeal extends TestCase {

    public void testAccmulate() {
        accmulateTest(2000, 1500);
        accmulateTest(2000, 1500);
        accmulateTest(1000, 1000);
        accmulateTest(100, 80);
    }

    private void accmulateTest(int rowCount, int group) {
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
        final Double[] result = new Double[rowCount];
        double v = 0;
        for (int i = 0; i < rowCount; i++) {
            if (values[i] != null) {
                v += values[i];
            }
            result[i] = v;
        }
        IMocksControl control = EasyMock.createControl();
        ICubeTableService ti = control.createMock(ICubeTableService.class);
        ti.getRowCount();
        EasyMock.expectLastCall().andReturn(rowCount).anyTimes();
        for (int i = 0; i < rowCount; i++) {
            ti.getRow(null, i);
            EasyMock.expectLastCall().andReturn(values[i]).anyTimes();
        }
        control.replay();
        Traversal<BIDataValue> t = new Traversal<BIDataValue>() {
            int i = 0;

            @Override
            public void actionPerformed(BIDataValue data) {
                assertEquals(result[i++], (Double) data.getValue());
            }
        };
        Constructor<AccumulateResultDealer> con;
        AccumulateResultDealer dealer = null;
        GroupValueIndex gvi = GVIFactory.createAllShowIndexGVI(rowCount);
        try {
            con = AccumulateResultDealer.class.getDeclaredConstructor(BIKey.class, Traversal.class);
            con.setAccessible(true);
            dealer = con.newInstance(null, t);
            dealer.dealWith(ti, gvi, 0);
        } catch (Exception ignore) {

        }

    }


}