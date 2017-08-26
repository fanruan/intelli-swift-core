package com.fr.bi.stable.data.db;

import com.fr.bi.common.persistent.annotation.PersistNameHistory;
import com.fr.bi.stable.data.key.IPersistentField;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.data.tablesource.FineCubeTable;
import com.fr.bi.stable.utils.BIDBUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;

/**
 * 保存列的信息
 */
public class PersistentField implements IPersistentField {

    public static final int DEFAULT_SCALE = 15;

    private static final int DEFAULT_COLUMN_SIZE = 100;

    private static final long serialVersionUID = -3638876557665551219L;

    private int sqlType;
    private String columnName;
    private String remark;
    private boolean isPrimaryKey;
    @PersistNameHistory(historyNames = {"column_size"})
    private int columnSize;
    private int biType;
    private boolean canSetUsable;

    //小数位数
    private int scale = DEFAULT_SCALE;

    public PersistentField(String columnName, String remark, int type, boolean isPrimaryKey, int columnSize, int scale) {
        this.setSqlType(type);
        this.setColumnName(columnName);
        this.biType = BIDBUtils.sqlType2BI(type, columnSize, scale);
        this.remark = remark;
        this.isPrimaryKey = isPrimaryKey;
        //FIXME 临时处理大于8K长度的字段，比如sqlserver TEXT; 截取前255
        this.columnSize = columnSize > 8000 ? 255 : columnSize;
        this.scale = scale;
    }

    public PersistentField(String columnName, String remark, int type, int columnSize, int scale) {
        this(columnName, remark, type, false, columnSize, scale);
    }

    public PersistentField(String columnName, int type, int columnSize) {
        this(columnName, columnName, type, columnSize, DEFAULT_SCALE);
    }

    public PersistentField(String columnName, int type) {
        this(columnName, columnName, type, DEFAULT_COLUMN_SIZE, DEFAULT_SCALE);
    }

    public PersistentField(String columnName, int type, int columnSize, int scale) {
        this(columnName, columnName, type, columnSize, scale);
    }

    public PersistentField() {
    }

    public String getRemark() {
        return remark;
    }

    @Override
    public int getColumnSize() {
        return columnSize;
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
    public int getSqlType() {
        return sqlType;
    }

    /**
     * 设置列数据的类型
     *
     * @param type java.sql.Types
     */
    public void setSqlType(int type) {
        this.sqlType = type;
    }

    /**
     * 列数据的BI类型
     *
     * @return java.sql.Types
     */
    public int getBIType() {
        return biType;
    }

    public void setColumnSize(int columnSize) {
        this.columnSize = columnSize;
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
        JSONObject jo = JSONObject.create();
        jo.put("text", this.getFieldName());
        jo.put("value", this.getFieldName());
        jo.put("type", this.getSqlType());
        jo.put("biColumnType", BIDBUtils.sqlType2BI(this.getSqlType(), columnSize, scale));
        jo.put("scale", scale);
        jo.put("isPrimaryKey", isPrimaryKey());
        jo.put("columnSize", columnSize);
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
            this.setSqlType(jo.getInt("type"));
        }
        if (jo.has("isPrimaryKey")) {
            this.setSqlType(jo.getInt("isPrimaryKey"));
        }
        if (jo.has("columnSize")) {
            this.columnSize = jo.getInt("columnSize");
        }
        if (jo.has("scale")) {
            this.scale = jo.getInt("scale");
        }
    }

    @Override
    public int getScale() {
        return scale;
    }

    public BICubeFieldSource toDBField(CubeTableSource tableBelongTo) {
        return new BICubeFieldSource(tableBelongTo, getFieldName(), BIDBUtils.checkColumnClassTypeFromSQL(getSqlType(), getColumnSize(), getScale()), getColumnSize());
    }

    public FineCubeField toDBField(FineCubeTable tableBelongTo) {
        return new BIFineCubeField(tableBelongTo, getFieldName(), BIDBUtils.checkColumnClassTypeFromSQL(getSqlType(), getColumnSize(), getScale()), getColumnSize());
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
        return o2 instanceof PersistentField
                && ComparatorUtils.equals(((PersistentField) o2).getFieldName(), this.getFieldName())
                && ((PersistentField) o2).getSqlType() == this.getSqlType()
                && ((PersistentField) o2).isPrimaryKey() == this.isPrimaryKey();
    }

    public boolean canSetUsable() {
        return canSetUsable;
    }

}
