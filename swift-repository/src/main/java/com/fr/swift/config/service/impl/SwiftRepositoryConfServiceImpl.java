package com.fr.swift.config.service.impl;

import com.fr.swift.config.bean.SwiftFileSystemConfig;
import com.fr.swift.config.convert.SwiftFileSystemConvert;
import com.fr.swift.config.service.SwiftConfigService;
import com.fr.swift.config.service.SwiftRepositoryConfService;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

/**
 * @author yee
 * @date 2018/7/6
 */
@Service("swiftRepositoryConfService")
public class SwiftRepositoryConfServiceImpl implements SwiftRepositoryConfService {
    private static final SwiftFileSystemConvert CONVERT = new SwiftFileSystemConvert();
    @Autowired
    private SwiftConfigService configService;

    @Override
    public SwiftFileSystemConfig getCurrentRepository() {
        return configService.getConfigBean(CONVERT);
    }

    @Override
    public boolean setCurrentRepository(SwiftFileSystemConfig config) {
        return configService.updateConfigBean(CONVERT, config);
    }
}
