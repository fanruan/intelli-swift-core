package com.fr.swift.config.unique;

import com.fr.config.holder.Conf;
import com.fr.config.holder.factory.Holders;
import com.fr.config.holder.impl.ObjectMapConf;
import com.fr.config.utils.UniqueKey;
import com.fr.stable.StringUtils;
import com.fr.swift.config.IMetaData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private ObjectMapConf<Map<Integer, MetaDataColumnUnique>> fieldList = Holders.objMap(new HashMap<Integer, MetaDataColumnUnique>(), Integer.class, MetaDataColumnUnique.class);
    public SwiftMetaDataUnique() {
    }

    public SwiftMetaDataUnique(String schema, String tableName, String remark, List<MetaDataColumnUnique> fieldList) {
        this.setSchema(schema);
        this.setTableName(tableName);
        this.setRemark(remark);
        this.setFields(fieldList);
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
    public List<MetaDataColumnUnique> getFields() {
        // 不能直接用fieldList.get(i)
        Map<Integer, MetaDataColumnUnique> map = fieldList.get();
        int size = map.size();
        List<MetaDataColumnUnique> target = new ArrayList<MetaDataColumnUnique>();
        for (int i = 0; i < size; i++) {
            target.add(map.get(i));
        }
        return target;
    }

    @Override
    public void setFields(List<MetaDataColumnUnique> fields) {
        for (int i = 0, len = fields.size(); i < len; i++) {
            this.fieldList.put(i, fields.get(i));
        }
    }


}
