package com.fr.bi.conf.manager.excelview.source;

import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;
import com.fr.stable.ColumnRow;

import java.util.*;

/**
 * Created by Young's on 2016/4/20.
 */
public class ExcelViewSource implements JSONTransform {

    private static final String XML_TAG = "ExcelViewSource";

    private Map<String, ColumnRow> positions = new HashMap<String, ColumnRow>();
    private List<List<String>> excel;
    private String excelName;
    private List<List<List<String>>> mergeInfos;

    public Map<String, ColumnRow> getPositions() {
        return positions;
    }

    public void setPositions(Map<String, ColumnRow> positions) {
        this.positions = positions;
    }

    public List<List<String>> getExcel() {
        return excel;
    }

    public void setExcel(List<List<String>> excel) {
        this.excel = excel;
    }

    public List<List<List<String>>> getMergeInfos() {
        return mergeInfos;
    }

    public void setMergeInfos(List<List<List<String>>> mergeInfos) {
        this.mergeInfos = mergeInfos;
    }

    public ExcelViewSource() {

    }

    public ExcelViewSource(Map<String, ColumnRow> positions, List<List<String>> excel, String excelName, List<List<List<String>>> mergeInfos) {
        this.positions = positions;
        this.excel = excel;
        this.excelName = excelName;
        this.mergeInfos = mergeInfos;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        Iterator<Map.Entry<String, ColumnRow>> iterator = positions.entrySet().iterator();
        JSONObject positions = new JSONObject();
        while (iterator.hasNext()) {
            Map.Entry<String, ColumnRow> map = iterator.next();
            JSONObject pos = new JSONObject();
            pos.put("col", map.getValue().getColumn());
            pos.put("row", map.getValue().getRow());
            positions.put(map.getKey(), pos);
        }
        jo.put("positions", positions);
        JSONArray excel = new JSONArray();
        for (int i = 0; i < this.excel.size(); i++) {
            JSONArray row = new JSONArray();
            List<String> rowList = this.excel.get(i);
            for (int j = 0; j < rowList.size(); j++) {
                row.put(rowList.get(j));
            }
            excel.put(row);
        }
        jo.put("excel", excel);
        jo.put("name", this.excelName);
        if (this.mergeInfos != null) {
            JSONArray mergeInfos = new JSONArray();
            for (int i = 0; i < this.mergeInfos.size(); i++) {
                JSONArray mergeInfo = new JSONArray();
                List<List<String>> mergeInfoList = this.mergeInfos.get(i);
                for (int j = 0; j < mergeInfoList.size(); j++) {
                    JSONArray flag = new JSONArray();
                    List<String> flagList = mergeInfoList.get(j);
                    for (int k = 0; k < flagList.size(); k++) {
                        flag.put(flagList.get(k));
                    }
                    mergeInfo.put(flag);
                }
                mergeInfos.put(mergeInfo);
            }
            jo.put("mergeInfos", mergeInfos);
        }
        return jo;
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("positions")) {
            JSONObject positions = jo.getJSONObject("positions");
            Iterator<String> iterator = positions.keys();
            while (iterator.hasNext()) {
                String fieldId = iterator.next();
                JSONObject pos = positions.getJSONObject(fieldId);
                this.positions.put(fieldId, ColumnRow.valueOf(pos.getInt("col"), pos.getInt("row")));
            }
        }

        if (jo.has("excel")) {
            JSONArray excel = jo.getJSONArray("excel");
            this.excel = new ArrayList<List<String>>();
            for (int i = 0; i < excel.length(); i++) {
                JSONArray row = excel.getJSONArray(i);
                List<String> rowList = new ArrayList<String>();
                for (int j = 0; j < row.length(); j++) {
                    rowList.add(j, row.getString(j));
                }
                this.excel.add(i, rowList);
            }
        }

        if (jo.has("name")) {
            this.excelName = jo.getString("name");
        }

        if (jo.has("mergeInfos")) {
            JSONArray mergeInfos = jo.getJSONArray("mergeInfos");
            this.mergeInfos = new ArrayList<List<List<String>>>();
            for (int i = 0; i < mergeInfos.length(); i++) {
                JSONArray mergeInfo = mergeInfos.getJSONArray(i);
                List<List<String>> mergeInfoList = new ArrayList<List<String>>();
                for (int j = 0; j < mergeInfo.length(); j++) {
                    JSONArray flag = mergeInfo.getJSONArray(j);
                    List<String> flagList = new ArrayList<String>();
                    for (int k = 0; k < flag.length(); k++) {
                        flagList.add(k, flag.getString(k));
                    }
                    mergeInfoList.add(j, flagList);
                }
                this.mergeInfos.add(i, mergeInfoList);
            }
        }
    }

//    @Override
//    public void readXML(XMLableReader reader) {
//        if(reader.isAttr()) {
//            this.userId = reader.getAttrAsLong("userId", UserControl.getInstance().getSuperManagerID());
//            this.excel = reader.getAttrAsString("excel", StringUtils.EMPTY);
//        }
//        if(reader.isChildNode()) {
//            String tag = reader.getTagName();
//            if (ComparatorUtils.equals(tag, "position")) {
//                int column = reader.getAttrAsInt("column", 0);
//                int row = reader.getAttrAsInt("row", 0);
//                String fieldId = reader.getAttrAsString("field_id", "");
//                this.positions.put(fieldId, ColumnRow.valueOf(column, row));
//            }
//        }
//    }
//
//    @Override
//    public void writeXML(XMLPrintWriter writer) {
//        writer.startTAG(XML_TAG);
//        writer.attr("userId", this.userId);
//        writer.attr("excel", this.excel);
//
//        if(positions != null) {
//            Iterator<Map.Entry<String, ColumnRow>> iterator = positions.entrySet().iterator();
//            while (iterator.hasNext()) {
//                Map.Entry<String, ColumnRow> map = iterator.next();
//                writer.startTAG("position");
//                writer.attr("field_id", map.getKey());
//                writer.attr("column", map.getValue().getColumn()).attr("row", map.getValue().getRow());
//                writer.end();
//            }
//        }
//        writer.end();
//    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}