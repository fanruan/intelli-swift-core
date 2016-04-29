/**
 *
 */
package com.finebi.cube.calculator.bidouble;

import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.engine.cal.ResultDealer;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;

/**
 * @author Daniel
 */
public class CalResultDealer implements ResultDealer {

    private CubeDoubleDataCalculator cal;
    private BIKey key;
    private Traversal<BIDataValue> travel;

    public CalResultDealer(BIKey key, CubeDoubleDataCalculator cal, Traversal<BIDataValue> travel) {
        this.key = key;
        this.cal = cal;
        this.travel = travel;
    }

    @Override
    public void dealWith(ICubeTableService tableGetterService, GroupValueIndex gvi, final int startCol) {
        final Double v = cal.calculate(tableGetterService, key, gvi);
        gvi.Traversal(new SingleRowTraversalAction() {
            @Override
            public void actionPerformed(int row) {
                travel.actionPerformed(new BIDataValue(row, startCol, v));
            }
        });
    }

}