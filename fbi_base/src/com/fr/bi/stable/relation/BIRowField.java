package com.fr.bi.stable.relation;

import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.*;
import com.fr.bi.stable.data.db.DBField;
import com.fr.json.JSONObject;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

/**
 * This class created on 2016/3/28.
 * 位图索引通过主外键的关系，而建立了基于行号的关联。
 * 该对象值的就是某张表的行号字段。
 *
 * @author Connery
 * @since 4.0
 */
public final class BIRowField extends DBField {
    public static BIRowField rowNumberField = new BIRowField();

    private BIRowField() {
        super(BITable.BI_EMPTY_TABLE().getID().getIdentityValue(), "row_number_field", DBConstant.CLASS.ROW, 2);
    }

    @Override
    public void setTableBelongTo(Table tableBelongTo) {
        throw new UnsupportedOperationException();
    }


    @Override
    public BITableID getTableID() {
        throw new UnsupportedOperationException();
    }


    @Override
    public void setFieldName(String fieldName) {
        throw new UnsupportedOperationException();

    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new UnsupportedOperationException();

    }


    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        throw new UnsupportedOperationException();

    }

    @Override
    public JSONObject createJSON() throws Exception {
        throw new UnsupportedOperationException();

    }


    @Override
    public void writeXML(XMLPrintWriter writer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void readXML(XMLableReader reader) {
        throw new UnsupportedOperationException();
    }
}
