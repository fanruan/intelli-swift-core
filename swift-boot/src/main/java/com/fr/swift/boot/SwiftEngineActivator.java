package com.fr.swift.boot;

import com.fineio.FineIO;
import com.fr.module.Activator;
import com.fr.module.extension.Prepare;
import com.fr.stable.db.constant.BaseDBConstant;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.context.SwiftConfigContext;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.queue.ProviderTaskManager;
import com.fr.swift.event.ClusterListenerHandler;
import com.fr.swift.log.FineIOLoggerImpl;
import com.fr.swift.log.SwiftLoggers;
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
        ClusterListenerHandler.addListener(new FRClusterListener());
        SwiftContext.init();
        SwiftConfigContext.getInstance().init();
        new LocalSwiftRegister().serviceRegister();
        FineIO.setLogger(new FineIOLoggerImpl());
        ProviderTaskManager.start();
    }

    @Override
    public void stop() {
        SwiftLoggers.getLogger().info("swift engine stopped");
    }

    @Override
    public void prepare() {
        this.addMutable(BaseDBConstant.BASE_ENTITY_KEY, SwiftConfigConstants.ENTITIES);
    }

}