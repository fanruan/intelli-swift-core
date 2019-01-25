package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.annotation.SwiftService;
import com.fr.swift.basics.annotation.ProxyService;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.db.Where;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.operator.delete.WhereDeleter;
import com.fr.swift.source.SourceKey;
import com.fr.swift.task.service.ServiceTaskExecutor;
import com.fr.swift.task.service.ServiceTaskType;
import com.fr.swift.task.service.SwiftServiceCallable;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author anchore
 * @date 2019/1/22
 */
@SwiftService(name = "swiftDeleteService")
@ProxyService(DeleteService.class)
@SwiftBean(name = "swiftDeleteService")
public class SwiftDeleteService extends AbstractSwiftService implements DeleteService, Serializable {

    private static final long serialVersionUID = 1;

    private transient SwiftSegmentManager segmentManager;

    private transient ServiceTaskExecutor taskExecutor;

    @Override
    public boolean start() throws SwiftServiceException {
        super.start();
        segmentManager = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);
        taskExecutor = SwiftContext.get().getBean(ServiceTaskExecutor.class);

        return true;
    }

    @Override
    public boolean shutdown() throws SwiftServiceException {
        super.shutdown();
        segmentManager = null;
        taskExecutor = null;

        return true;
    }

    @Override
    public boolean delete(final SourceKey tableKey, final Where where) {
        try {
            taskExecutor.submit(new SwiftServiceCallable<Void>(tableKey, ServiceTaskType.DELETE, new Callable<Void>() {
                @Override
                public Void call() {
                    List<SegmentKey> segmentKeys = segmentManager.getSegmentKeys(tableKey);

                    for (SegmentKey segKey : segmentKeys) {
                        if (!segmentManager.existsSegment(segKey)) {
                            continue;
                        }

                        try {
                            WhereDeleter whereDeleter = (WhereDeleter) SwiftContext.get().getBean("decrementer", segKey);
                            whereDeleter.delete(where);
                        } catch (Exception e) {
                            SwiftLoggers.getLogger().error(e);
                        }
                    }
                    return null;
                }
            }));

            return true;
        } catch (InterruptedException e) {
            SwiftLoggers.getLogger().error(e);
            return false;
        }
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.DELETE;
    }
}