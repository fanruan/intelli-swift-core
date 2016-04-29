package com.fr.bi.cluster.zookeeper;

import com.fr.bi.stable.data.BITable;
import com.fr.bi.stable.relation.BITableRelation;
import com.fr.json.JSONArray;
import com.fr.json.JSONCreator;
import com.fr.json.JSONObject;
import com.fr.json.JSONParser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Created by Connery on 2015/4/1.
 */
public class BINodeValueParser {

    private BINodeValueParser() {

    }


    public static ArrayList<JSONParser> string2BIJson(String content, int taskName) throws Exception {
        ArrayList<JSONParser> result = new ArrayList<JSONParser>();
        if (content != null && content.length() > 0) {
            JSONArray jsonArray = new JSONArray(content);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONParser key = new BITable("");
                switch (taskName) {
                    case BIWorkerNodeValue.TASK_LOAD_DB:
                        key = new BITable("");
                        break;
                    case BIWorkerNodeValue.TASK_BASIC_INDEX:
                        key = new BITableRelation();
                        break;
                    case BIWorkerNodeValue.TASK_FIRST_INDEX:
                        key = new BITable("");
                        break;

                }
                key.parseJSON(jsonObject);
                result.add(key);
            }
        }
        return result;
    }


    public static String BIJson2String(List<JSONCreator> objects) throws Exception {
        Iterator<JSONCreator> it = objects.iterator();
        JSONArray array = new JSONArray();
        while (it.hasNext()) {
            array.put((it.next()).createJSON());
        }
        return array.toString();
    }


}