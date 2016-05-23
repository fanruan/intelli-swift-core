package com.finebi.cube.conf.table;


import com.fr.bi.stable.data.BITableID;
import com.fr.json.JSONObject;

/**
 * Created by GUY on 2015/3/3.
 */
public class BIBusinessTable implements IBusinessTable {

    protected BITableID ID;
    private String tableName;

    public BIBusinessTable(BITableID ID) {
        this(ID, "");
    }

    public BIBusinessTable(BITableID ID, String tableName) {
        this.ID = new BITableID(ID);
        this.tableName = tableName;
    }

    public BIBusinessTable(String ID, String tableName) {
        this(new BITableID(ID), tableName);
    }

    @Override
    public BITableID getID() {
        return ID;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        return null;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    @Override
    public void parseJSON(JSONObject jsonObject) throws Exception {

    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BIBusinessTable)) return false;

        BIBusinessTable that = (BIBusinessTable) o;

        return !(ID != null ? !ID.equals(that.ID) : that.ID != null);

    }

    @Override
    public int hashCode() {
        return ID != null ? ID.hashCode() : 0;
    }
}