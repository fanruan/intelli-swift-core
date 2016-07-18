package com.fr.bi.stable.utils;

import com.finebi.cube.api.ICubeDataLoader;
import com.fr.base.TableData;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.engine.cal.*;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.data.DataModel;
import com.fr.script.Calculator;

/**
 * Created by GUY on 2015/3/11.
 */
public class BIServerUtils {

    public static long runServer(TableData data, ICubeFieldSource[] columns, Traversal<BIDataValue> back) {
        DataModel dm = null;
        long rowCount = 0;
        try {
            long start = System.currentTimeMillis();
            dm = data.createDataModel(Calculator.createCalculator());
            rowCount = dm.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                for (int j = 0; j < columns.length; j++) {
                    if (!columns[j].isUsable()) {
                        continue;
                    }
                    Object v = dm.getValueAt(i, j);
                    if (back != null) {
                        back.actionPerformed(new BIDataValue(i, j, v));
                    }
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            try {
                dm.release();
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }
        return rowCount;
    }
    
	/**
	 * 根据参数创建一个链表对象用于递归运算 最后是 FilterIndexCalculator终结
	 * @param lastDealer 
	 * @return
	 */
	public static ResultDealer createDimensonDealer(BIKey[] dimension, ResultDealer lastDealer) {
		DimensionCalculatorDealer dealer = null;
		DimensionCalculatorDealer tempDealer = null;
        if(dimension != null) {
            for (BIKey key : dimension) {
                DimensionCalculatorDealer dimensionDealer = new DimensionCalculatorDealer(key);
                if (dealer == null) {
                    dealer = dimensionDealer;
                }
                if (tempDealer != null) {
                    tempDealer.setNext(dimensionDealer);
                }
                tempDealer = dimensionDealer;
            }
        }
		if(dealer != null){
			tempDealer.setNext(lastDealer);
			return dealer;
		} else {
			return lastDealer;
		}
	}

    /**
     * 根据参数创建一个链表对象用于递归运算 最后是 FilterIndexCalculator终结 带排序
     * @param lastDealer
     * @return
     */
    public static NodeResultDealer createAllCalDimensonDealer(DimensionCalculator[] dcs, NodeResultDealer lastDealer, boolean[] dimensionSortIsAsc, ICubeDataLoader loader) {
        SortDimensionCalculatorDealer dealer = null;
        SortDimensionCalculatorDealer tempDealer = null;
        if(dcs != null && dimensionSortIsAsc != null && dcs.length == dimensionSortIsAsc.length) {
            for (int i = 0; i < dcs.length; i++) {
                DimensionCalculator dc = dcs[i];
                boolean asc = dimensionSortIsAsc[i];
                SortDimensionCalculatorDealer dimensionDealer = new SortDimensionCalculatorDealer(dc, asc, loader.getTableIndex(dc.getField().getTableBelongTo().getTableSource()));
                if (dealer == null) {
                    dealer = dimensionDealer;
                }
                if (tempDealer != null) {
                    tempDealer.setNext(dimensionDealer);
                }
                tempDealer = dimensionDealer;
            }
        }
        if(dealer != null){
            tempDealer.setNext(lastDealer);
            return dealer;
        } else {
            return lastDealer;
        }
    }

    public static NodeResultDealer createAllCalDimensonDealer(DimensionCalculator[] dcs, NodeResultDealer lastDealer, ICubeDataLoader loader) {
        NoneSortDimensionCalculatorDealer dealer = null;
        NoneSortDimensionCalculatorDealer tempDealer = null;
        if(dcs != null) {
            for (int i = 0; i < dcs.length; i++) {
                DimensionCalculator dc = dcs[i];
                NoneSortDimensionCalculatorDealer dimensionDealer = new NoneSortDimensionCalculatorDealer(dc, loader.getTableIndex(dc.getField().getTableBelongTo().getTableSource()));
                if (dealer == null) {
                    dealer = dimensionDealer;
                }
                if (tempDealer != null) {
                    tempDealer.setNext(dimensionDealer);
                }
                tempDealer = dimensionDealer;
            }
        }
        if(dealer != null){
            tempDealer.setNext(lastDealer);
            return dealer;
        } else {
            return lastDealer;
        }
    }
}