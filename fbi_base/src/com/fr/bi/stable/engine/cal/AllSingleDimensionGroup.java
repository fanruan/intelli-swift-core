/**
 * 
 */
package com.fr.bi.stable.engine.cal;

import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.BrokenTraversalAction;
import com.fr.bi.stable.structure.CubeValueEntryNode;
import com.fr.bi.stable.structure.object.CubeValueEntry;
import com.fr.bi.stable.structure.object.CubeValueEntrySort;

import java.util.Iterator;

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
	public static void run(GroupValueIndex parentIndex, final ICubeTableService ti, final BIKey key, final ResultDealer deal){
		GroupValueIndex currentIndex = parentIndex.clone();
		final FinalAdapter<GroupValueIndex> adapter = new FinalAdapter<GroupValueIndex>(currentIndex);
		while(!adapter.get().isAllEmpty()){
			adapter.get().BrokenableTraversal(new BrokenTraversalAction() {
				
				@Override
				public boolean actionPerformed(int row) {
					GroupValueIndex gvi = ti.getIndexByRow(key, row);
					GroupValueIndex currentIndex = adapter.get().AND(gvi);
					if(deal != null){
						deal.dealWith(ti, currentIndex);
					}
					adapter.set(adapter.get().andnot(currentIndex));
					return true;
				}
			});
		}
	}
	
	public static void runWithSort(GroupValueIndex parentIndex, final ICubeTableService ti, final BIKey key, final NodeResultDealer deal, final CubeValueEntryNode snParent, boolean asc){
		GroupValueIndex currentIndex = parentIndex.clone();
		final FinalAdapter<GroupValueIndex> adapter = new FinalAdapter<GroupValueIndex>(currentIndex);
		final CubeValueEntrySort.CubeValueEntrySortBuilder sortBuilder = CubeValueEntrySort.getBuilder(ti.loadGroup(key).sizeOfGroup());
		while(!adapter.get().isAllEmpty()){
			adapter.get().BrokenableTraversal(new BrokenTraversalAction() {
				
				@Override
				public boolean actionPerformed(int row) {
					CubeValueEntryNode entryNote = CubeValueEntryNode.fromParent(ti.getEntryByRow(key, row));
					sortBuilder.putSortItem(entryNote);
					GroupValueIndex currentIndex = adapter.get().AND(entryNote.getGvi());
					if(deal != null){
						deal.dealWithNode(ti, currentIndex, entryNote);
					}
					adapter.set(adapter.get().andnot(currentIndex));
					return true;
				}
			});
		}
		CubeValueEntrySort sort = sortBuilder.build();
		if(asc) {
			snParent.setChildList((CubeValueEntryNode[]) sort.getSortedASC());
		}
		else {
			snParent.setChildList((CubeValueEntryNode[]) sort.getSortedDESC());
		}
	}

}