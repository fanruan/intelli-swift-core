package com.fr.swift.config.service.impl;

import com.fr.swift.config.SwiftUseZipConfig;
import com.fr.swift.config.service.SwiftZipService;
import com.fr.third.springframework.stereotype.Service;

/**
 * @author yee
 * @date 2018/6/27
 */
@Service("swiftZipService")
public class SwiftZipServiceImpl implements SwiftZipService {

    private SwiftUseZipConfig config = SwiftUseZipConfig.getInstance();

    @Override
    public boolean isZip() {
        return config.isUseZip();
    }

    @Override
    public void setZip(boolean isZip) {
        config.setUseZip(isZip);
    }
}
