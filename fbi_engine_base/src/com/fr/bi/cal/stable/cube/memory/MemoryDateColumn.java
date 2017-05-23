package com.fr.bi.cal.stable.cube.memory;

import com.finebi.cube.api.ICubeColumnDetailGetter;
import com.finebi.cube.api.ICubeColumnIndexReader;
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
    public long getGroupCount(BIKey key) {
        /**
         * Connery:日期类型的，getter要从getters中获取。
         * 这个对象不能被重用。
         *
         */
        if(this.getter==null) {
            this.getter = createGroupByType(key, new ArrayList<BITableSourceRelation>(), null);
        }
        return this.getter.sizeOfGroup();
    }
    /**
     * Connery:重写掉获取位置的方法。
     * 日期类型的子类型，从明细数据里面取出来的是原始数据，而非子类型的数据。
     * 例如 第一季度，需要的groupValue是1，但是原始数据可能是1490756112763
     */
    @Override
    public int getPositionOfGroup(int row, SingleUserNIOReadManager manager) {
        if (groupPosition != null) {
            Integer value = groupPosition.get(row);
            return processPosition(value);
        }
        for (int i = 0; i < getter.sizeOfGroup(); i++) {
            GroupValueIndex groupValueIndex = getter.getGroupValueIndex(i);
            if (groupValueIndex.isOneAt(row)) {
                return i;
            }
        }
        return 0;
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