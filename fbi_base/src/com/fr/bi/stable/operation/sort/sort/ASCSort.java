package com.fr.bi.stable.operation.sort.sort;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.operation.sort.AbstractSort;
import com.fr.bi.stable.operation.sort.comp.ComparatorFacotry;
import com.fr.bi.stable.operation.sort.comp.IComparator;
import com.fr.bi.stable.structure.collection.map.CubeTreeMap;
import com.fr.general.GeneralUtils;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by GUY on 2015/4/9.
 */
public class ASCSort extends AbstractSort {

    protected transient IComparator comparator = ComparatorFacotry.getComparator(getSortType());

    @Override
    public int getSortType() {
        return BIReportConstant.SORT.ASC;
    }

    @Override
    public ICubeColumnIndexReader createGroupedMap(ICubeColumnIndexReader baseMap) {
        CubeTreeMap treeMap = new CubeTreeMap(getComparator());
        Iterator it = baseMap.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            treeMap.put(GeneralUtils.objectToString(entry.getKey()), entry.getValue());
        }
        return treeMap;
    }

    @Override
    public IComparator getComparator() {
        return comparator;
    }
}