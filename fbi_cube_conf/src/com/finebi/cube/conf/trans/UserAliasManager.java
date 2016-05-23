package com.finebi.cube.conf.trans;

import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * Created by GUY on 2015/4/2.
 *
 * @author Connery
 */
public class UserAliasManager {


    private final Map<String, String> transMap = new HashMap<String, String>();
    private long userId;

    public UserAliasManager(long userId) {
        this.userId = userId;
    }

    public String getTransName(String id) {
        synchronized (transMap) {
            return transMap.get(id);
        }
    }

    public void setTransName(String id, String name) {
        if (StringUtils.isEmpty(id) || StringUtils.isEmpty(name)) {
            return;
        }
        synchronized (transMap) {
            transMap.put(id, name);
        }
    }

    public JSONObject createJSON() throws Exception {
        JSONObject trans = new JSONObject();
        Iterator<Map.Entry<String, String>> it = transMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            trans.put(entry.getKey(), entry.getValue());
        }
        return trans;
    }


    public void clear() {
        synchronized (transMap) {
            transMap.clear();
        }
    }
}