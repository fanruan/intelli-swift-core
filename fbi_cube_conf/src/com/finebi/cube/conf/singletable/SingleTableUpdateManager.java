package com.finebi.cube.conf.singletable;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.BIUser;
import com.fr.bi.common.factory.BIFactory;
import com.fr.bi.common.inter.Release;
import com.fr.bi.stable.data.db.IPersistentTable;
import com.fr.bi.stable.utils.code.BILogDelegate;
import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class created on 2016/5/23.
 *
 * @author Connery
 * @since 4.0
 */
public class SingleTableUpdateManager implements JSONTransform, Release {

    private Map<BusinessTable, TableUpdate> single_table_update = new HashMap<BusinessTable, TableUpdate>();
    private BIUser user;
    public static String XML_TAG = "SingleTableUpdateManager";

    public SingleTableUpdateManager(long userId) {
        try {
            user = (BIUser) BIFactory.getInstance().getObject(BIUser.class.getName(), userId);
        } catch (Exception ex) {
            BILogDelegate.errorDelegate(ex.getMessage(), ex);
        }
    }

    public TableUpdate getSingleTableUpdateAction(IPersistentTable table) {
//        SingleTableUpdateAction action = single_table_update.get(table);
//        if (action == null) {
//            action = new SingleTableUpdateAction(table, user.getUserId());
//        }
//        return action;
        return null;
    }

    public void setSingleTableUpdateAction(TableUpdate action, BICubeTimeTaskCreator creator) {
        synchronized (this) {
            TableUpdate old = single_table_update.get(action.getTableKey());
            if (old != null) {
                old.clear();
            }
            action.scheduleStart(creator);
            single_table_update.put(action.getTableKey(), action);
        }
    }

    public Iterator<TableUpdate> getSingleTableUpdateActionIter() {
        return single_table_update.values().iterator();
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
        Iterator<Map.Entry<BusinessTable, TableUpdate>> iter = single_table_update.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<BusinessTable, TableUpdate> entry = iter.next();
            TableUpdate sa = entry.getValue();
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