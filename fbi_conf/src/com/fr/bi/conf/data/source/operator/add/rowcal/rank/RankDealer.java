/**
 * 
 */
package com.fr.bi.conf.data.source.operator.add.rowcal.rank;

import com.finebi.cube.api.ICubeColumnDetailGetter;
import com.fr.bi.base.FinalInt;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.engine.cal.ResultDealer;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;
import com.fr.bi.stable.operation.sort.comp.ComparatorFacotry;
import com.fr.bi.stable.operation.sort.comp.IComparator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * @author Daniel
 *
 */
public class RankDealer implements ResultDealer {
	
	private BIKey key;
	private int type;
	
	private Traversal<BIDataValue> travel;
	
	RankDealer(BIKey key, int type, Traversal<BIDataValue> travel){
		this.key = key;
		this.type = type;
		this.travel = travel;
	}

	@Override
	public void dealWith(ICubeTableService ti, GroupValueIndex currentIndex, final int startCol) {
		TreeMap<Number, FinalInt> tree = createSortedTree(ti, currentIndex);
		HashMap<Number, Integer> map = buildrankMap(tree); 
		writeValue(ti, currentIndex, map, startCol);
	}

	
	/**
	 * @param ti
	 * @param currentIndex
	 * @param map
	 */
	private void writeValue(final ICubeTableService ti, GroupValueIndex currentIndex, final HashMap<Number, Integer> map, final int startCol) {
        final ICubeColumnDetailGetter getter = ti.getColumnDetailReader(key);
		currentIndex.Traversal(new SingleRowTraversalAction() {
			@Override
			public void actionPerformed(int row) {
				Number v = (Number) getter.getValue(row);
				int rank = map.get(v);
				travel.actionPerformed(new BIDataValue(row, startCol, rank));
			}	
		});
	}


	/**
	 * @param tree
	 * @return
	 */
	private HashMap<Number, Integer> buildrankMap(TreeMap<Number, FinalInt> tree) {
		int rank = 1;
		HashMap<Number, Integer> rankMap = new HashMap<Number, Integer>();
		Iterator<Entry<Number, FinalInt>> iter = tree.entrySet().iterator();
		while(iter.hasNext()){
			Entry<Number, FinalInt> entry = iter.next();
			rankMap.put(entry.getKey(), rank);
			rank += entry.getValue().value;
		}
		return rankMap;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private TreeMap<Number, FinalInt> createSortedTree(final ICubeTableService ti, GroupValueIndex currentIndex){
		IComparator comparator = null; 
		if(type == BIReportConstant.TARGET_TYPE.CAL_VALUE.RANK_TPYE.ASC){
			comparator = ComparatorFacotry.DOUBLE_ASC;
		} else {
			comparator = ComparatorFacotry.DOUBLE_DESC;
		}
		final TreeMap<Number, FinalInt> tree = new TreeMap<Number, FinalInt>(comparator);
        final ICubeColumnDetailGetter getter = ti.getColumnDetailReader(key);
        currentIndex.Traversal(new SingleRowTraversalAction() {
			@Override
			public void actionPerformed(int row) {
				Number v = (Number) getter.getValue(row);
				FinalInt count = tree.get(v);
				if(count == null){
					count = new FinalInt();
					tree.put(v, count);
				}
				count.value++;
			}
		});
		return tree;
	}
}