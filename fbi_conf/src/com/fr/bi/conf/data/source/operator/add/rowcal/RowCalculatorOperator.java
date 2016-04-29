/**
 * 
 */
package com.fr.bi.conf.data.source.operator.add.rowcal;

import com.fr.bi.base.BICore;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.data.source.operator.add.AbstractAddColumnOperator;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.engine.cal.ResultDealer;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.utils.BIServerUtils;

/**
 * @author Daniel
 *
 */
public abstract class RowCalculatorOperator extends AbstractAddColumnOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1094437940677911376L;
    public static final String XML_TAG = "RowCalculatorOperator";
	@BICoreField
    protected BIKey[] dimension;
	@BICoreField
    protected BIKey key;
    
    protected RowCalculatorOperator(){
    	super();
    	columnType = DBConstant.COLUMN.NUMBER;
    }


	@Override
	public BICore fetchObjectCore() {
		return null;
	}
	
	@Override
    protected int write(Traversal<BIDataValue> travel, ICubeTableService ti, int startCol) {
		 int rowCount = ti.getRowCount();
		 ResultDealer dealer = createResultDealer(travel);
		 ResultDealer dimensionDealer = BIServerUtils.createDimensonDealer(this.dimension, dealer);
		 dimensionDealer.dealWith(ti, ti.getAllShowIndex(), startCol);
		 return rowCount;
	}


	/**
	 * @return
	 */
	protected abstract ResultDealer createResultDealer(Traversal<BIDataValue> travel);

}