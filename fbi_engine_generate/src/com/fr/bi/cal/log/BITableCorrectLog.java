package com.fr.bi.cal.log;

import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.Table;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLReadable;
import com.fr.stable.xml.XMLableReader;

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

    public BITableCorrectLog() {
    }

    public BITableCorrectLog(Table table, long t, long userId) {
        super(table, userId);
        this.getValueFromDB = t;
    }

    public void setIndexTime(long i) {
        this.indexTime = i;
    }

    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isAttr()) {
            getValueFromDB = reader.getAttrAsLong("getValueFromDB", -1L);
            this.setID(new BITableID(reader.getAttrAsString("id", "")));
        }
        if (reader.isChildNode()) {
            reader.readXMLObject(new XMLReadable() {
                @Override
                public void readXML(XMLableReader reader) {
                    if (reader.isChildNode()) {
                        if (ComparatorUtils.equals(reader.getTagName(), BIColumnLog.XML_TAG)) {
                            BIColumnLog log = new BIColumnLog();
                            log.readXML(reader);
                            columnList.put(log.getColumnName(), log);
                        } else if (ComparatorUtils.equals(reader.getTagName(), BIColumnRunningLog.XML_TAG)) {
                            BIColumnRunningLog log = new BIColumnRunningLog();
                            log.readXML(reader);
                            columnList.put(log.getColumnName(), log);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        writer.attr("getValueFromDB", getValueFromDB);
        writer.attr("id", this.getID().getIdentityValue());
        Iterator<Entry<String, BIColumnLog>> iter = columnList.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, BIColumnLog> entry = iter.next();
            entry.getValue().writeXML(writer);
        }
        writer.end();
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = super.createJSON();
        jo.put("time", getValueFromDB);
        JSONArray columIndexs = new JSONArray();
        jo.put("column", columIndexs);
        Iterator<Entry<String, BIColumnLog>> iter = columnList.entrySet().iterator();
        List<BIColumnLog> list = new ArrayList<BIColumnLog>();
        while (iter.hasNext()) {
            Entry<String, BIColumnLog> entry = iter.next();
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
            columIndexs.put(it.next().createJSON());
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