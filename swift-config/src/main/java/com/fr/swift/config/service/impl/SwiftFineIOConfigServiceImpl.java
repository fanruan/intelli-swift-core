package com.fr.swift.config.service.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.bean.FineIOConnectorConfig;
import com.fr.swift.config.convert.FineIOConfigConvert;
import com.fr.swift.config.service.SwiftConfigService;
import com.fr.swift.config.service.SwiftFineIOConnectorService;

/**
 * @author yee
 * @date 2018-12-20
 */
@SwiftBean(name = "swiftFineIOConfigServiceImpl")
public class SwiftFineIOConfigServiceImpl implements SwiftFineIOConnectorService {
    private static final FineIOConfigConvert CONFIG_CONVERT = new FineIOConfigConvert();
    private SwiftConfigService configService = SwiftContext.get().getBean(SwiftConfigService.class);

    @Override
    public FineIOConnectorConfig getCurrentConfig() {
        return configService.getConfigBean(CONFIG_CONVERT);
    }

    @Override
    public void setCurrentConfig(FineIOConnectorConfig config) {
        FineIOConnectorConfig current = getCurrentConfig();
        if (null != config && !config.equals(current)) {
            configService.deleteConfigBean(CONFIG_CONVERT, current);
            configService.updateConfigBean(CONFIG_CONVERT, config);
        }
    }

}
