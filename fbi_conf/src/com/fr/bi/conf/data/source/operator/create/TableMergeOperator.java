package com.fr.bi.conf.data.source.operator.create;

import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.data.source.operator.IETLOperator;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.DBTable;
import com.fr.bi.stable.data.source.ITableSource;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;
import com.fr.stable.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 小灰灰 on 2016/5/12.
 */
public class TableMergeOperator extends AbstractCreateTableETLOperator{
    public static final String XML_TAG = "TableMergeOperator";
    private static final int MERGE_TYPE_UNION = 5;
    private int mergeType;
    private List<MergeColumn> columns;
    private transient IETLOperator transOp;
    @Override
    public String xmlTag() {
        return XML_TAG;
    }

    @Override
    public DBTable getBITable(DBTable[] tables) {
        return getTransOperatpr().getBITable(tables);
    }

    @Override
    public int writeSimpleIndex(Traversal<BIDataValue> travel, List<? extends ITableSource> parents, ICubeDataLoader loader) {
        return getTransOperatpr().writeSimpleIndex(travel, parents, loader);
    }

    @Override
    public int writePartIndex(Traversal<BIDataValue> travel, List<? extends ITableSource> parents, ICubeDataLoader loader, int startCol, int start, int end) {
        return getTransOperatpr().writePartIndex(travel, parents, loader, startCol, start, end);
    }

    private IETLOperator getTransOperatpr(){
        if (transOp != null){
            return transOp;
        }
        if (this.mergeType == MERGE_TYPE_UNION){
            List<List<String>> lists = new ArrayList<List<String>>();
            for (MergeColumn c : this.columns){
                String[] list = {c.name, StringUtils.EMPTY,StringUtils.EMPTY};
                for (int i = 0; i < c.columns.length; i++){
                    SingleColumn sc = c.columns[i];
                    list[sc.index + 1] = sc.name;
                }
                lists.add(Arrays.asList(list));
            }
            transOp = new TableUnionOperator(lists);
        } else {
            List<String> left = new ArrayList<String>();
            List<String> right = new ArrayList<String>();
            List<JoinColumn> joinColumns = new ArrayList<JoinColumn>();
            for (int i = 0; i < this.columns.size(); i++){
                MergeColumn c = this.columns.get(i);
                JoinColumn joinColumn = new JoinColumn(c.name, c.isLeft(), c.columns[0].name);
                if (c.columns.length == 2){
                    left.add(c.columns[0].name);
                    left.add(c.columns[1].name);
                }
                joinColumns.add(joinColumn);
            }
            transOp = new TableJoinOperator(mergeType, joinColumns, left, right);
        }
        return transOp;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("mergeType", mergeType);
        if (this.columns != null){
            JSONArray ja = new JSONArray();
            for (MergeColumn c : this.columns){
                ja.put(c.createJSON());
            }
            jo.put("columns", ja);
        }
        return jo;
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        this.mergeType = jo.getInt("mergeType");
        JSONArray ja = jo.getJSONArray("columns");
        this.columns = new ArrayList<MergeColumn>();
        for (int i = 0; i < ja.length(); i++){
            MergeColumn c = new MergeColumn();
            c.parseJSON(ja.getJSONObject(i));
            columns.add(c);
        }
    }

    private class MergeColumn implements JSONTransform{
        private String name;
        private int type;
        private SingleColumn[] columns ;

        @Override
        public JSONObject createJSON() throws Exception {
            JSONObject jo = new JSONObject();
            jo.put("field_name", name);
            jo.put("field_type", type);
            if (this.columns != null){
                JSONArray ja = new JSONArray();
                for (SingleColumn c : this.columns){
                    ja.put(c.createJSON());
                }
                jo.put(BIJSONConstant.JSON_KEYS.STATISTIC_ELEMENT, ja);
            }
            return jo;
        }

        @Override
        public void parseJSON(JSONObject jo) throws Exception {
            this.name = jo.getString("field_name");
            this.type = jo.getInt("field_type");
            JSONArray ja = jo.getJSONArray(BIJSONConstant.JSON_KEYS.STATISTIC_ELEMENT);
            this.columns = new SingleColumn[ja.length()];
            for (int i = 0; i < ja.length(); i++){
                SingleColumn c = new SingleColumn();
                c.parseJSON(ja.getJSONObject(i));
                this.columns[i] = c;
            }
        }

        public boolean isLeft() {
            return columns[0].index == 0;
        }
    }

    private class SingleColumn implements JSONTransform{
        private String name;
        private int type;
        private int index;
        @Override
        public JSONObject createJSON() throws Exception {
            JSONObject jo = new JSONObject();
            jo.put("field_name", name);
            jo.put("field_type", type);
            jo.put("table_index", index);
            return jo;
        }

        @Override
        public void parseJSON(JSONObject jo) throws Exception {
            this.name = jo.getString("field_name");
            this.type = jo.getInt("field_type");
            this.index = jo.getInt("table_index");
        }
    }
}
