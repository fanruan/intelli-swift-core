package com.fr.swift.config.unique;

import com.fr.config.holder.Conf;
import com.fr.config.holder.factory.Holders;
import com.fr.config.holder.impl.ObjectColConf;
import com.fr.config.utils.UniqueKey;
import com.fr.stable.StringUtils;
import com.fr.swift.config.IMetaData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Author: Lucifer
 * @Description:
 * @Date: Created in 2018-3-8
 */
public class SwiftMetaDataUnique extends UniqueKey implements IMetaData<MetaDataColumnUnique> {

    private final static String NAMESPACE = "metadata";

    private Conf<String> schema = Holders.simple(StringUtils.EMPTY);
    private Conf<String> tableName = Holders.simple(StringUtils.EMPTY);
    private Conf<String> remark = Holders.simple(StringUtils.EMPTY);
    private ObjectColConf<Collection<MetaDataColumnUnique>> fieldList =
            Holders.objCollection(new ArrayList<MetaDataColumnUnique>(), MetaDataColumnUnique.class);

    public SwiftMetaDataUnique() {
    }

    public SwiftMetaDataUnique(String schema, String tableName, String remark, List<MetaDataColumnUnique> fieldList) {
        this.setSchema(schema);
        this.setTableName(tableName);
        this.setRemark(remark);
        this.setFieldList(fieldList);
    }

    @Override
    public String getSchema() {
        return schema.get();
    }

    @Override
    public void setSchema(String schema) {
        this.schema.set(schema);
    }

    @Override
    public String getTableName() {
        return tableName.get();
    }

    @Override
    public void setTableName(String tableName) {
        this.tableName.set(tableName);
    }

    @Override
    public String getRemark() {
        return remark.get();
    }

    @Override
    public void setRemark(String remark) {
        this.remark.set(remark);
    }

    @Override
    public List<MetaDataColumnUnique> getFieldList() {
        return (List<MetaDataColumnUnique>) fieldList.get();
    }

    @Override
    public void setFieldList(List<MetaDataColumnUnique> fieldList) {
        this.fieldList.set(fieldList);
    }


}
