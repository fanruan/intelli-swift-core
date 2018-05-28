package com.fr.engine.bi.register;

import com.finebi.base.stable.StableManager;
import com.finebi.conf.algorithm.DMDataModel;
import com.fr.module.Activator;
import com.fr.module.extension.Prepare;
import com.fr.stable.db.constant.BaseDBConstant;
import com.fr.swift.config.entity.SwiftMetaDataEntity;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.config.service.SwiftConfigServiceProvider;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.queue.ProviderTaskManager;
import com.fr.swift.driver.SwiftDriverRegister;
import com.fr.swift.manager.ConnectionProvider;
import com.fr.swift.service.LocalSwiftServerService;
import com.fr.swift.service.SwiftAnalyseService;
import com.fr.swift.service.SwiftHistoryService;
import com.fr.swift.service.SwiftIndexingService;
import com.fr.swift.service.SwiftRealTimeService;
import com.fr.swift.source.db.ConnectionManager;
import com.fr.swift.source.etl.ETLOperator;
import com.fr.swift.source.etl.ETLTransferOperator;
import com.fr.swift.source.etl.EtlTransferOperatorFactory;
import com.fr.swift.source.etl.datamining.DataMiningOperator;
import com.fr.swift.source.etl.datamining.DataMiningTransferOperator;
import com.fr.swift.source.etl.rcompile.RCompileOperator;
import com.fr.swift.source.etl.rcompile.RCompileTransferOperator;
import com.fr.swift.util.Crasher;

/**
 * Created by pony on 2018/5/9.
 */
public class SwiftActivator extends Activator implements Prepare {
    @Override
    public void start() {
        StableManager.addClass("swiftTableEngineExecutor", com.finebi.conf.impl.SwiftTableEngineExecutor.class);
        StableManager.addClass("swiftAnalysisTableManager", com.finebi.conf.impl.SwiftAnalysisTableManager.class);
        StableManager.addClass("swiftEngineWidgetExecutorManager", com.fr.swift.adaptor.widget.SwiftWidgetExecutorManager.class);
        StableManager.addClass("swiftUpdateManager", com.finebi.conf.impl.SwiftUpdateManager.class);
        StableManager.addClass("swiftAnalysisConfManager", com.finebi.conf.provider.SwiftAnalysisConfManager.class);
        StableManager.addClass("swiftAnalysisRelationPathManager", com.finebi.conf.provider.SwiftAnalysisRelationPathManager.class);
        StableManager.addClass("swiftPackageConfProvider", com.finebi.conf.provider.SwiftPackageConfProvider.class);
        StableManager.addClass("swiftTableManager", com.finebi.conf.provider.SwiftTableManager.class);
        StableManager.addClass("swiftRelationPathConfProvider", com.finebi.conf.provider.SwiftRelationPathConfProvider.class);
        StableManager.addClass("swiftSpaceManager", com.fr.swift.adaptor.space.SwiftSpaceManager.class);
        StableManager.addClass("swiftFoundationListener", com.fr.engine.bi.register.SwiftFoundationListener.class);
        startSwift();

    }

    private void startSwift() {
        ConnectionManager.getInstance().registerProvider(new ConnectionProvider());

        EtlTransferOperatorFactory.register(DataMiningOperator.class, new EtlTransferOperatorFactory.ETLTransferCreator() {

            @Override
            public ETLTransferOperator createTransferOperator(ETLOperator operator) {
                return new DataMiningTransferOperator(((DataMiningOperator) operator).getAlgorithmBean());
            }
        });
        EtlTransferOperatorFactory.register(RCompileOperator.class, new EtlTransferOperatorFactory.ETLTransferCreator() {

            @Override
            public ETLTransferOperator createTransferOperator(ETLOperator operator) {
                RCompileOperator op = (RCompileOperator) operator;
                DMDataModel dataList = op.getDataModel();
                return new RCompileTransferOperator(dataList);
            }
        });

        // fixme 这边先不去掉，其实和SwiftEngineActivator重复了
        try {
            SwiftDriverRegister.register();
            new LocalSwiftServerService().start();
            new SwiftAnalyseService().start();
            new SwiftHistoryService().start();
            new SwiftIndexingService().start();
            new SwiftRealTimeService().start();
            ProviderTaskManager.start();
        } catch (Exception e) {
            Crasher.crash("swift service start failed", e);
        }
    }

    @Override
    public void stop() {
    }

    @Override
    public void prepare() {
        this.addMutable(BaseDBConstant.BASE_ENTITY_KEY, SwiftMetaDataEntity.class, SwiftSegmentEntity.class);
    }
}