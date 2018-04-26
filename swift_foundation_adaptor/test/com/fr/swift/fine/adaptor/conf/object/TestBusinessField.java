package com.fr.swift.fine.adaptor.conf.object;

import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.structure.bean.field.FineBusinessField;

/**
 * This class created on 2018-1-24 10:16:41
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class TestBusinessField implements FineBusinessField {

    String id;
    String name;

    public TestBusinessField(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void rename(String newName) {

    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public boolean isEnable() {
        return false;
    }

    @Override
    public boolean isUsable() {
        return false;
    }

    @Override
    public boolean isPrimaryKey() {
        return false;
    }

    @Override
    public String getTransferName() {
        return null;
    }

    @Override
    public void makeDisable() {

    }

    @Override
    public void makeUnDisable() {

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setType(int type) {

    }

    @Override
    public int getFieldGroupType() {
        return 0;
    }

    @Override
    public void setFieldGroupType(int i) {

    }

    @Override
    public FineBusinessField clone() {
        return null;
    }

    @Override
    public FineEngineType getEngineType() {
        return FineEngineType.Cube;
    }
}
