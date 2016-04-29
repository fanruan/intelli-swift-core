/**
 * 
 */
package com.fr.bi.stable.engine.cal;

import com.fr.bi.base.key.BIKey;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.BrokenTraversalAction;

/**
 * @author Daniel
 * 全部计算的
 */
public class AllSingleDimensionGroup {
	
	private static class FinalAdapter<T> {
		T t;
		
		FinalAdapter(T t){
			set(t);
		}
		
		T get(){
			return t;
		}
		
		void set(T t){
			this.t = t;
		}
	}

	/**
	 * 通过遍历index获取index里面分组的所有值的index，此种情况下取值是无序的
	 * @param parentIndex
	 * @param ti
	 * @param key
	 * @param deal
	 */
	public static void run(GroupValueIndex parentIndex, final ICubeTableService ti, final BIKey key, final ResultDealer deal, final int startCol){
		GroupValueIndex currentIndex = parentIndex.clone();
		final FinalAdapter<GroupValueIndex> adapter = new FinalAdapter<GroupValueIndex>(currentIndex);
		while(!adapter.get().isAllEmpty()){
			adapter.get().BrokenableTraversal(new BrokenTraversalAction() {
				
				@Override
				public boolean actionPerformed(int row) {
					GroupValueIndex gvi = ti.getIndexByRow(key, row);
					GroupValueIndex currentIndex = adapter.get().AND(gvi);
					if(deal != null){
						deal.dealWith(ti, currentIndex, startCol);
					}
					adapter.set(adapter.get().andnot(currentIndex));
					return true;
				}
			});
		}
	}
	
	public static void runWithSort(GroupValueIndex parentIndex, final ICubeTableService ti, final BIKey key, final ResultDealer deal, final int startCol){
		GroupValueIndex currentIndex = parentIndex.clone();
		final FinalAdapter<GroupValueIndex> adapter = new FinalAdapter<GroupValueIndex>(currentIndex);
		while(!adapter.get().isAllEmpty()){
			adapter.get().BrokenableTraversal(new BrokenTraversalAction() {
				
				@Override
				public boolean actionPerformed(int row) {
					GroupValueIndex gvi = ti.getIndexByRow(key, row);
					GroupValueIndex currentIndex = adapter.get().AND(gvi);
					if(deal != null){
						deal.dealWith(ti, currentIndex, startCol);
					}
					adapter.set(adapter.get().andnot(currentIndex));
					return true;
				}
			});
		}
	}

}