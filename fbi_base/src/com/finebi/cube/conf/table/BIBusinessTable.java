package com.finebi.cube.conf.table;


import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.program.BICollectionUtils;
import com.fr.json.JSONObject;

import java.util.List;

/**
 * This class created on 2016/5/21.
 *
 * @author Connery
 * @since 4.0
 */
public class BIBusinessTable implements BusinessTable {

    protected BITableID ID;
    protected String tableName;
    protected List<BusinessField> fields;
    protected CubeTableSource source;

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
    public List<BusinessField> getFields() {
        return BICollectionUtils.unmodifiedCollection(fields);
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

    @Override
    public CubeTableSource getTableSource() {
        return source;
    }

    @Override
    public JSONObject createJSONWithFieldsInfo(ICubeDataLoader loader) {
        return null;
    }
}