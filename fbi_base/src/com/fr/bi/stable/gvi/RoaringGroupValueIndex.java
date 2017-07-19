package com.fr.bi.stable.gvi;

import com.fr.bi.stable.gvi.roaringbitmap.IntConsumer;
import com.fr.bi.stable.gvi.roaringbitmap.IntIterator;
import com.fr.bi.stable.gvi.roaringbitmap.RoaringBitmap;
import com.fr.bi.stable.gvi.traversal.BrokenTraversalAction;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;
import com.fr.bi.stable.gvi.traversal.TraversalAction;
import com.fr.bi.stable.structure.array.IntList;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by Hiram on 2015/6/19.
 */
public class RoaringGroupValueIndex extends AbstractGroupValueIndex {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6076084903420996622L;
	private RoaringBitmap bitmap = new RoaringBitmap();

	
	public RoaringGroupValueIndex(RoaringBitmap bitmap){
		this.bitmap = bitmap;
	}
	@Override
	public GroupValueIndex AND(GroupValueIndex valueIndex) {
		if (valueIndex == null || GVIUtils.isAllShowRoaringGroupValueIndex(valueIndex)) {
			return this.clone();
		}
		if (GVIUtils.isAllEmptyRoaringGroupValueIndex(valueIndex)) {
			return GVIFactory.createAllEmptyIndexGVI();
		}
		if (GVIUtils.isIDGroupValueIndex(valueIndex)){
			if (this.isOneAt(((IDGroupValueIndex)valueIndex).id)){
				return valueIndex.clone();
			}
			return GVIFactory.createAllEmptyIndexGVI();
		} else {
			RoaringBitmap map = RoaringBitmap.and(bitmap, (((RoaringGroupValueIndex)valueIndex).bitmap));
			return new RoaringGroupValueIndex(map);
		}
	}

	public RoaringGroupValueIndex() {
	}

	@Override
	public RoaringGroupValueIndex clone() {
		RoaringGroupValueIndex ret = new RoaringGroupValueIndex();
		ret.bitmap = bitmap.clone();
		return ret;
	}

	@Override
	public GroupValueIndex ANDNOT(GroupValueIndex valueIndex) {
		if (valueIndex == null || GVIUtils.isAllEmptyRoaringGroupValueIndex(valueIndex)) {
			return this.clone();
		}
		if (GVIUtils.isAllShowRoaringGroupValueIndex(valueIndex)) {
			return GVIFactory.createAllEmptyIndexGVI();
		}
		if (GVIUtils.isIDGroupValueIndex(valueIndex)){
			RoaringGroupValueIndex gvi = this.clone();
			gvi.removeValueByIndex(((IDGroupValueIndex)valueIndex).id);
			return gvi;
		} else {
			return new RoaringGroupValueIndex(RoaringBitmap.andNot(bitmap, ((RoaringGroupValueIndex) valueIndex).bitmap));
		}
	}
	@Override
	public GroupValueIndex andnot(GroupValueIndex index) {
		if(index == null || GVIUtils.isAllEmptyRoaringGroupValueIndex(index)){
			return this;
		}
		if(GVIUtils.isAllShowRoaringGroupValueIndex(index)){
			bitmap.clear();
		}
		if (GVIUtils.isIDGroupValueIndex(index)){
			this.removeValueByIndex(((IDGroupValueIndex)index).id);
		} else {
			bitmap.andNot(((RoaringGroupValueIndex) index).bitmap);
		} 
		return this;
	}

	@Override
	public GroupValueIndex OR(GroupValueIndex valueIndex) {
		if (valueIndex == null || GVIUtils.isAllEmptyRoaringGroupValueIndex(valueIndex)) {
			return this.clone();
		}
		if (GVIUtils.isAllShowRoaringGroupValueIndex(valueIndex)) {
			return valueIndex.clone();
		}
		RoaringGroupValueIndex ret = clone();
		if (GVIUtils.isIDGroupValueIndex(valueIndex)){
			ret.addValueByIndex(((IDGroupValueIndex)valueIndex).id);
		} else {
			ret.bitmap.or((((RoaringGroupValueIndex) valueIndex).bitmap));
		}
		return ret;
	}

	//改变自身and
	@Override
	public GroupValueIndex and(GroupValueIndex valueIndex) {
		if (valueIndex == null || GVIUtils.isAllShowRoaringGroupValueIndex(valueIndex)) {
			return this;
		}
		if (GVIUtils.isAllEmptyRoaringGroupValueIndex(valueIndex)){
			bitmap.clear();
		} else if (GVIUtils.isIDGroupValueIndex(valueIndex)){
			int id = ((IDGroupValueIndex)valueIndex).id;
			if (this.isOneAt(id)){
				bitmap.clear();
				bitmap.add(id);
			} else {
				bitmap.clear();
			}
		}else {
			bitmap.and((((RoaringGroupValueIndex) valueIndex).bitmap));
		}
		return this;
	}

	//改变自身的or //FIXME如何预防valueIndex不是allshow呢
	@Override
	public GroupValueIndex or(GroupValueIndex valueIndex) {
		if (valueIndex == null  || GVIUtils.isAllEmptyRoaringGroupValueIndex(valueIndex)) {
			return this;
		}
		if(GVIUtils.isAllShowRoaringGroupValueIndex(valueIndex)){
			return valueIndex;
		}
		if (GVIUtils.isIDGroupValueIndex(valueIndex)){
			this.addValueByIndex(((IDGroupValueIndex)valueIndex).id);
		} else {
			bitmap.or((((RoaringGroupValueIndex) valueIndex).bitmap));
		}
		return this;
	}

	@Override
	public GroupValueIndex NOT(int rowCount) {
		RoaringGroupValueIndex ret = clone();
		ret.bitmap.flip(0l, rowCount);
		return ret;
	}

	@Override
	public void addValueByIndex(int row) {
		bitmap.add(row);
	}

	protected void removeValueByIndex(int row) {
		bitmap.remove(row);
	}

	@Override
	public boolean isAllEmpty() {
//		if(DebugUtils.isIsAllEmpty()){
//			return false;
//		}
		return bitmap.isEmpty();
	}

	@Override
	public void Traversal(TraversalAction action) {
		action.actionPerformed(bitmap.toArray());
	}

	@Override
	//TODOiteartor 需要改成非iterator 模板
	public void Traversal(final SingleRowTraversalAction action) {
        bitmap.forEach(new IntConsumer() {
			@Override
			public void accept(int i) {
				action.actionPerformed(i);
			}
		});
	}

	@Override
	public boolean BrokenableTraversal(BrokenTraversalAction action) {
		IntIterator iterator = bitmap.getIntIterator();
		while (iterator.hasNext()) {
			if(action.actionPerformed(iterator.next())){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isOneAt(int rowIndex) {
		return bitmap.contains(rowIndex);
	}

	@Override
	public int getRowsCountWithData() {
		return bitmap.getCardinality();
	}

	@Override
	public boolean hasSameValue(GroupValueIndex parentIndex) {
		return !(AND(parentIndex).isAllEmpty());
	}

	@Override
	public void write(DataOutput out) throws IOException {
		bitmap.serialize(out);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		bitmap.deserialize(in);
	}

	public static RoaringGroupValueIndex createGroupValueIndex(IntList intList) {
		RoaringGroupValueIndex roaringGroupValueIndex = new RoaringGroupValueIndex();
		for (int i = 0; i < intList.size(); i++){
			roaringGroupValueIndex.bitmap.add(intList.get(i));
		}
		return roaringGroupValueIndex;
	}

	public static RoaringGroupValueIndex createGroupValueIndex(int[] rowIndexs) {
		RoaringGroupValueIndex roaringGroupValueIndex = new RoaringGroupValueIndex();
		roaringGroupValueIndex.bitmap = RoaringBitmap.bitmapOf(rowIndexs);
		return roaringGroupValueIndex;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		RoaringGroupValueIndex that = (RoaringGroupValueIndex) o;
		if (bitmap != null ? !bitmap.equals(that.bitmap) : that.bitmap != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = bitmap != null ? bitmap.hashCode() : 0;
		return result;
	}

	@Override
	protected byte getType() {
		return GroupValueIndexCreator.ROARING_INDEX.getType();
	}

	protected RoaringBitmap getBitMap() {
		return bitmap;
	}
}