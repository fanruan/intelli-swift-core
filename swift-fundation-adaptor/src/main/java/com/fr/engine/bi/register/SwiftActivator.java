package com.fr.engine.bi.register;

import com.finebi.base.stable.StableManager;
import com.fr.module.Activator;

/**
 * Created by pony on 2018/5/9.
 */
public class SwiftActivator extends Activator {
    @Override
    public void start() {
        StableManager.addClass("localSwiftServerService", com.fr.swift.service.LocalSwiftServerService.class);
        StableManager.addClass("swiftTableEngineExecutor", com.finebi.conf.imp.SwiftTableEngineExecutor.class);
        StableManager.addClass("swiftAnalysisTableManager", com.finebi.conf.imp.SwiftAnalysisTableManager.class);
        StableManager.addClass("swiftEngineWidgetExecutorManager", com.fr.swift.adaptor.widget.SwiftWidgetExecutorManager.class);
        StableManager.addClass("swiftUpdateManager", com.finebi.conf.imp.SwiftUpdateManager.class);
        StableManager.addClass("swiftAnalysisConfManager", com.finebi.conf.provider.SwiftAnalysisConfManager.class);
        StableManager.addClass("swiftAnalysisRelationPathManager", com.finebi.conf.provider.SwiftAnalysisRelationPathManager.class);
        StableManager.addClass("swiftPackageConfProvider", com.finebi.conf.provider.SwiftPackageConfProvider.class);
        StableManager.addClass("swiftTableManager", com.finebi.conf.provider.SwiftTableManager.class);
        StableManager.addClass("swiftRelationPathConfProvider", com.finebi.conf.provider.SwiftRelationPathConfProvider.class);
        StableManager.addClass("swiftSpaceManager", com.fr.swift.adaptor.space.SwiftSpaceManager.class);
    }

    @Override
    public void stop() {

    }
}
