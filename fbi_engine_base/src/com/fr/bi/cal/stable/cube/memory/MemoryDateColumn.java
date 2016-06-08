package com.fr.bi.cal.stable.cube.memory;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.fr.bi.base.ValueConverterFactory;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.cal.stable.tableindex.detailgetter.MemoryDateDetailGetter;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.engine.index.getter.DetailGetter;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.engine.index.key.IndexTypeKey;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.bi.stable.operation.sort.comp.ComparatorFacotry;
import com.finebi.cube.relation.BITableSourceRelation;

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

    public DetailGetter createDetailGetter(SingleUserNIOReadManager manager) {
        return new MemoryDateDetailGetter(detail);
    }


    @Override
    protected void initDetail() {
        detail = new AnyIndexArray<Long>(new NullChecker<Long>() {
            @Override
            public boolean isNull(Long v) {
                return v ==null || v == Long.MAX_VALUE;
            }
        });
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

    @Override
    protected Object createEmptyValue(BIKey key) {
//       if (key instanceof IndexTypeKey) {
//           int type = ((IndexTypeKey) key).getType();
//           switch (type) {
//               case  BIReportConstant.GROUP.Y: {
//                   return Integer.MAX_VALUE;
//               }
//               case BIReportConstant.GROUP.M: {
//                   return Integer.MAX_VALUE;
//               }
//               case  BIReportConstant.GROUP.S: {
//                   return Integer.MAX_VALUE;
//               }
//               case  BIReportConstant.GROUP.MD: {
//                   return Integer.MAX_VALUE;
//               }
//               case  BIReportConstant.GROUP.W: {
//                   return Integer.MAX_VALUE;
//               }
//           }
//       }
        return Long.MAX_VALUE;
    }


}