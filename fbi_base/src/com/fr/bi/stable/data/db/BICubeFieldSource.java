package com.fr.bi.stable.data.db;

import com.fr.bi.common.constant.BIValueConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.BIBasicField;
import com.fr.bi.stable.data.source.ICubeTableSource;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;


/**
 * 数据库列转成的基础列, 表示生成cube时的字段信息
 * Created by GUY on 2015/4/10.
 */
public class BICubeFieldSource extends BIBasicField {

    private static final long serialVersionUID = 2399880587121141814L;
    protected String fieldName = StringUtils.EMPTY;

    protected int fieldType;
    protected int fieldSize;
    protected boolean isUsable;
    private int classType;
    private ICubeTableSource cubeTableSource;

    public static BICubeFieldSource createEmpty() {
        return new BICubeFieldSource("", "", -1, -1);
    }

    public BICubeFieldSource(String id, String fieldName, int classType, int fieldSize) {

        this.classType = classType;
    }

    public static BICubeFieldSource getBiEmptyField() {
        return new BICubeFieldSource(BIValueConstant.EMPTY, BIValueConstant.EMPTY, -1, -1);
    }

    /**
     * 返回字段对应的java类
     *
     * @return
     */
    public int getClassType() {
        return classType;
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