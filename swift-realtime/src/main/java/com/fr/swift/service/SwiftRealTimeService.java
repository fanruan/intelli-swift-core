package com.fr.swift.service;


import com.fr.swift.context.SwiftContext;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.recover.SegmentRecovery;
import com.fr.swift.stuff.RealTimeIndexingStuff;

/**
 * @author pony
 * @date 2017/10/10
 */
public class SwiftRealTimeService extends AbstractSwiftService {
    @Override
    public boolean start() throws SwiftServiceException {
        super.start();
        try {
            // 恢复所有realtime块
            SwiftContext.getInstance().getBean(SegmentRecovery.class).recoverAll();
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
        return true;
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.REAL_TIME;
    }

    public void index(RealTimeIndexingStuff stuff) {

    }
}
