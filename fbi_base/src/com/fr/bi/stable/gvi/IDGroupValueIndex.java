package com.fr.bi.stable.gvi;

import com.fr.bi.stable.gvi.traversal.BrokenTraversalAction;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;
import com.fr.bi.stable.gvi.traversal.TraversalAction;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by 小灰灰 on 2016/10/13.
 */
public class IDGroupValueIndex extends AbstractGroupValueIndex {
    protected int id;
    protected IDGroupValueIndex(){

    }

    protected IDGroupValueIndex(int i) {
        this.id = i;
    }

    @Override
    public GroupValueIndex AND(GroupValueIndex valueIndex) {
        if (valueIndex == null || GVIUtils.isAllShowRoaringGroupValueIndex(valueIndex)) {
            return this.clone();
        }
        if (GVIUtils.isAllEmptyRoaringGroupValueIndex(valueIndex)) {
            return GVIFactory.createAllEmptyIndexGVI();
        }
        GroupValueIndex result = valueIndex.clone();
        if (GVIUtils.isIDGroupValueIndex(valueIndex)){
            return ((IDGroupValueIndex)valueIndex).id == id ? result : GVIFactory.createAllEmptyIndexGVI();
        } else {
            ((RoaringGroupValueIndex)result).removeValueByIndex(id);
            return result;
        }
    }

    @Override
    public GroupValueIndex OR(GroupValueIndex valueIndex) {
        if (valueIndex == null || GVIUtils.isAllEmptyRoaringGroupValueIndex(valueIndex)) {
            return this.clone();
        }
        if (GVIUtils.isAllShowRoaringGroupValueIndex(valueIndex)) {
            return valueIndex.clone();
        }
        if (GVIUtils.isIDGroupValueIndex(valueIndex)){
            if (id == ((IDGroupValueIndex)valueIndex).id){
                return this.clone();
            }
            GroupValueIndex gvi = toRoaringGroupValueIndex();
            gvi.addValueByIndex(((IDGroupValueIndex)valueIndex).id);
            return gvi;
        } else {
            GroupValueIndex gvi = valueIndex.clone();
            gvi.addValueByIndex(id);
            return gvi;
        }
    }

    @Override
    public GroupValueIndex ANDNOT(GroupValueIndex index) {
        if (index == null) {
            return this.clone();
        }
        return index.isOneAt(id) ? GVIFactory.createAllEmptyIndexGVI() : this.clone();
    }

    @Override
    public GroupValueIndex NOT(int rowCount) {
        return toRoaringGroupValueIndex().NOT(rowCount);
    }

    @Override
    public void addValueByIndex(int index) {
    }

    @Override
    public boolean isAllEmpty() {
        return false;
    }

    @Override
    public void Traversal(TraversalAction action) {
        action.actionPerformed(new int[]{id});
    }

    @Override
    public void Traversal(SingleRowTraversalAction action) {
        action.actionPerformed(id);
    }

    @Override
    public boolean BrokenableTraversal(BrokenTraversalAction action) {
        if(action.actionPerformed(id)){
            return true;
        }
        return false;
    }

    @Override
    public boolean isOneAt(int rowIndex) {
        return id == rowIndex;
    }

    @Override
    public int getRowsCountWithData() {
        return 1;
    }

    @Override
    public boolean hasSameValue(GroupValueIndex parentIndex) {
        if (GVIUtils.isAllShowRoaringGroupValueIndex(parentIndex)){
            return true;
        }
        return parentIndex.isOneAt(id);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(id);
    }

    //pony 这个调用太频繁，不要通过stream初始化，直接判断下第一个byte走createGroupValueIndex
    @Deprecated
    @Override
    public void readFields(DataInput in) throws IOException {
        id = in.readInt();
    }

    @Override
    public GroupValueIndex or(GroupValueIndex index) {
        if (index == null || GVIUtils.isAllEmptyRoaringGroupValueIndex(index)) {
            return this;
        }
        if (GVIUtils.isAllShowRoaringGroupValueIndex(index)){
            return index;
        }
        if (GVIUtils.isIDGroupValueIndex(index)){
            if (id == ((IDGroupValueIndex)index).id){
                return this;
            }
            GroupValueIndex gvi = toRoaringGroupValueIndex();
            gvi.addValueByIndex(((IDGroupValueIndex)index).id);
            return gvi;
        } else {
            GroupValueIndex gvi = index.clone();
            gvi.addValueByIndex(id);
            return gvi;
        }
    }

    @Override
    public GroupValueIndex and(GroupValueIndex index) {
        if (index == null) {
            return this;
        }
        if (!index.isOneAt(id)){
            return GVIFactory.createAllEmptyIndexGVI();
        }
        return this;
    }

    @Override
    public GroupValueIndex andnot(GroupValueIndex index) {
        if (index == null) {
            return this;
        }
        if (index.isOneAt(id)){
            return GVIFactory.createAllEmptyIndexGVI();
        }
        return this;
    }

    @Override
    protected byte getType() {
        return GroupValueIndexCreator.ROARING_INDEX_ID.getType();
    }

    protected RoaringGroupValueIndex toRoaringGroupValueIndex(){
        RoaringGroupValueIndex gvi = new RoaringGroupValueIndex();
        gvi.addValueByIndex(id);
        return gvi;
    }

    @Override
    public IDGroupValueIndex clone() {
        return new IDGroupValueIndex(id);
    }

    protected static GroupValueIndex createGroupValueIndex(byte[] b) {
        return new IDGroupValueIndex((((b[1]) << 24) |
                ((b[2] & 0xff) << 16) |
                ((b[3] & 0xff) <<  8) |
                ((b[4] & 0xff)      )));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        IDGroupValueIndex that = (IDGroupValueIndex) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
