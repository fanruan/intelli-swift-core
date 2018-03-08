package com.fr.swift.config.conf;

import com.fr.config.ConfigContext;
import com.fr.config.DefaultConfiguration;
import com.fr.config.holder.factory.Holders;
import com.fr.config.holder.impl.ObjectMapConf;
import com.fr.swift.config.IMetaData;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Lucifer
 * @Description:
 * @Date: Created in 2018-3-8
 * todo 事务处理
 */
public class MetaDataConfig extends DefaultConfiguration {

    private final static String NAMESPACE = "metadata_config";

    private static MetaDataConfig config = null;

    private ObjectMapConf<Map<String, IMetaData>> metaDataHolder =
            Holders.objMap(new HashMap<String, IMetaData>(), String.class, IMetaData.class);

    public static MetaDataConfig getInstance() {
        if (config == null) {
            config = ConfigContext.getConfigInstance(MetaDataConfig.class);
        }
        return config;
    }

    public Map<String, IMetaData> getAllMetaData() {
        return metaDataHolder.get();
    }

    public IMetaData getMetaDataByKey(String key) {
        return (IMetaData) metaDataHolder.get(key);
    }


    //todo key是存sourcekey还是metadata的name？
    public void addMetaData(IMetaData... metaDatas) {
        for (IMetaData metaData : metaDatas) {
            metaDataHolder.put(metaData.getTableName(), metaData);
        }
    }

    public void removeMetaData(String key) {
        metaDataHolder.remove(key);
    }

    public void modifyMetaData(IMetaData metaData) {
        IMetaData iMetaData = (IMetaData) metaDataHolder.get(metaData.getTableName());
        iMetaData.setSchema(metaData.getSchema());
        iMetaData.setRemark(metaData.getRemark());
        iMetaData.setTableName(metaData.getTableName());
        iMetaData.setFieldList(metaData.getFieldList());
    }

    @Override
    public String getNameSpace() {
        return NAMESPACE;
    }
}
