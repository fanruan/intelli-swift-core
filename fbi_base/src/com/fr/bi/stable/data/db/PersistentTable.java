package com.fr.bi.stable.data.db;

import com.fr.bi.stable.utils.program.BICollectionUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONCreator;
import com.fr.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 持久化的Table对象
 *
 * @author Daniel-pc
 * @author Connery
 */
public class PersistentTable implements  IPersistentTable {
    private List<PersistentField> fieldList = new ArrayList<PersistentField>();
    private String remark;
    private String schema;
    protected String tableName;

    public PersistentTable(String schema, String tableName, String tableNameRemark) {
        this.schema = schema;
        this.setTableName(tableName);
        this.remark = tableNameRemark;
    }

    /**
     * 添加列
     *
     * @param column 列
     */
    @Override
    public void addColumn(PersistentField column) {
        fieldList.add(column);
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
        Iterator<PersistentField> iter = fieldList.iterator();
        JSONArray ja = new JSONArray();
        while (iter.hasNext()) {
            PersistentField column = iter.next();
            ja.put(column.createJSON());
        }
        jo.put("fields", ja);
        return jo;
    }

    public List<PersistentField> getFieldList() {
        return BICollectionUtils.unmodifiedCollection(fieldList);
    }

    @Override
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

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
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
        return o2 instanceof IPersistentTable
                && ComparatorUtils.equals(schema, ((IPersistentTable) o2).getSchema())
                && ComparatorUtils.equals(tableName, ((IPersistentTable) o2).getTableName());
    }


    /**
     * 获取遍历column的list
     *
     * @return
     */
    @Override
    public int getFieldSize() {
        return this.fieldList.size();
    }

    /**
     * 获取第N个元素
     *
     * @param index list的index
     * @return
     */
    @Override
    public PersistentField getField(int index) {
        return this.fieldList.get(index);
    }

    @Override
    public PersistentField getField(String name) {
        for (int i = 0, len = fieldList.size(); i < len; i++) {
            PersistentField col = fieldList.get(i);
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
    @Override
    public int getFieldIndex(String name) {
        for (int i = 0, len = fieldList.size(); i < len; i++) {
            PersistentField col = fieldList.get(i);
            if (ComparatorUtils.equals(col.getFieldName(), name)) {
                return i;
            }
        }
        return -1;
    }
}