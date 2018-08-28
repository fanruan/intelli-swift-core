package com.fr.swift.service;

import com.fr.swift.annotation.SwiftService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.io.Types;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.query.QueryRunnerProvider;
import com.fr.swift.query.session.factory.SessionFactory;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.segment.SegmentLocationProvider;
import com.fr.swift.segment.impl.SegmentDestinationImpl;
import com.fr.swift.segment.impl.SegmentLocationInfoImpl;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.task.service.ServiceTaskExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author pony
 * @date 2017/10/12
 * 分析服务
 */
@SwiftService(name = "analyse")
public class SwiftAnalyseService extends AbstractSwiftService implements AnalyseService {
    private static final long serialVersionUID = 841582089735823794L;

    private transient ServiceTaskExecutor taskExecutor;

    private transient SwiftSegmentService segmentProvider;

    private transient SessionFactory sessionFactory;

    private transient boolean loadable = true;

    private SwiftAnalyseService() {
    }

    @Override
    public boolean start() throws SwiftServiceException {
        boolean start = super.start();
        taskExecutor = SwiftContext.get().getBean(ServiceTaskExecutor.class);
        segmentProvider = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class);
        QueryRunnerProvider.getInstance().registerRunner(this);
        this.sessionFactory = SwiftContext.get().getBean("swiftQuerySessionFactory", SessionFactory.class);
        if (loadable) {
            loadSelfSegmentDestination();
            loadable = false;
        }
        return start;
    }

    @Override
    public boolean shutdown() throws SwiftServiceException {
        super.shutdown();
        taskExecutor = null;
        segmentProvider = null;
        sessionFactory = null;
        QueryRunnerProvider.getInstance().registerRunner(null);
        return true;
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.ANALYSE;
    }

    @Override
    public SwiftResultSet getQueryResult(QueryBean info) throws Exception {
        return sessionFactory.openSession(info.getQueryId()).executeQuery(info);
    }

    @Override
    public void updateSegmentInfo(SegmentLocationInfo locationInfo, SegmentLocationInfo.UpdateType updateType) {
        SegmentLocationProvider.getInstance().updateSegmentInfo(locationInfo, updateType);
    }

    private void loadSelfSegmentDestination() {
        Map<String, List<SegmentKey>> segments = segmentProvider.getOwnSegments();
        if (!segments.isEmpty()) {
            Map<String, List<SegmentDestination>> hist = new HashMap<String, List<SegmentDestination>>();
            Map<String, List<SegmentDestination>> realTime = new HashMap<String, List<SegmentDestination>>();
            for (Map.Entry<String, List<SegmentKey>> entry : segments.entrySet()) {
                initSegDestinations(hist, entry.getKey());
                initSegDestinations(realTime, entry.getKey());
                for (SegmentKey segmentKey : entry.getValue()) {
                    if (segmentKey.getStoreType() == Types.StoreType.FINE_IO) {
                        hist.get(entry.getKey()).add(new SegmentDestinationImpl(segmentKey.toString(), segmentKey.getOrder()));
                    } else {
                        realTime.get(entry.getKey()).add(new SegmentDestinationImpl(segmentKey.toString(), segmentKey.getOrder()));
                    }
                }
            }
            updateSegmentInfo(new SegmentLocationInfoImpl(ServiceType.HISTORY, hist), SegmentLocationInfo.UpdateType.PART);
            updateSegmentInfo(new SegmentLocationInfoImpl(ServiceType.REAL_TIME, realTime), SegmentLocationInfo.UpdateType.PART);
        }
    }

    private void initSegDestinations(Map<String, List<SegmentDestination>> map, String key) {
        if (null == map.get(key)) {
            map.put(key, new ArrayList<SegmentDestination>() {
                @Override
                public boolean add(SegmentDestination segmentDestination) {
                    return contains(segmentDestination) ? false : super.add(segmentDestination);
                }
            });
        }
    }
}
