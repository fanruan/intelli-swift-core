package com.fr.swift.decision.config;

import com.fr.config.ConfigContext;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.base.impl.SwiftAbstractObjectMapConfig;
import com.fr.swift.config.bean.unique.RpcServiceAddressUnique;
import com.fr.swift.config.service.SwiftServiceAddressService;
import com.fr.swift.context.SwiftContext;


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
        return SwiftContext.getInstance().getBean(SwiftServiceAddressService.class).addOrUpdateAddress(key, value.convert());
    }

    @Override
    public String getNameSpace() {
        return SwiftConfigConstants.FRConfiguration.SERVICE_ADDRESS_NAMESPACE;
    }
}
