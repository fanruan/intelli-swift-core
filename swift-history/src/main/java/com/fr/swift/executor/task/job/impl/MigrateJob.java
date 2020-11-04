package com.fr.swift.executor.task.job.impl;

import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.executor.task.bean.MigrateBean;
import com.fr.swift.executor.task.job.BaseJob;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.service.ServiceContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Heng.J
 * @date 2020/10/29
 * @description
 * @since swift-1.2.0
 */
public class MigrateJob extends BaseJob<Boolean, MigrateBean> {

    private MigrateBean migrateBean;

    public MigrateJob(MigrateBean migrateBean) {
        this.migrateBean = migrateBean;
    }

    @Override
    public Boolean call() throws Exception {
        ServiceContext serviceContext = ProxySelector.getProxy(ServiceContext.class);
        Map<SegmentKey, Map<String, byte[]>> segments = new HashMap<>();
        boolean success = serviceContext.migrate(segments, migrateBean.getTarget());
        return false;
    }

    @Override
    public MigrateBean serializedTag() {
        return migrateBean;
    }
}
