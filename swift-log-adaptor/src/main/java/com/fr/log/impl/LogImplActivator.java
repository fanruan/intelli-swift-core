package com.fr.log.impl;

import com.fr.cluster.entry.ClusterTicketKey;
import com.fr.general.LogOperatorFactory;
import com.fr.module.Activator;
import com.fr.module.extension.Prepare;
import com.fr.stable.db.constant.BaseDBConstant;
import com.fr.swift.adaptor.log.LogOperatorProxy;
import com.fr.swift.config.SwiftServiceInfoEntity;
import com.fr.swift.config.entity.SwiftMetaDataEntity;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.frrpc.SwiftClusterTicket;

/**
 * @author anchore
 * @date 2018/4/26
 */
public class LogImplActivator extends Activator implements Prepare {
    @Override
    public void start() {
        LogOperatorFactory.registerLogOperatorProvider(LogOperatorProxy.getInstance());
    }

    @Override
    public void stop() {
        LogOperatorFactory.registerLogOperatorProvider(null);
    }

    @Override
    public void prepare() {
        addMutable(ClusterTicketKey.KEY, SwiftClusterTicket.getInstance());
    }
}