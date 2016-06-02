package com.finebi.cube.conf.table;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.json.JSONObject;

import java.util.List;

/**
 * 工具类，在DataSource记录下来Id和BusinessTable对应TableSource
 * 的情况下，通过Helper类实现只有tableId的情况下，来获得table其他属性。
 *
 * 使用方法：必须提供一个带Id的BusinessTable实现，并且此BusinessTable必须不是
 * 一个BIBusinessTableGetter。
 * 此外只读不可写
 * This class created on 2016/5/26.
 *
 * @author Connery
 * @since 4.0
 */
public class BIBusinessTableGetter implements BusinessTable {
    private BusinessTable table;

    public BIBusinessTableGetter() {
    }

    public void setTable(BusinessTable table) {
        if (table.getID() != null && !(table instanceof BIBusinessTableGetter)) {
            this.table = table;
        } else {
            throw BINonValueUtils.beyondControl();
        }
    }

    public BIBusinessTableGetter(BusinessTable table) {
        setTable(table);
    }

    public BIBusinessTableGetter(BITableID tableID) {
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


}
