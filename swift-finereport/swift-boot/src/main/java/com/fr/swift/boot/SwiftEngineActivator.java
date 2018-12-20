package com.fr.swift.boot;

import com.fineio.FineIO;
import com.fr.cluster.entry.ClusterTicketKey;
import com.fr.io.base.WebInfResourceFolders;
import com.fr.log.impl.MetricInvocationHandler;
import com.fr.module.Activator;
import com.fr.module.extension.Prepare;
import com.fr.stable.db.constant.BaseDBConstant;
import com.fr.swift.SwiftContext;
import com.fr.swift.boot.upgrade.UpgradeTask;
import com.fr.swift.cluster.listener.NodeStartedListener;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.context.SwiftConfigContext;
import com.fr.swift.cube.queue.ProviderTaskManager;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.event.ClusterListenerHandler;
import com.fr.swift.log.SwiftFrLoggers;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.container.SegmentContainer;
import com.fr.swift.service.MaskHistoryListener;
import com.fr.swift.service.RemoveHistoryListener;
import com.fr.swift.service.TransferRealtimeListener;
import com.fr.swift.service.UploadHistoryListener;
import com.fr.swift.service.local.ServiceManager;
import com.fr.swift.util.concurrent.CommonExecutor;

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

    private void startSwift() {
        upgrade();

        SwiftLoggers.setLoggerFactory(new SwiftFrLoggers());
        FineIO.setLogger(SwiftLoggers.getLogger());

        ClusterListenerHandler.addInitialListener(new FRClusterListener());

        CommonExecutor.get().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    SwiftContext.get().init();
                    MetricInvocationHandler.getInstance().doAfterSwiftContextInit();

                    ClusterListenerHandler.addInitialListener(NodeStartedListener.INSTANCE);
                    SwiftConfigContext.getInstance().init();
                    SwiftContext.get().getBean("localManager", ServiceManager.class).startUp();

                    ProviderTaskManager.start();

                    TransferRealtimeListener.listen();
                    UploadHistoryListener.listen();
                    MaskHistoryListener.listen();
                    RemoveHistoryListener.listen();
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error("swift engine start failed", e);
                }
            }
        });

        registerResourceIoPath();
    }

    private static void registerResourceIoPath() {
        for (SwiftDatabase schema : SwiftDatabase.values()) {
            WebInfResourceFolders.add(String.format("%s", schema.getDir()));
        }
    }

    private void upgrade() {
        new UpgradeTask().run();
    }

    @Override
    public void stop() {
        try {
            SwiftContext.get().getBean("localManager", ServiceManager.class).shutDown();
            for (SegmentContainer container : SegmentContainer.values()) {
                container.clear();
            }
            SwiftLoggers.getLogger().info("swift engine stopped");
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("swift engine stop failed", e);
        }
    }

    @Override
    public void prepare() {
        this.addMutable(ClusterTicketKey.KEY, SwiftClusterTicket.getInstance());
        this.addMutable(BaseDBConstant.BASE_ENTITY_KEY, SwiftConfigConstants.ENTITIES);
    }

}