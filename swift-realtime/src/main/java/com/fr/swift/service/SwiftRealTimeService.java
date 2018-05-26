package com.fr.swift.service;


import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.segment.recover.SwiftSegmentRecovery;
import com.fr.swift.stuff.RealTimeIndexingStuff;

/**
 * @author pony
 * @date 2017/10/10
 */
public class SwiftRealTimeService extends AbstractSwiftService {
    @Override
    public boolean start() throws SwiftServiceException {
        super.start();
        // 恢复所有realtime块
        SwiftSegmentRecovery.getInstance().recoverAll();
        return true;
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.REAL_TIME;
    }

    public void index(RealTimeIndexingStuff stuff) {

    }
}
