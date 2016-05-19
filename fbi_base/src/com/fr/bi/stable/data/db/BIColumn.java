package com.fr.bi.stable.data.db;

import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.data.key.IColumn;
import com.fr.bi.stable.utils.BIDBUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * 保存列的信息
 *
 * @author Daniel-pc
 */
public class BIColumn extends BIDBObject implements IColumn {
    /**
     *
     */
    public static final int DEFALUTSCALE = 15;

    public static final int DEFALUTCOLUMN_SIZE = 100;

    public String getRemark() {
        return remark;
    }

    private int type;
    private String columnName;
    private String remark;
    private boolean isPrimaryKey;
    private int column_size;
    private int bitype;
    //小数位数
    private int scale = DEFALUTSCALE;

    public BIColumn(String columnName, String remark, int type, boolean isPrimaryKey, int column_size, int scale) {
        this.setType(type);
        this.setColumnName(columnName);
        this.bitype = BIDBUtils.sqlType2BI(type, column_size, scale);
        this.remark = remark;
        this.isPrimaryKey = isPrimaryKey;
        //FIXME 临时处理大于8K长度的字段，比如sqlserver TEXT; 截取前255
        this.column_size = column_size > 8000 ? 255 : column_size;
        this.scale = scale;
    }

    public BIColumn(String columnName, String remark, int type, int column_size, int scale) {
        this(columnName, remark, type, false, column_size, scale);
    }

    public BIColumn(String columnName, int type, int column_size) {
        this(columnName, columnName, type, column_size, DEFALUTSCALE);
    }

    public BIColumn(String columnName, int type) {
        this(columnName, columnName, type, DEFALUTCOLUMN_SIZE, DEFALUTSCALE);
    }

    public BIColumn() {
    }

    public int getColumnSize() {
        return column_size;
    }

    /**
     * 是否为主键
     *
     * @return 是否为主键
     */
    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public void setPrimaryKey(boolean isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
    }

    /**
     * 列数据的类型
     *
     * @return java.sql.Types
     */
    @Override
    public int getType() {
        return type;
    }

    /**
     * 设置列数据的类型
     *
     * @param type java.sql.Types
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * 列数据的BI类型
     *
     * @return java.sql.Types
     */
    public int getBIType() {
        return bitype;
    }

    public void setColumn_size(int column_size) {
        this.column_size = column_size;
    }

    /**
     * 获取列名称
     *
     * @return 列名称
     */
    @Override
    public String getFieldName() {
        return columnName;
    }

    @Override
    public boolean isUsable() {
        return true;
    }

    /**
     * 设置列名称
     *
     * @param columnName
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * 创建JSON对象
     *
     * @return 创建的json对象
     */
    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("text", this.getFieldName());
        jo.put("value", this.getFieldName());
        jo.put("type", this.getType());
        jo.put("biColumnType", BIDBUtils.sqlType2BI(this.getType(), column_size, scale));
        jo.put("scale", scale);
        jo.put("isPrimaryKey", isPrimaryKey());
        jo.put("column_size", column_size);
        return jo;
    }

    /**
     * 创建转义名json对象
     *
     * @return 创建的json对象
     * @throws JSONException
     */
    public JSONObject asJson4TableTranslater() throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("field_name", this.getFieldName()).put("field_name_text", this.remark);
        return jo;
    }

    /**
     * 解析json对象
     *
     * @param jo json对象
     */
    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("value")) {
            this.setColumnName(jo.getString("value"));
        }
        if (jo.has("type")) {
            this.setType(jo.getInt("type"));
        }
        if (jo.has("isPrimaryKey")) {
            this.setType(jo.getInt("isPrimaryKey"));
        }
        if (jo.has("column_size")) {
            this.column_size = jo.getInt("column_size");
        }
        if (jo.has("scale")) {
            this.scale = jo.getInt("scale");
        }
    }

    public int getScale() {
        return scale;
    }

    public DBField toDBField(Table table) {

        return new DBField(table.getID().getIdentityValue(), getFieldName(), BIDBUtils.checkColumnClassTypeFromSQL(getType(), getColumnSize(), getScale()), getColumnSize());
    }

    /**
     * 克隆方法
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * equals方法
     */
    @Override
    public boolean equals(Object o2) {
        return o2 instanceof BIColumn
                && ComparatorUtils.equals(((BIColumn) o2).getFieldName(), this.getFieldName())
                && ((BIColumn) o2).getType() == this.getType()
                && ((BIColumn) o2).isPrimaryKey() == this.isPrimaryKey();
    }

    public boolean canSetUseable() {
        return true;
    }
}