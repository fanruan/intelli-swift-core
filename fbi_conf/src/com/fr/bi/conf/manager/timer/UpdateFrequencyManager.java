package com.fr.bi.conf.manager.timer;

import com.fr.bi.base.BIUser;
import com.fr.bi.common.inter.Release;
import com.fr.bi.conf.manager.timer.data.UpdateFrequency;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLReadable;
import com.fr.stable.xml.XMLable;
import com.fr.stable.xml.XMLableReader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by GUY on 2015/3/31.
 */
public class UpdateFrequencyManager  implements XMLable,JSONTransform, Release {
    public UpdateFrequencyManager(long userId) {
        biUser = new BIUser(userId);
    }

    public Iterator<UpdateFrequency> getUpdateListIterator() {
        return updateList.iterator();
    }


    protected List<UpdateFrequency> updateList = new ArrayList<UpdateFrequency>();


    protected BIUser biUser;

    public List<UpdateFrequency> getUpdateList() {
        return updateList;
    }

    public BIUser getBiUser() {
        return biUser;
    }

    public void setBiUser(BIUser biUser) {
        this.biUser = biUser;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONArray ja = new JSONArray();
        for (UpdateFrequency anUpdateList : this.updateList) {
            ja.put(anUpdateList.createJSON());
        }
        return new JSONObject().put("update_list", ja);
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        synchronized (this) {
            JSONArray ja = jo.optJSONArray("update_list");
            List<UpdateFrequency> list = new ArrayList<UpdateFrequency>();
            for (int i = 0, len = ja.length(); i < len; i++) {
                UpdateFrequency uf = new UpdateFrequency();
                uf.parseJSON(ja.getJSONObject(i));
                list.add(uf);
            }
            this.updateList = list;
        }
    }

    @Override
    public void readXML(XMLableReader reader) {
        final java.util.List<UpdateFrequency> update_list = new java.util.ArrayList<UpdateFrequency>();
        reader.readXMLObject(new XMLReadable() {
            @Override
            public void readXML(XMLableReader reader) {
                if (reader.isChildNode()) {
                    if (ComparatorUtils.equals(reader.getTagName(), UpdateFrequency.XML_TAG)) {
                        UpdateFrequency pack = new UpdateFrequency();
                        reader.readXMLObject(pack);
                        update_list.add(pack);
                    }
                }
            }
        });
        this.updateList.addAll(update_list);
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG("update_list");
        for (int i = 0; i < this.updateList.size(); i++) {
            this.updateList.get(i).writeXML(writer);
        }
        writer.end();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public void releaseResource() {
        updateList.clear();
    }
}