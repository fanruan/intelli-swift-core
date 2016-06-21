package com.fr.bi.cal.stable.cube.memory;


import com.fr.bi.base.ValueConverter;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.cal.stable.tableindex.detailgetter.MemoryDetailGetter;
import com.fr.bi.stable.engine.index.getter.DetailGetter;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.bi.stable.operation.sort.comp.ComparatorFacotry;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.api.ICubeColumnIndexReader;
import com.fr.stable.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 小灰灰 on 2016/1/14.
 */
public class MemoryStringColumn extends AbstractSingleMemoryColumn<String> {
    @Override
    public DetailGetter<String> createDetailGetter(SingleUserNIOReadManager manager) {
        return new MemoryDetailGetter<String>(detail);
    }
    @Override
    public ICubeColumnIndexReader createGroupByType(BIKey key, List<BITableSourceRelation> relationList, SingleUserNIOReadManager manager) {
        if (getter == null){
            synchronized (getterLock){
                if (getter == null){
                    getter = createGroupByType(key, ValueConverter.DEFAULT ,ComparatorFacotry.CHINESE_ASC);
                }
            }
        }
        return getter;
    }

    @Override
    protected void initDetail() {
        detail = new AnyIndexArray<String>();
    }
}