package com.fr.swift.driver;

import com.finebi.base.constant.FineEngineType;
import com.finebi.common.internalimp.config.driver.CommonAnalysisDataSourceDriver;
import com.finebi.common.internalimp.config.driver.CommonCircleDataSourceDriver;
import com.finebi.common.internalimp.config.driver.CommonConfAnalysisDataSourceDriver;
import com.finebi.common.internalimp.config.driver.CommonDataSourceDriverFactory;
import com.finebi.common.internalimp.config.driver.CommonDatabaseDataSourceDriver;
import com.finebi.common.internalimp.config.driver.CommonSqlDataSourceDriver;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;

/**
 * @author yee
 * @date 2018/4/9
 */
public class SwiftDriverRegister {
    private static SwiftDriverRegister instance;
    private static SwiftLogger logger = SwiftLoggers.getLogger(SwiftDriverRegister.class);

    public static void register() {
        if (null == instance) {
            synchronized (SwiftDriverRegister.class) {
                if (null == instance) {
                    instance = new SwiftDriverRegister();
                    instance.init();
                }
            }
        }
    }

    public void init() {
        CommonDataSourceDriverFactory.getInstance(FineEngineType.Cube).update(new SwiftDataSourceDriverWrapper(new CommonAnalysisDataSourceDriver(FineEngineType.Cube)));
        CommonDataSourceDriverFactory.getInstance(FineEngineType.Cube).update(new SwiftDataSourceDriverWrapper(new CommonCircleDataSourceDriver(FineEngineType.Cube)));
        CommonDataSourceDriverFactory.getInstance(FineEngineType.Cube).update(new SwiftDataSourceDriverWrapper(new CommonConfAnalysisDataSourceDriver(FineEngineType.Cube)));
        CommonDataSourceDriverFactory.getInstance(FineEngineType.Cube).update(new SwiftDataSourceDriverWrapper(new CommonSqlDataSourceDriver(FineEngineType.Cube)));
        CommonDataSourceDriverFactory.getInstance(FineEngineType.Cube).update(new SwiftDataSourceDriverWrapper(new CommonDatabaseDataSourceDriver(FineEngineType.Cube)));
        logger.info("Drives registration success");
    }
}
