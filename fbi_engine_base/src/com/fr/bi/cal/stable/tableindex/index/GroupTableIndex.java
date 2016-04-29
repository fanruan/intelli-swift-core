package com.fr.bi.cal.stable.tableindex.index;

import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.engine.index.BITableCubeFile;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.array.ICubeTableIndexReader;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.finebi.cube.api.ICubeColumnIndexReader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by GUY on 2015/3/19.
 */
public abstract class GroupTableIndex extends BaseTableIndex {

    public GroupTableIndex(String path, SingleUserNIOReadManager manager) {
        super(path, manager);
    }

    public GroupTableIndex(BITableCubeFile cube) {
        super(cube);
    }

    @Override
    public GroupValueIndex getNullGroupValueIndex(final BIKey key) {
        if (key == null) {
            return null;
        }
        return cube.getNullGroupValueIndex(key, manager);
    }

    @Override
    public ICubeTableIndexReader ensureBasicIndex(final List<BITableSourceRelation> relationList) {
        if (relationList == null || relationList.isEmpty()) {
            return null;
        }
        return cube.getBasicGroupValueIndexArrayReader(relationList, manager);
    }

    @Override
	public int getLinkedRowCount(List<BITableSourceRelation> relationList) {
    	return (int) cube.getLinkIndexFile(relationList).getGroupCount( BIKey.DEFAULT);
    }

    @Override
    public ICubeColumnIndexReader loadGroup(final BIKey key) {
        if (key == null) {
            return null;
        }
        return cube.createGroupByType(key, new ArrayList<BITableSourceRelation>(), manager);
    }

    /**
     * @param key
     * @param row
     * @return
     */
	@Override
	public GroupValueIndex getIndexByRow(BIKey key, int row) {
        return cube.getIndexByRow(key,row, manager);
	}

    @Override
    public GroupValueIndex[] getIndexes(BIKey key, Object[] values) {
        ICubeColumnIndexReader groupMap = loadGroup(key);
        if (groupMap == null) {
            return new GroupValueIndex[values.length];
        }
        Set<Integer> list = new HashSet<Integer>();
        Object[] newValues = groupMap.createKey(values.length);
        for (int i = 0; i < values.length; i++) {
            Object v = values[i];
            newValues[i] = groupMap.createValue(v);
            if (v == null) {
                list.add(i);
            }
        }
        values = newValues;
        Object[] obs = groupMap.getGroupIndex(values);
        GroupValueIndex[] res = new GroupValueIndex[obs.length];
        for (int i = 0; i < obs.length; i++) {
            if (list.contains(i)) {
                res[i] = GVIFactory.createAllEmptyIndexGVI();
            }
            res[i] = (GroupValueIndex) obs[i];
        }
        return res;
    }

    @Override
    public boolean isDistinct(String columnName) {
        BIKey columnIndex = getColumnIndex(columnName);
        return getGroupCount(columnIndex) == getRowCount();
    }


}