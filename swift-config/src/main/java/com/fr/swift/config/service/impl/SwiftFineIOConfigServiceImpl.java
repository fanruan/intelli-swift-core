package com.fr.swift.config.service.impl;

import com.fr.swift.config.bean.FineIOConnectorConfig;
import com.fr.swift.config.convert.FineIOConfigConvert;
import com.fr.swift.config.service.SwiftConfigService;
import com.fr.swift.config.service.SwiftFineIOConnectorService;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

/**
 * @author yee
 * @date 2018-12-20
 */
@Service
public class SwiftFineIOConfigServiceImpl implements SwiftFineIOConnectorService {
    private static final FineIOConfigConvert CONFIG_CONVERT = new FineIOConfigConvert();
    @Autowired
    private SwiftConfigService configService;

    @Override
    public FineIOConnectorConfig getCurrentConfig() {
        return configService.getConfigBean(CONFIG_CONVERT);
    }

    @Override
    public void setCurrentConfig(FineIOConnectorConfig config) {
        configService.deleteConfigBean(CONFIG_CONVERT, getCurrentConfig());
        configService.updateConfigBean(CONFIG_CONVERT, config);
    }

}
