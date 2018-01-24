package com.fr.swift.fine.adaptor.conf.object;

import com.finebi.base.constant.BaseConstant;
import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.exception.dao.FineDAOException;
import com.finebi.conf.internalimp.table.FineDBBusinessTable;
import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.fr.general.ComparatorUtils;
import com.fr.swift.fine.adaptor.conf.creater.TestFieldCreator;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018-1-24 10:12:05
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class TestBusinessTable extends FineDBBusinessTable {

    private String id;
    private String name;
    private List<FineBusinessField> fieldList = new ArrayList<FineBusinessField>();

    public TestBusinessTable(String id, String name) {
        this.id = id;
        this.name = name;
//        fieldList.add(TestFieldCreator.fieldA);
//        fieldList.add(TestFieldCreator.fieldB);
//        fieldList.add(TestFieldCreator.fieldC);
//        fieldList.add(TestFieldCreator.fieldD);
//        fieldList.add(TestFieldCreator.fieldE);
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<FineBusinessField> getFields() {
        return fieldList;
    }

    @Override
    public void addField(FineBusinessField field) {

    }

    @Override
    public void setId(String tableId) {
        this.id = tableId;
    }

    @Override
    public void updateTableName(String newName) {

    }

    @Override
    public void resetFields() throws Exception {

    }

    @Override
    public FineBusinessField getFieldByFieldId(String fieldId) {
        for (FineBusinessField fineBusinessField : fieldList) {
            if (ComparatorUtils.equals(fieldId, fineBusinessField.getId())) {
                return fineBusinessField;
            }
        }
        return null;
    }

    @Override
    public void notifyEngine() {

    }

    @Override
    public int getType() {
        return BaseConstant.TABLETYPE.DB;
    }

    @Override
    public long getLastUpdateTime() {
        return 0;
    }

    @Override
    public BIDetailTableResult getPreviewData(int rowCount) throws FineDAOException {
        return null;
    }

    @Override
    public List<FineBusinessField> getEngineFields() throws Exception {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public FineEngineType getEngineType() {
        return FineEngineType.Cube;
    }
}
