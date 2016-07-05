package com.fr.bi.cal.stable.tableindex.index;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.engine.index.BITableCubeFile;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.bi.stable.structure.collection.map.CubeTreeMap;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * Created by GUY on 2015/4/7.
 */
public class BITableIndex extends GroupTableIndex {

    public BITableIndex(String path, SingleUserNIOReadManager manager) {
        super(path, manager);
    }

    public BITableIndex(BITableCubeFile cube) {
        super(cube);
    }


    @Override
    public ICubeColumnIndexReader loadGroup(final BIKey key, List<BITableSourceRelation> relationList) {
        if (key == null) {
            return null;
        }
        return cube.createGroupByType(key, relationList, manager);
    }

    @Override
    public ICubeColumnIndexReader loadGroup(BIKey key, List<BITableSourceRelation> relationList, boolean useRealData, int groupLimit) {
        if (key == null) {
            return null;
        }
        ICubeColumnIndexReader loadAll = cube.createGroupByType(key, relationList, manager);
        if (!useRealData) {
            CubeTreeMap m = new CubeTreeMap(BIBaseConstant.COMPARATOR.STRING.ASC_STRING_CC);
            Iterator iter = loadAll.iterator();
            int i = 0;
            while (iter.hasNext() && i < groupLimit) {
                Entry entry = (Entry) iter.next();
                m.put(entry.getKey(), entry.getValue());
                i++;
            }
            return m;
        } else {
            return loadAll;
        }

    }

    @Override
    public boolean isDataAvailable() {
        return false;
    }
}