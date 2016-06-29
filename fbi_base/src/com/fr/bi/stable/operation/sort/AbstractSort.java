package com.fr.bi.stable.operation.sort;

import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.operation.sort.comp.CustomComparator;
import com.finebi.cube.api.ICubeColumnIndexReader;
import com.fr.bi.stable.structure.collection.map.CubeTreeMap;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by GUY on 2015/4/9.
 */
public abstract class AbstractSort implements ISort {

    protected String[] reg;
    private int sortType = BIReportConstant.SORT.NONE;

    @Override
    public int getSortType() {
        return sortType;
    }

    @Override
    public void setSortType(int sortType) {
        this.sortType = sortType;
    }


    @Override
    public ICubeColumnIndexReader createGroupedMap(ICubeColumnIndexReader baseMap) {
        CubeTreeMap treeMap = new CubeTreeMap(getComparator());
        Iterator it = baseMap.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            treeMap.put(entry.getKey(), entry.getValue());
        }
        return treeMap;
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("type")) {
            sortType = jo.optInt("type", BIReportConstant.SORT.ASC);
        }
        if (jo.has("details")) {
            JSONArray ja = jo.optJSONArray("details");
            int len = ja.length();
            reg = new String[len];
            for (int i = 0; i < len; i++) {
                reg[i] = ja.getString(i);
            }
            ((CustomComparator) getComparator()).setSortReg(reg);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AbstractSort)) {
            return false;
        }
        AbstractSort that = (AbstractSort) obj;
        if (sortType != that.sortType) {
            return false;
        }
        if (!ComparatorUtils.equals(reg, that.reg)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 31;
        result = 31 * result + sortType;
        result = 31 * result + (reg != null ? Arrays.hashCode(reg) : 0);
        return result;
    }
}