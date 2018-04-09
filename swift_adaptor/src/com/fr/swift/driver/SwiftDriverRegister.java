package com.fr.swift.driver;

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
        logger.debug("Drives registration success");
    }

    /**
     * TODO 注册驱动逻辑
     */
    public void init() {
        CommonDataSourceDriverFactory.getInstance().registerDriver(new SwiftDataSourceDriverWrapper(new CommonAnalysisDataSourceDriver()));
        CommonDataSourceDriverFactory.getInstance().registerDriver(new SwiftDataSourceDriverWrapper(new CommonCircleDataSourceDriver()));
        CommonDataSourceDriverFactory.getInstance().registerDriver(new SwiftDataSourceDriverWrapper(new CommonConfAnalysisDataSourceDriver()));
        CommonDataSourceDriverFactory.getInstance().registerDriver(new SwiftDataSourceDriverWrapper(new CommonSqlDataSourceDriver()));
        CommonDataSourceDriverFactory.getInstance().registerDriver(new SwiftDataSourceDriverWrapper(new CommonDatabaseDataSourceDriver()));
    }
}
