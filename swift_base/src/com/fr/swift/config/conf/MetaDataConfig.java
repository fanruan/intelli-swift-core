package com.fr.swift.config.conf;

import com.fr.config.ConfigContext;
import com.fr.config.Configuration;
import com.fr.config.DefaultConfiguration;
import com.fr.config.holder.factory.Holders;
import com.fr.config.holder.impl.ObjectMapConf;
import com.fr.swift.config.IMetaData;
import com.fr.transaction.Configurations;
import com.fr.transaction.Worker;

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

    /**
     * sourceKey -> metadata
     * @param sourceKey
     * @param metaData
     */
    public void addMetaData(final String sourceKey, final IMetaData metaData) {
        Configurations.update(new Worker() {
            @Override
            public void run() {
                metaDataHolder.put(sourceKey, metaData);
            }

            @Override
            public Class<? extends Configuration>[] targets() {
                return new Class[] { MetaDataConfig.class };
            }
        });

    }

    public void removeMetaData(final String key) {

        Configurations.update(new Worker() {
            @Override
            public void run() {
                metaDataHolder.remove(key);
            }

            @Override
            public Class<? extends Configuration>[] targets() {
                return new Class[] { MetaDataConfig.class };
            }
        });
    }

    public void modifyMetaData(final String sourceKey, final IMetaData metaData) {
        Configurations.update(new Worker() {
            @Override
            public void run() {
                IMetaData iMetaData = (IMetaData) metaDataHolder.get(sourceKey);
                iMetaData.setSchema(metaData.getSchema());
                iMetaData.setRemark(metaData.getRemark());
                iMetaData.setTableName(metaData.getTableName());
                iMetaData.setFieldList(metaData.getFieldList());
            }

            @Override
            public Class<? extends Configuration>[] targets() {
                return new Class[] { MetaDataConfig.class };
            }
        });
    }

    @Override
    public String getNameSpace() {
        return NAMESPACE;
    }
}
