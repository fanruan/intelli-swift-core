package com.fr.swift.boot;

import com.fr.module.Activator;
import com.fr.module.extension.Prepare;
import com.fr.stable.db.constant.BaseDBConstant;
import com.fr.swift.config.bean.unique.RepositoryConfigUnique;
import com.fr.swift.config.bean.unique.RpcServiceAddressUnique;
import com.fr.swift.config.context.SwiftConfigContext;
import com.fr.swift.config.entity.SwiftMetaDataEntity;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.config.entity.SwiftServiceInfoEntity;
import com.fr.swift.config.service.SwiftPathService;
import com.fr.swift.config.service.SwiftRepositoryConfService;
import com.fr.swift.config.service.SwiftServiceAddressService;
import com.fr.swift.config.service.SwiftZipService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.queue.ProviderTaskManager;
import com.fr.swift.decision.config.SwiftCubePathConfig;
import com.fr.swift.decision.config.SwiftRepositoryConfig;
import com.fr.swift.decision.config.SwiftServiceAddressConfig;
import com.fr.swift.decision.config.SwiftZipConfig;
import com.fr.swift.event.ClusterListenerHandler;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.service.register.LocalSwiftRegister;

import java.util.Iterator;
import java.util.Map;

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
        this.addMutable(BaseDBConstant.BASE_ENTITY_KEY, SwiftMetaDataEntity.class, SwiftSegmentEntity.class, SwiftServiceInfoEntity.class);
    }

    private void syncFRConfig() {
        String path = SwiftCubePathConfig.getInstance().get();
        SwiftContext.getInstance().getBean(SwiftPathService.class).setSwiftPath(path);
        boolean zip = SwiftZipConfig.getInstance().get();
        SwiftContext.getInstance().getBean(SwiftZipService.class).setZip(zip);
        RepositoryConfigUnique unique = SwiftRepositoryConfig.getInstance().getCurrentRepository();
        if (null != unique) {
            SwiftContext.getInstance().getBean(SwiftRepositoryConfService.class).setCurrentRepository(unique.convert());
        }
        Map<String, RpcServiceAddressUnique> all = SwiftServiceAddressConfig.getInstance().get();
        if (!all.isEmpty()) {
            Iterator<Map.Entry<String, RpcServiceAddressUnique>> iterator = all.entrySet().iterator();
            SwiftServiceAddressService service = SwiftContext.getInstance().getBean(SwiftServiceAddressService.class);
            while (iterator.hasNext()) {
                Map.Entry<String, RpcServiceAddressUnique> entry = iterator.next();
                service.addOrUpdateAddress(entry.getKey(), entry.getValue().convert());
            }
        }
    }
}