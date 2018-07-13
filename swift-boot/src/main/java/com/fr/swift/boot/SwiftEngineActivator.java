package com.fr.swift.boot;

import com.fr.module.Activator;
import com.fr.module.extension.Prepare;
import com.fr.stable.db.constant.BaseDBConstant;
import com.fr.swift.config.context.SwiftConfigContext;
import com.fr.swift.config.entity.SwiftConfigEntity;
import com.fr.swift.config.entity.SwiftMetaDataEntity;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.config.entity.SwiftSegmentLocationEntity;
import com.fr.swift.config.entity.SwiftServiceInfoEntity;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.queue.ProviderTaskManager;
import com.fr.swift.event.ClusterListenerHandler;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.server.SwiftEngineStart;
import com.fr.swift.service.register.LocalSwiftRegister;

/**
 * @author anchore
 * @date 2018/5/24
 */
public class SwiftEngineActivator extends Activator implements Prepare {
    @Override
    public void start() {
        try {
            startSwift();
            SwiftLoggers.getLogger().info("swift engine started");
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("swift engine start failed", e);
        }
    }

    private void startSwift() throws Exception {
        SwiftContext.init();
        SwiftConfigContext.getInstance().init();
        syncFRConfig();
        new LocalSwiftRegister().serviceRegister();
        ClusterListenerHandler.addListener(new ClusterListener());
        ProviderTaskManager.start();
    }

    @Override
    public void stop() {
        SwiftLoggers.getLogger().info("swift engine stopped");
    }

    @Override
    public void prepare() {
        this.addMutable(BaseDBConstant.BASE_ENTITY_KEY,
                SwiftMetaDataEntity.class,
                SwiftSegmentEntity.class,
                SwiftServiceInfoEntity.class,
                SwiftSegmentLocationEntity.class,
                SwiftConfigEntity.class);
    }

    private void syncFRConfig() {
        SwiftEngineStart.syncConfiguration();
    }
}