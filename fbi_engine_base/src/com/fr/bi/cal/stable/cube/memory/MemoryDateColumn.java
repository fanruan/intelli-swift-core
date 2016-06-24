package com.fr.bi.cal.stable.cube.memory;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeColumnDetailGetter;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.base.ValueConverterFactory;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.cal.stable.tableindex.detailgetter.MemoryDetailGetter;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.engine.index.key.IndexTypeKey;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.bi.stable.operation.sort.comp.ComparatorFacotry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 小灰灰 on 2016/1/14.
 */
public class MemoryDateColumn extends AbstractSingleMemoryColumn<Long> {
    private Map<Integer, ICubeColumnIndexReader> getters = new ConcurrentHashMap<Integer, ICubeColumnIndexReader>();
    private Map<Integer, Object> locks = new ConcurrentHashMap<Integer, Object>();

    public ICubeColumnDetailGetter createDetailGetter(SingleUserNIOReadManager manager) {
        return new MemoryDetailGetter(detail);
    }


    @Override
    protected void initDetail() {
        detail = new AnyIndexArray<Long>();
    }

    @Override
    public GroupValueIndex getIndexByRow(int row, SingleUserNIOReadManager manager) {
        return this.createGroupByType(new IndexKey(""), new ArrayList<BITableSourceRelation>(), manager).getGroupIndex(new Object[]{detail.get(row)})[0];
    }

    @Override
    public ICubeColumnIndexReader createGroupByType(BIKey key, List<BITableSourceRelation> relationList, SingleUserNIOReadManager manager) {

        if (key instanceof IndexTypeKey) {
            int type = ((IndexTypeKey) key).getType();
            if (getters.get(type) == null) {
                synchronized (locks) {
                    if (locks.get(type) == null) {
                        locks.put(type, new Object());
                    }
                }
                Object l = locks.get(type);
                synchronized (l) {
                    if (getters.get(type) == null) {
                        getters.put(type, createGroupByType(key, ValueConverterFactory.createDateValueConverterByGroupType(type), ComparatorFacotry.createASCComparator()));
                    }
                }
            }
            return getters.get(type);
        } else {
            return super.createGroupByType(key, relationList, manager);
        }
    }


}