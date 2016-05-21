package com.fr.bi.conf.manager.singletable;

import com.fr.bi.base.BIUser;
import com.fr.bi.common.factory.BIFactory;
import com.fr.bi.common.inter.Release;
import com.fr.bi.conf.manager.singletable.data.BICubeTimeTaskCreator;
import com.fr.bi.conf.manager.singletable.data.SingleTableUpdateAction;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.data.db.IPersistentTable;
import com.fr.bi.stable.utils.code.BILogDelegate;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLReadable;
import com.fr.stable.xml.XMLable;
import com.fr.stable.xml.XMLableReader;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by GUY on 2015/4/2.
 */
public abstract class AbstractSingleTableUpdateManager implements XMLable, JSONTransform, Release {

    private Map<Table, SingleTableUpdateAction> single_table_update = new HashMap<Table, SingleTableUpdateAction>();
    private BIUser user;

    public AbstractSingleTableUpdateManager(long userId) {
        try {
            user = (BIUser) BIFactory.getInstance().getObject(BIUser.class.getName(), userId);
        } catch (Exception ex) {
            BILogDelegate.errorDelegate(ex.getMessage(), ex);
        }
    }

    public SingleTableUpdateAction getSingleTableUpdateAction(IPersistentTable table) {
//        SingleTableUpdateAction action = single_table_update.get(table);
//        if (action == null) {
//            action = new SingleTableUpdateAction(table, user.getUserId());
//        }
//        return action;
        return null;
    }

    public void setSingleTableUpdateAction(SingleTableUpdateAction action, BICubeTimeTaskCreator creator) {
        synchronized (this) {
            SingleTableUpdateAction old = single_table_update.get(action.getTableKey());
            if (old != null) {
                old.clear();
            }
            action.scheduleStart(creator);
            single_table_update.put(action.getTableKey(), action);
        }
    }

    public Iterator<SingleTableUpdateAction> getSingleTableUpdateActionIter() {
        return single_table_update.values().iterator();
    }

    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isChildNode()) {
            final Map<Table, SingleTableUpdateAction> map = new HashMap<Table, SingleTableUpdateAction>();
            reader.readXMLObject(new XMLReadable() {
                @Override
                public void readXML(XMLableReader reader) {
                    if (reader.isChildNode()) {
                        if (ComparatorUtils.equals(reader.getTagName(), SingleTableUpdateAction.XML_TAG)) {
                            SingleTableUpdateAction action = new SingleTableUpdateAction();
                            reader.readXMLObject(action);
                            if (map.containsKey(action.getTableKey())) {
                                return;
                            }
                            map.put(action.getTableKey(), action);
                        }
                    }
                }
            });
            this.single_table_update.putAll(map);
        }
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG("single_table_update");
        Iterator<Map.Entry<Table, SingleTableUpdateAction>> it = single_table_update.entrySet().iterator();
        while (it.hasNext()) {
            it.next().getValue().writeXML(writer);
        }
        writer.end();
    }

    @Override
    public void parseJSON(JSONObject json) throws Exception {
        user.parseJSON(json.getJSONObject("user"));
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("user", user.createJSON());
        return jo;

    }

    @Override
    public void clear() {
        Iterator<Map.Entry<Table, SingleTableUpdateAction>> iter = single_table_update.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Table, SingleTableUpdateAction> entry = iter.next();
            SingleTableUpdateAction sa = entry.getValue();
            if (sa != null) {
                sa.clear();
            }
        }
        single_table_update.clear();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}