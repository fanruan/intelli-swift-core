package com.fr.swift.service;

import com.fr.swift.annotation.SwiftService;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.segment.SegmentKey;

import java.util.Map;

/**
 * @author Heng.J
 * @date 2020/10/27
 * @description
 * @since swift-1.2.0
 */
@SwiftService(name = "migrate")
@SwiftBean(name = "migrate")
public class SwiftMigrateService extends AbstractSwiftService implements MigrateService {

    @Override
    public ServiceType getServiceType() {
        return ServiceType.MIGRATE;
    }

    @Override
    public Boolean appointMigrate(Map<SegmentKey, Map<String, byte[]>> segments, String location) {
        return false;
    }

    @Override
    public boolean start() throws SwiftServiceException {
        return super.start();
    }

    @Override
    public boolean shutdown() throws SwiftServiceException {
        return super.shutdown();
    }
}
