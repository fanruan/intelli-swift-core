package com.fr.bi.stable.operation.group.group;

import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.operation.group.AbstractGroup;
import com.fr.bi.stable.operation.group.data.string.StringGroupInfo;
import com.finebi.cube.api.ICubeColumnIndexReader;
import com.fr.bi.stable.structure.collection.map.CubeLinkedHashMap;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLReadable;
import com.fr.stable.xml.XMLableReader;

import java.util.*;

/**
 * Created by GUY on 2015/4/9.
 */
public class CustomGroup extends AbstractGroup {

    public static final String XML_TAG = "CustomGroup";
    @BICoreField
    protected int ungroup2Other = 0;
    @BICoreField
    protected String ungroup2OtherName;
    @BICoreField
    protected StringGroupInfo[] groups = new StringGroupInfo[0];

    public boolean ungroup2Other() {
        return ungroup2Other == 1;
    }

    public String getUngroup2OtherName() {
        return ungroup2OtherName;
    }

    public StringGroupInfo[] getGroups() {
        return groups;
    }

    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isAttr()) {
            this.ungroup2Other = reader.getAttrAsInt("ungroup2Other", 0);
            this.ungroup2OtherName = reader.getAttrAsString("ungroup2OtherName", StringUtils.EMPTY);
        }
        final List<StringGroupInfo> list = new ArrayList<StringGroupInfo>();
        reader.readXMLObject(new XMLReadable() {
            @Override
            public void readXML(XMLableReader xmLableReader) {
                if (ComparatorUtils.equals(xmLableReader.getTagName(), StringGroupInfo.XML_TAG)) {
                    StringGroupInfo info = new StringGroupInfo();
                    info.readXML(xmLableReader);
                    list.add(info);
                }
            }
        });
        groups = list.toArray(new StringGroupInfo[list.size()]);
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        writer.attr("ungroup2Other", ungroup2Other).attr("ungroup2OtherName", ungroup2OtherName);
        for (StringGroupInfo info : groups) {
            info.writeXML(writer);
        }
        writer.end();
    }

    /**
     * 创建分组MAP
     *
     * @param baseMap 分组Map
     * @return MAP对象
     */
    @Override
    public ICubeColumnIndexReader createGroupedMap(ICubeColumnIndexReader baseMap) {
        if (isNullGroup()) {
            return baseMap;
        }
        CubeLinkedHashMap newMap = new CubeLinkedHashMap();
        CubeLinkedHashMap ungroupMap = new CubeLinkedHashMap();
        Set[] set = createConfigGroupMap();
        Iterator iter = baseMap.iterator();
        int len = groups.length;
        GroupValueIndex otherGVi = null;
        GroupValueIndex[] newMapArray = new GroupValueIndex[len];
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            if (key == null){
                continue;
            }
            GroupValueIndex gvi = (GroupValueIndex) entry.getValue();
            boolean contains = false;
            for (int i = 0; i < len; i++) {
                if (set[i].contains(key.toString())) {
                    contains = true;
                    newMapArray[i] = GVIUtils.OR(newMapArray[i], gvi);
                }
            }
            if (!contains) {
                if (ungroup2Other == 1) {
                    otherGVi = GVIUtils.OR(otherGVi, gvi);
                } else {
                    String name = key.toString();
                    ungroupMap.put(name, GVIUtils.OR((GroupValueIndex) newMap.get(name), gvi));
                }
            }
        }
        for (int i = 0; i < len; i++) {
            newMap.put(groups[i].getValue(), newMapArray[i]);
        }
        newMap.putAll(ungroupMap);
        if (otherGVi != null) {
            newMap.put(ungroup2OtherName, otherGVi);
        }
        return newMap;
    }

    /**
     * d是否是空分组
     *
     * @return true或false
     */
    @Override
    public boolean isNullGroup() {
        return groups == null || groups.length == 0;
    }

    /**
     * @return 分组集合
     */
    protected Set[] createConfigGroupMap() {
        int len = groups.length;
        Set[] groupSet = new Set[len];
        for (int i = 0; i < len; i++) {
            groupSet[i] = groups[i].createValueSet();
        }
        return groupSet;
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        super.parseJSON(jo);
        if (jo.has("ungroup2Other")) {
            ungroup2Other = jo.getInt("ungroup2Other");
        }
        if (jo.has("ungroup2OtherName")) {
            ungroup2OtherName = jo.getString("ungroup2OtherName");
        }
        if (jo.has("details")) {
            JSONArray ja = jo.getJSONArray("details");
            int len = ja.length();
            groups = new StringGroupInfo[len];
            for (int i = 0; i < len; i++) {
                groups[i] = new StringGroupInfo();
                groups[i].parseJSON(ja.getJSONObject(i));
            }
        }
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("ungroup2Other", ungroup2Other);
        jo.put("ungroup2OtherName", ungroup2OtherName);
        int len = groups.length;
        JSONArray ja = new JSONArray();
        for (int i = 0; i < len; i++) {
            ja.put(groups[i].createJSON());
        }
        jo.put("details", ja);
        return jo;
    }


    /**
     * hash值
     *
     * @return hash值
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Arrays.hashCode(groups);
        result = prime * result + type;
        result = prime * result + ungroup2Other;
        result = prime * result + (ungroup2OtherName == null ? 0 : ungroup2OtherName.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        CustomGroup other = (CustomGroup) obj;
        if (!ComparatorUtils.equals(groups, other.groups)) {
            return false;
        }
        if (!ComparatorUtils.equals(ungroup2Other, other.ungroup2Other)) {
            return false;
        }
        if (!ComparatorUtils.equals(ungroup2OtherName, other.ungroup2OtherName)) {
            return false;
        }
        return true;
    }

    @Override
    public Object clone() {
        try {
            CustomGroup sg = (CustomGroup) super.clone();
            sg.type = this.type;
            sg.ungroup2Other = this.ungroup2Other;
            sg.ungroup2OtherName = this.ungroup2OtherName;
            if (this.groups != null) {
                sg.groups = new StringGroupInfo[this.groups.length];
                for (int i = 0, len = this.groups.length; i < len; i++) {
                    sg.groups[i] = (StringGroupInfo) this.groups[i].clone();
                }
            }
            return sg;
        } catch (CloneNotSupportedException e) {
            return this;
        }
    }
}