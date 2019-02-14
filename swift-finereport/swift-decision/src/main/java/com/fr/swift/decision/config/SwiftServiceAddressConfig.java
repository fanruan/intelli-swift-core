package com.fr.swift.decision.config;

import com.fr.config.ConfigContext;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.service.SwiftServiceAddressService;
import com.fr.swift.SwiftContext;
import com.fr.swift.decision.config.base.SwiftAbstractObjectMapConfig;
import com.fr.swift.decision.config.unique.RpcServiceAddressUnique;


/**
 * @author yee
 * @date 2018/6/15
 */
public class SwiftServiceAddressConfig extends SwiftAbstractObjectMapConfig<RpcServiceAddressUnique> {
    private static SwiftServiceAddressConfig config;

    public SwiftServiceAddressConfig() {
        super(RpcServiceAddressUnique.class);
    }

    public static SwiftServiceAddressConfig getInstance() {
        if (null == config) {
            config = ConfigContext.getConfigInstance(SwiftServiceAddressConfig.class);
        }
        return config;
    }

    @Override
    public boolean addOrUpdate(String key, RpcServiceAddressUnique value) {
        super.addOrUpdate(key, value);
        return SwiftContext.get().getBean(SwiftServiceAddressService.class).addOrUpdateAddress(key, value.convert());
    }

    @Override
    public String getNameSpace() {
        return SwiftConfigConstants.FRConfiguration.SERVICE_ADDRESS_NAMESPACE;
    }
}
