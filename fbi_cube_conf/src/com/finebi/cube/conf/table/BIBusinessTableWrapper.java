package com.finebi.cube.conf.table;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.json.JSONObject;

import java.util.List;

/**
 * 工具类
 * This class created on 2016/5/26.
 *
 * @author Connery
 * @since 4.0
 */
public class BIBusinessTableWrapper implements BusinessTable {
    private BusinessTable table;

    public BIBusinessTableWrapper() {
    }

    public void setTable(BusinessTable table) {
        if (table.getID() != null && !(table instanceof BIBusinessTableWrapper)) {
            this.table = table;
        } else {
            throw BINonValueUtils.beyondControl();
        }
    }

    public BIBusinessTableWrapper(BusinessTable table) {
        setTable(table);
    }

    public BIBusinessTableWrapper(BITableID tableID) {
        this(new BIBusinessTable(tableID));
    }

    @Override
    public BITableID getID() {
        return getInnerTableSource().getID();
    }

    private BusinessTable getInnerTableSource() {
        return BusinessTableHelper.getBusinessTable(table.getID());
    }

    @Override
    public List<BusinessField> getFields() {
        return getInnerTableSource().getFields();
    }

    @Override
    public void setSource(CubeTableSource source) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getTableName() {
        return getInnerTableSource().getTableName();
    }

    @Override
    public CubeTableSource getTableSource() {
        return getInnerTableSource().getTableSource();
    }

    @Override
    public void setFields(List<BusinessField> fields) {
        throw new UnsupportedOperationException();

    }

    @Override
    public JSONObject createJSONWithFieldsInfo(ICubeDataLoader loader) throws Exception {
        return getInnerTableSource().createJSONWithFieldsInfo(loader);
    }

    @Override
    public JSONObject createJSON() throws Exception {
        return getInnerTableSource().createJSON();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public void parseJSON(JSONObject jsonObject) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getUsedFieldNames() {
        return null;
    }

    @Override
    public void setUsedFieldNames(List<String> usedFieldNames) {

    }
}
