package com.fr.swift.driver;

import com.finebi.base.constant.FineEngineType;
import com.finebi.common.internalimp.config.driver.CommonAnalysisDataSourceDriver;
import com.finebi.common.internalimp.config.driver.CommonCircleDataSourceDriver;
import com.finebi.common.internalimp.config.driver.CommonConfAnalysisDataSourceDriver;
import com.finebi.common.internalimp.config.driver.CommonDataSourceDriverFactory;
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
    private CommonDataSourceDriverFactory driverFactory = CommonDataSourceDriverFactory.getInstance(FineEngineType.Cube);

    public static void registerIfNeed() {
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
        driverFactory.registerDriver(new SwiftDataSourceDriverWrapper(new CommonAnalysisDataSourceDriver(FineEngineType.Cube)));
        driverFactory.registerDriver(new SwiftDataSourceDriverWrapper(new CommonCircleDataSourceDriver(FineEngineType.Cube)));
        driverFactory.registerDriver(new SwiftDataSourceDriverWrapper(new CommonConfAnalysisDataSourceDriver(FineEngineType.Cube)));
        driverFactory.registerDriver(new SwiftDataSourceDriverWrapper(new CommonSqlDataSourceDriver(FineEngineType.Cube)));
        driverFactory.registerDriver(new SwiftDatabaseDataSourceDriver());
        logger.info("Swift drives register success");
    }
}
