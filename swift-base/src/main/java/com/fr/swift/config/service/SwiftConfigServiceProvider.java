package com.fr.swift.config.service;

import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.cube.io.Types;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;

import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/3/23
 */
public class SwiftConfigServiceProvider implements SwiftConfigService, SwiftServiceInfoService {

    private SwiftConfigService service = null;
    private SwiftServiceInfoService serviceInfoService = null;
    private static SwiftConfigServiceProvider instance;

    private SwiftConfigServiceProvider() {
        this.service = new SwiftConfigServiceImpl();
        this.serviceInfoService = new SwiftServiceInfoServiceImpl();
    }

    public static SwiftConfigServiceProvider getInstance() {
        if (null == instance) {
            synchronized (SwiftConfigServiceProvider.class) {
                instance = new SwiftConfigServiceProvider();
            }
        }
        return instance;
    }

    @Override
    public boolean addMetaData(String sourceKey, SwiftMetaData metaData) {
        return service.addMetaData(sourceKey, metaData);
    }

    @Override
    public boolean addMetaDatas(Map<String, SwiftMetaData> metaDatas) {
        return service.addMetaDatas(metaDatas);
    }

    @Override
    public boolean removeMetaDatas(String... sourceKey) {
        return service.removeMetaDatas(sourceKey);
    }

    @Override
    public boolean updateMetaData(String sourceKey, SwiftMetaData metaData) {
        return service.updateMetaData(sourceKey, metaData);
    }

    @Override
    public Map<String, SwiftMetaData> getAllMetaData() {
        return service.getAllMetaData();
    }

    @Override
    public SwiftMetaData getMetaDataByKey(String sourceKey) {
        return service.getMetaDataByKey(sourceKey);
    }

    @Override
    public boolean containsMeta(SourceKey sourceKey) {
        return service.containsMeta(sourceKey);
    }

    @Override
    public boolean addSegments(List<SegmentKey> segments) {
        return service.addSegments(segments);
    }

    @Override
    public boolean removeSegments(String... sourceKey) {
        return service.removeSegments(sourceKey);
    }

    @Override
    public boolean removeByStoreType(Types.StoreType type) {
        return service.removeByStoreType(type);
    }

    @Override
    public boolean updateSegments(String sourceKey, List<SegmentKey> segments) {
        return service.updateSegments(sourceKey, segments);
    }

    @Override
    public Map<String, List<SegmentKey>> getAllSegments() {
        return service.getAllSegments();
    }

    @Override
    public List<SegmentKey> getSegmentByKey(String sourceKey) {
        return service.getSegmentByKey(sourceKey);
    }

    @Override
    public List<SegmentKey> getUnStoreSegments(String sourceKey) {
        return service.getUnStoreSegments(sourceKey);
    }

    @Override
    public boolean setSwiftPath(String path) {
        return service.setSwiftPath(path);
    }

    @Override
    public String getSwiftPath() {
        return service.getSwiftPath();
    }


    @Override
    public boolean saveOrUpdateServiceInfo(SwiftServiceInfoBean serviceInfoBean) {
        return serviceInfoService.saveOrUpdateServiceInfo(serviceInfoBean);
    }

    @Override
    public boolean removeServiceInfo(SwiftServiceInfoBean serviceInfoBean) {
        return serviceInfoService.removeServiceInfo(serviceInfoBean);
    }

    @Override
    public SwiftServiceInfoBean getServiceInfo(SwiftServiceInfoBean serviceInfoBean) {
        return serviceInfoService.getServiceInfo(serviceInfoBean);
    }

    @Override
    public List<SwiftServiceInfoBean> getAllServiceInfo() {
        return serviceInfoService.getAllServiceInfo();
    }

    @Override
    public List<SwiftServiceInfoBean> getServiceInfoByService(String service) {
        return serviceInfoService.getServiceInfoByService(service);
    }
}
