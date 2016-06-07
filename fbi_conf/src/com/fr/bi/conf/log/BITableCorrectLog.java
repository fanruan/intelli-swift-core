package com.fr.bi.conf.log;

import com.fr.bi.stable.data.db.IPersistentTable;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.*;
import java.util.Map.Entry;

public class BITableCorrectLog extends BITableLog {

    public static final String XML_TAG = "bi_table_log";
    /**
     *
     */
    private static final long serialVersionUID = 8556623220342935484L;
    private long getValueFromDB = -1L;

    private long indexTime = -1L;

    private Map<String, BIColumnLog> columnList = new LinkedHashMap<String, BIColumnLog>();


    public BITableCorrectLog(IPersistentTable table, long t, long userId) {
        super(table, userId);
        this.getValueFromDB = t;
    }

    public void setIndexTime(long i) {
        this.indexTime = i;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = super.createJSON();
        jo.put("time", getValueFromDB);
        JSONArray columnIndexes = new JSONArray();
        jo.put("column", columnIndexes);
        Iterator<Entry<String, BIColumnLog>> iterator = columnList.entrySet().iterator();
        List<BIColumnLog> list = new ArrayList<BIColumnLog>();
        while (iterator.hasNext()) {
            Entry<String, BIColumnLog> entry = iterator.next();
            list.add(entry.getValue());
        }
        Collections.sort(list, new Comparator<BIColumnLog>() {
            @Override
            public int compare(BIColumnLog o1, BIColumnLog o2) {
                if (o1.isRunning()) {
                    if (o2.isRunning()) {
                        if (o1.getTime() == o2.getTime()) {
                            return 0;
                        }
                        return o1.getTime() < o2.getTime() ? 1 : -1;
                    }
                    return -1;
                } else if (o2.isRunning()) {
                    return 1;
                } else {
                    if (o1.getTime() == o2.getTime()) {
                        return 0;
                    }
                    return o1.getTime() < o2.getTime() ? 1 : -1;
                }
            }
        });
        Iterator<BIColumnLog> it = list.iterator();
        while (it.hasNext()) {
            columnIndexes.put(it.next().createJSON());
        }
        return jo;
    }

    public void info_column(String columnName, long t, int percent) {
        columnList.put(columnName, new BIColumnRunningLog(columnName, t, percent));
    }

    public void info_column(String columnName, long t) {
        columnList.put(columnName, new BIColumnLog(columnName, t));
    }

    @Override
    public boolean isRunning() {
        Iterator<Entry<String, BIColumnLog>> iter = columnList.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, BIColumnLog> entry = iter.next();
            if (entry.getValue().isRunning()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public long getTotalTime() {
        long t = getValueFromDB + indexTime;
        return Math.max(t, 0L);
    }

}
