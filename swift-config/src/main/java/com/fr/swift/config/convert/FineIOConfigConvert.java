package com.fr.swift.config.convert;

import com.fr.swift.config.bean.CommonConnectorConfig;
import com.fr.swift.config.bean.FineIOConnectorConfig;
import com.fr.swift.config.convert.base.AbstractObjectConfigConvert;
import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.entity.SwiftConfigEntity;
import com.fr.swift.config.entity.key.SwiftTablePathKey;
import com.fr.swift.cube.io.impl.fineio.connector.CommonConnectorType;
import com.fr.swift.event.ClusterEvent;
import com.fr.swift.event.ClusterEventListener;
import com.fr.swift.event.ClusterEventType;
import com.fr.swift.event.ClusterListenerHandler;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.selector.ClusterSelector;
import com.fr.third.org.hibernate.Session;

/**
 * @author yee
 * @date 2018-12-20
 */
public class FineIOConfigConvert extends AbstractObjectConfigConvert<FineIOConnectorConfig> {
    private static final String FINE_IO_CONNECTOR = "FINE_IO_CONNECTOR";
    private String clusterId = SwiftTablePathKey.LOCALHOST;

    public FineIOConfigConvert() {
        ClusterListenerHandler.addListener(new ClusterEventListener() {
            @Override
            public void handleEvent(ClusterEvent clusterEvent) {
                if (clusterEvent.getEventType() == ClusterEventType.JOIN_CLUSTER) {
                    clusterId = ClusterSelector.getInstance().getFactory().getCurrentId();
                } else if (clusterEvent.getEventType() == ClusterEventType.LEFT_CLUSTER) {
                    clusterId = SwiftTablePathKey.LOCALHOST;
                }
            }
        });
    }

    @Override
    public FineIOConnectorConfig toBean(SwiftConfigDao<SwiftConfigEntity> dao, Session session, Object... args) {
        try {
            return super.toBean(dao, session, args);
        } catch (Exception e) {
            SwiftLoggers.getLogger().warn("Cannot find FineIO config. use default");
            return new CommonConnectorConfig(CommonConnectorType.LZ4);
        }
    }

    @Override
    protected String transferClassName(String className) {
        return className;
    }

    @Override
    protected String getNameSpace() {
        return FINE_IO_CONNECTOR + "." + clusterId;
    }
}
