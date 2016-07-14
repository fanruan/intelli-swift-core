/**
 *
 */
package com.fr.bi.stable.engine.cal;

import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.api.ICubeValueEntryGetter;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.BrokenTraversalAction;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.structure.CubeValueEntryNode;
import com.fr.bi.stable.structure.object.CubeValueEntry;
import com.fr.bi.stable.structure.object.CubeValueEntrySort;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Daniel
 *         全部计算的
 */
public class AllSingleDimensionGroup {

    private static class FinalAdapter<T> {
        T t;

        FinalAdapter(T t) {
            set(t);
        }

        T get() {
            return t;
        }

        void set(T t) {
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
        final ICubeValueEntryGetter getter = ti.getValueEntryGetter(key, new ArrayList<BITableSourceRelation>());
		while(!adapter.get().isAllEmpty()){
			adapter.get().BrokenableTraversal(new BrokenTraversalAction() {
				
				@Override
				public boolean actionPerformed(int row) {
					GroupValueIndex gvi = getter.getIndexByRow(row);
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

    public static Set getAllValue(GroupValueIndex parentIndex, final ICubeValueEntryGetter getter){
        final Set set = new HashSet();
        final FinalAdapter<GroupValueIndex> adapter = new FinalAdapter<GroupValueIndex>(parentIndex.clone());
        while(!adapter.get().isAllEmpty()){
            adapter.get().BrokenableTraversal(new BrokenTraversalAction() {
                @Override
                public boolean actionPerformed(int row) {
                    CubeValueEntry entry = getter.getEntryByRow(row);
                    GroupValueIndex currentIndex = adapter.get().AND(entry.getGvi());
                    set.add(entry.getT());
                    adapter.set(adapter.get().andnot(currentIndex));
                    return true;
                }
            });
        }
        return set;
    }

	public static void run(GroupValueIndex parentIndex, final ICubeTableService ti, final DimensionCalculator dc, final NodeResultDealer deal, final CubeValueEntryNode snParent){
		GroupValueIndex currentIndex = parentIndex.clone();
		final FinalAdapter<GroupValueIndex> adapter = new FinalAdapter<GroupValueIndex>(currentIndex);
        final BIKey key = dc.createKey();
        final ICubeValueEntryGetter getter = ti.getValueEntryGetter(key, dc.getRelationList());
		final CubeValueEntryNode[] children = new CubeValueEntryNode[getter.getGroupSize()];
		final FinalAdapter<Integer> indexadp = new FinalAdapter<Integer>(0);
		while(!adapter.get().isAllEmpty()){
			adapter.get().BrokenableTraversal(new BrokenTraversalAction() {

				@Override
				public boolean actionPerformed(int row) {
					CubeValueEntryNode entryNote = CubeValueEntryNode.fromParent(getter.getEntryByRow(row));
					GroupValueIndex currentIndex = adapter.get().AND(entryNote.getGvi());
					entryNote.setGvi(currentIndex);
					children[indexadp.get()] = entryNote;
					indexadp.set(indexadp.get() + 1);
					if(deal != null  && !currentIndex.isAllEmpty()){
						deal.dealWithNode(currentIndex, entryNote);
					}
					adapter.set(adapter.get().andnot(currentIndex));
					return true;
				}
			});
		}
		CubeValueEntryNode[] childrenWithoutNull = new CubeValueEntryNode[indexadp.get()];
		System.arraycopy(children, 0, childrenWithoutNull, 0, childrenWithoutNull.length);
		snParent.setChildren(childrenWithoutNull);
	}
	
	public static void runWithSort(GroupValueIndex parentIndex, final ICubeTableService ti, final DimensionCalculator dc, final NodeResultDealer deal, final CubeValueEntryNode snParent, boolean asc){
		GroupValueIndex currentIndex = parentIndex.clone();
		final FinalAdapter<GroupValueIndex> adapter = new FinalAdapter<GroupValueIndex>(currentIndex);
        final BIKey key = dc.createKey();
        final ICubeValueEntryGetter getter = ti.getValueEntryGetter(key, dc.getRelationList());
		final CubeValueEntrySort.CubeValueEntrySortBuilder sortBuilder = CubeValueEntrySort.getBuilder(ti.loadGroup(key).sizeOfGroup());
		while(!adapter.get().isAllEmpty()){
			adapter.get().BrokenableTraversal(new BrokenTraversalAction() {
				
				@Override
				public boolean actionPerformed(int row) {
					CubeValueEntryNode entryNote = CubeValueEntryNode.fromParent(getter.getEntryByRow(row));
					GroupValueIndex currentIndex = adapter.get().AND(entryNote.getGvi());
					entryNote.setGvi(currentIndex);
					sortBuilder.putSortItem(entryNote);
					if(deal != null && !currentIndex.isAllEmpty()){
						deal.dealWithNode(currentIndex, entryNote);
					}
					adapter.set(adapter.get().andnot(currentIndex));
					return true;
				}
			});
		}
		CubeValueEntrySort sort = sortBuilder.build();
		if(asc) {
			snParent.setChildren(sort.getSortedASC());
		}
		else {
			snParent.setChildren(sort.getSortedDESC());
		}
	}

}