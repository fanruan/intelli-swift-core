package com.fr.bi.stable.data.db;

import com.fr.bi.stable.data.BITable;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * BI数据库Table对象
 *
 * @author Daniel-pc
 */
public class DBTable extends BITable {
    private List<BIColumn> columnList = new ArrayList<BIColumn>();
    private String remark;
    private String schema;

    public DBTable(String schema, String tableName, String tableNameRemark) {
        this.schema = schema;
        this.setTableName(tableName);
        this.remark = tableNameRemark;
    }

    /**
     * 添加列
     *
     * @param column 列
     */
    public void addColumn(BIColumn column) {
        columnList.add(column);
    }


    /**
     * 将Java对象转换成JSON对象
     *
     * @return json对象
     * @throws Exception
     */
    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("tableName", getTableName());
        jo.put("schema", getSchema());
        Iterator<BIColumn> iter = columnList.iterator();
        JSONArray ja = new JSONArray();
        while (iter.hasNext()) {
            BIColumn column = iter.next();
            ja.put(column.createJSON());
        }
        jo.put("fields", ja);
        return jo;
    }

    /**
     * 创建json对象
     *
     * @param connectionName 连接
     * @return json对象
     * @throws JSONException
     */
    public JSONObject asJson4TableTranslater(String connectionName) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("connection_name", connectionName)
                .put("table_name", this.getTableName())
                .put("table_name_text", this.getRemark());
        if (this.getSchema() != null) {
            jo.put("schema_name", this.getSchema());
        }

        JSONArray ja = new JSONArray();
        jo.put("translated_fields", ja);
        for (int i = 0, len = columnList.size(); i < len; i++) {
            ja.put(((BIColumn) columnList.get(i)).asJson4TableTranslater());
        }

        return jo;

    }

    public String getSchema() {
        return schema;
    }

    /**
     * 获取表名称
     *
     * @return
     */
    @Override
	public String getTableName() {
        return tableName;
    }

    /**
     * 设置表名称
     *
     * @param tableName 表名称
     */
    @Override
	public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getRemark() {
        return remark;
    }

    /**
     * hash值
     *
     * @return hash值
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((schema == null) ? 0 : schema.hashCode());
        result = prime * result
                + ((tableName == null) ? 0 : tableName.hashCode());
        return result;
    }

    /**
     * 只要判断表名即可表示相等
     */
    @Override
    public boolean equals(Object o2) {
        return o2 instanceof DBTable
                && ComparatorUtils.equals(schema, ((DBTable) o2).getSchema())
                && ComparatorUtils.equals(tableName, ((DBTable) o2).getTableName());
    }

    /**
     * 获取遍历column的list
     *
     * @return
     */
    public Iterator<BIColumn> getBIColumnIterator() {
        return this.columnList.iterator();
    }

    public BIColumn[] getColumnArray() {
        return this.columnList.toArray(new BIColumn[this.columnList.size()]);
    }

    /**
     * 获取遍历column的list
     *
     * @return
     */
    public int getBIColumnLength() {
        return this.columnList.size();
    }

    /**
     * 获取第N个元素
     *
     * @param index list的index
     * @return
     */
    public BIColumn getBIColumn(int index) {
        return (BIColumn) this.columnList.get(index);
    }

    public BIColumn getBIColumn(String name) {
        for (int i = 0, len = columnList.size(); i < len; i++) {
            BIColumn col = columnList.get(i);
            if (ComparatorUtils.equals(col.getFieldName(), name)) {
                return col;
            }
        }
        return null;
    }

    /**
     * 通过名字找到index
     *
     * @param name
     * @return
     */
    public int getBIColumnIndex(String name) {
        for (int i = 0, len = columnList.size(); i < len; i++) {
            BIColumn col = (BIColumn) columnList.get(i);
            if (ComparatorUtils.equals(col.getFieldName(), name)) {
                return i;
            }
        }
        return -1;
    }
}