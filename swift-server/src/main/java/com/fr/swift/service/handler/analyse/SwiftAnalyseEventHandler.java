package com.fr.swift.service.handler.analyse;

import com.fr.swift.event.base.AbstractAnalyseRpcEvent;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.rpc.client.AsyncRpcCallback;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.service.ClusterSwiftServerService;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.entity.ClusterEntity;
import com.fr.swift.service.handler.base.AbstractHandler;
import com.fr.third.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

/**
 * @author yee
 * @date 2018/6/8
 */
@Service
public class SwiftAnalyseEventHandler extends AbstractHandler<AbstractAnalyseRpcEvent> {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftAnalyseEventHandler.class);

    @Override
    public <S extends Serializable> S handle(AbstractAnalyseRpcEvent event) {
        Map<String, ClusterEntity> analyseServices = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.ANALYSE);
        if (null == analyseServices || analyseServices.isEmpty()) {
            throw new RuntimeException("Cannot find analyse service");
        }
        switch (event.subEvent()) {
            case SEGMENT_LOCATION:
                Iterator<Map.Entry<String, ClusterEntity>> iterator = analyseServices.entrySet().iterator();
                while (iterator.hasNext()) {
                    final long start = System.currentTimeMillis();
                    Map.Entry<String, ClusterEntity> entity = iterator.next();
                    String address = entity.getKey();
                    Class clazz = entity.getValue().getServiceClass();
                    runAsyncRpc(address, clazz, "updateSegmentInfo", event.getContent(), SegmentLocationInfo.UpdateType.ALL)
                            .addCallback(new AsyncRpcCallback() {
                                @Override
                                public void success(Object result) {
                                    LOGGER.info(String.format("Update segmentInfo cost: %d ms", System.currentTimeMillis() - start));
                                }

                                @Override
                                public void fail(Exception e) {
                                    LOGGER.error(e.getMessage(), e);
                                }
                            });
                }
        }
        return null;
    }
}
