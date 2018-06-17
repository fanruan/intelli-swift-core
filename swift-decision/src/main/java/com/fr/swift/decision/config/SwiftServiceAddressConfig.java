package com.fr.swift.decision.config;

import com.fr.config.ConfigContext;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.base.impl.SwiftAbstractObjectMapConfig;
import com.fr.swift.config.bean.RpcServiceAddress;


/**
 * @author yee
 * @date 2018/6/15
 */
public class SwiftServiceAddressConfig extends SwiftAbstractObjectMapConfig<RpcServiceAddress> {
    private static SwiftServiceAddressConfig config;

    public SwiftServiceAddressConfig() {
        super(RpcServiceAddress.class);
    }

    public static SwiftServiceAddressConfig getInstance() {
        if (null == config) {
            config = ConfigContext.getConfigInstance(SwiftServiceAddressConfig.class);
        }
        return config;
    }

    @Override
    public String getNameSpace() {
        return SwiftConfigConstants.FRConfiguration.SERVICE_ADDRESS_NAMESPACE;
    }
}
