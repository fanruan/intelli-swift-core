package com.fr.swift.config.conf;

import com.fr.config.ConfigContext;
import com.fr.config.DefaultConfiguration;
import com.fr.config.holder.factory.Holders;
import com.fr.config.holder.impl.ObjectMapConf;
import com.fr.swift.config.IMetaDataRCode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Handsome on 2018/3/30 0006 16:66
 */
public class RCodeConfig extends DefaultConfiguration {

    private final static String NAMESPACE = "rcode_config";
    private static RCodeConfig config = null;

    private ObjectMapConf<Map<String, IMetaDataRCode>> rCodeHolder = Holders.objMap(new HashMap<String, IMetaDataRCode>(), String.class, IMetaDataRCode.class);

    public static RCodeConfig getInstance() {
        if (null == config) {
            config = ConfigContext.getConfigInstance(RCodeConfig.class);
        }
        return config;
    }

    public Map<String, IMetaDataRCode> getAllRCodes() {
        return rCodeHolder.get();
    }

    public IMetaDataRCode getRCodeById(String tableId) {
        return (IMetaDataRCode) rCodeHolder.get(tableId);
    }

    public void putRCode(IMetaDataRCode rCode) {
        rCodeHolder.put(rCode.getTableId(), rCode);
    }

    public void removeRCode(String key) {
        rCodeHolder.remove(key);
    }

    public void modifyRCode(IMetaDataRCode rCode) {
        putRCode(rCode);
    }


    @Override
    public String getNameSpace() {
        return NAMESPACE;
    }
}
