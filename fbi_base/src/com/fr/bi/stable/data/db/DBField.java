package com.fr.bi.stable.data.db;

import com.fr.bi.common.constant.BIValueConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.BIBasicField;
import com.fr.json.JSONObject;


/**
 * 数据库列转成的基础列, 表示生成cube时的字段信息
 * Created by GUY on 2015/4/10.
 */
public class DBField extends BIBasicField {
    /**
     *
     */
    private static final long serialVersionUID = 2399880587121141814L;
    private int classType;

    public static DBField createEmpty() {
        return new DBField("", "", -1, -1);
    }

    public DBField(String id, String fieldName, int classType, int fieldSize) {
        super(id, fieldName, classType, fieldSize);
        this.classType = classType;
    }

    public static DBField getBiEmptyField() {
        return new DBField(BIValueConstant.EMPTY, BIValueConstant.EMPTY, -1, -1);
    }

    /**
     * 返回字段对应的java类
     *
     * @return
     */
    public int getClassType() {
        return classType;
    }

    /**
     * 设置字段对应的java类
     *
     * @param classType
     */
    public void setClassType(int classType) {
        this.classType = classType;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = super.createJSON();
        return jo.put("class_type", classType);
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        super.parseJSON(jo);
        classType = jo.optInt("class_type", DBConstant.CLASS.STRING);
    }

    public boolean hasSubField() {
        return (getFieldType() == DBConstant.COLUMN.DATE);
    }
}