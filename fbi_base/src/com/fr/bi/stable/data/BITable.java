package com.fr.bi.stable.data;


import com.fr.bi.common.BICoreWrapper;
import com.fr.bi.exception.BIAmountLimitUnmetException;
import com.fr.bi.stable.data.source.ITableSource;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLable;
import com.fr.stable.xml.XMLableReader;

import javax.activation.UnsupportedDataTypeException;
import java.io.Serializable;

/**
 * BI的Table基本类型。其他Table都继承此类型
 * TODO clone方法
 * Created by Connery on 2015/12/15.
 */
public class BITable implements Serializable, XMLable, JSONTransform, Table {

    /**
     *
     */
    private static final long serialVersionUID = 9196168550564527877L;

    public static BITable BI_EMPTY_TABLE() {
        return new BITable("__FINE_BI_EMPTY__");
    }

    protected String tableName;
    protected transient BITableID ID;
    protected ITableSource tableSource;

    public BITable(String id) {
        this(id, null);
    }

    public BITable(String id, String tableName) {
        this.ID = new BITableID(id);
        this.tableName = tableName;
    }

    public BITable(BITableID table) {
        this(table.getIdentityValue());
    }

    public BITable() {
    }

    public BITable(Table table) {
        this(table.getID().getIdentityValue(), table.getTableName());
    }


    @Override
    public BITableID getID() {
        return ID;
    }

    @Override
    public void setID(BITableID id) {
        this.ID = id;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    @Override
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        BITable cloned = (BITable) super.clone();
        cloned.setID((BITableID) ID.clone());
        return cloned;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Table)) {
            return false;
        }

        BITable biTable = (BITable) o;

        if (!ComparatorUtils.equals(ID, biTable.ID)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        return "BITable{" +
                "tableName='" + tableName + '\'' +
                ", ID=" + ID +
                '}';
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
        jo.put("id", ID.getIdentityValue());
        return jo;
    }


    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("id")) {
            this.setID(generateID(jo.getString("id")));
        }
    }

    protected BITableID generateID(String id) {
        return new BITableID(id);
    }

    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isAttr()) {
            ID = new BITableID(reader.getAttrAsString("id", StringUtils.EMPTY));
        }
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.attr("id", ID.getIdentityValue());
    }

    @Override
    public int hashCode() {
        int result = tableName != null ? tableName.hashCode() : 0;
        result = 31 * result + (ID != null ? ID.hashCode() : 0);
        return result;
    }

    public class BITableCore extends BICoreWrapper {
        public BITableCore(Object... attributes) throws UnsupportedDataTypeException, BIAmountLimitUnmetException {
            super(attributes);
        }
    }
}