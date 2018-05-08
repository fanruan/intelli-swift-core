package com.fr.swift.config.conf;

import com.fr.config.ConfigContext;
import com.fr.config.DefaultConfiguration;
import com.fr.config.holder.factory.Holders;
import com.fr.config.holder.impl.ObjectMapConf;
import com.fr.swift.config.IMetaData;
import com.fr.swift.source.SourceKey;

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
    public void addMetaData(String sourceKey, IMetaData metaData) {
        metaDataHolder.put(sourceKey, metaData);
    }

    public void removeMetaData(String key) {
        metaDataHolder.remove(key);
    }

    public void modifyMetaData(String sourceKey, IMetaData metaData) {
        // 直接调添加方法好了，取出metaData再设置好像不能全覆盖
        addMetaData(sourceKey, metaData);
    }

    public boolean contains(SourceKey metaKey) {
        return metaDataHolder.containsKey(metaKey.getId());
    }

    @Override
    public String getNameSpace() {
        return NAMESPACE;
    }
}