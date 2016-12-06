package com.fr.bi.stable.operation.group.group;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.GroupValueIndexOrHelper;
import com.fr.bi.stable.operation.group.AbstractGroup;
import com.fr.bi.stable.operation.group.data.number.NumberGroupInfo;
import com.fr.bi.stable.operation.group.data.number.NumberOtherGroupInfo;
import com.fr.bi.stable.structure.collection.map.CubeLinkedHashMap;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.*;

/**
 * Created by GUY on 2015/4/9.
 */
public class CustomNumberGroup extends AbstractGroup {

    @BICoreField
    protected NumberGroupInfo[] groups = new NumberGroupInfo[0];

    @Override
    public ICubeColumnIndexReader createGroupedMap(ICubeColumnIndexReader baseMap) {
        if (isNullGroup()) {
            return baseMap;
        }
        CubeLinkedHashMap newMap = new CubeLinkedHashMap();
        CubeLinkedHashMap ungroupMap = new CubeLinkedHashMap();
        GroupValueIndexOrHelper[] newMapArray = new GroupValueIndexOrHelper[groups.length];
        for (int i = 0; i < newMapArray.length; i++) {
            newMapArray[i] = new GroupValueIndexOrHelper();
        }
        Iterator iter = baseMap.iterator();
        GroupValueIndexOrHelper otherHelper = null;
        for (int i = 0; i < groups.length; i++) {
            if (groups[i].isOtherGroup()) {
                otherHelper = newMapArray[i];
                break;
            }
        }
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            if (entry.getKey() == null || ComparatorUtils.equals(entry.getKey(), "")) {
                newMap.put("", entry.getValue());
                continue;
            }
            double key = ((Number) entry.getKey()).doubleValue();
            GroupValueIndex gvi = (GroupValueIndex) entry.getValue();

            boolean find = false;
            for (int i = 0; i < groups.length; i++) {
                if (groups[i].contains(key) && !groups[i].isOtherGroup()) {
                    find = true;
                    newMapArray[i].add(gvi);
                }
            }
            if (!find) {
                if (otherHelper != null) {
                    otherHelper.add(gvi);
                } else {
                    String name = entry.getKey().toString();
                    ungroupMap.put(name, gvi);
                }
            }
        }
        for (int i = 0; i < groups.length; ++i) {
            newMap.put(groups[i].getValue(), newMapArray[i].compute());
        }
        Iterator it = ungroupMap.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            if (newMap.containsKey(entry.getKey())) {
                GroupValueIndex result = newMap.getIndex(entry.getKey()).OR((GroupValueIndex) entry.getValue());
                newMap.put(entry.getKey(), result);
            } else {
                newMap.put(entry.getKey(), entry.getValue());
            }
        }
        return newMap;
    }

    @Override
    public boolean isNullGroup() {
        return groups == null || groups.length == 0;
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        super.parseJSON(jo);
        if (jo.has("group_value")) {
            JSONObject valueJson = jo.optJSONObject("group_value");
            if (valueJson.has("group_nodes")) {
                JSONArray ja = valueJson.getJSONArray("group_nodes");
                int len = ja.length();
                if (valueJson.has("use_other")) {
                    groups = new NumberGroupInfo[len + 1];
                    groups[len] = new NumberOtherGroupInfo();
                    groups[len].setValue(valueJson.getString("use_other"));
                } else {
                    groups = new NumberGroupInfo[len];
                }
                for (int i = 0; i < len; i++) {
                    JSONObject oneGroup = ja.getJSONObject(i);

                    groups[i] = new NumberGroupInfo();
                    if (oneGroup.has("max")) {
                        groups[i].max = oneGroup.getDouble("max");
                        groups[i].closemax = oneGroup.getBoolean("closemax");
                    }

                    if (oneGroup.has("min")) {
                        groups[i].min = oneGroup.getDouble("min");
                        groups[i].closemin = oneGroup.getBoolean("closemin");
                    }

                    groups[i].setValue(oneGroup.getString("group_name"));
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CustomNumberGroup that = (CustomNumberGroup) o;

        if (!super.equals(o)) {
            return false;
        }
        if (!ComparatorUtils.equals(groups, that.groups)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (groups != null ? Arrays.hashCode(groups) : 0);
        return result;
    }


    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return this;
        }
    }
}