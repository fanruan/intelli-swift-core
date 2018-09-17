package com.fr.swift.config.context;

import com.fr.config.BaseDBEnv;
import com.fr.finedb.FineDBProperties;
import com.fr.stable.db.DBContext;
import com.fr.stable.db.option.DBOption;
import com.fr.swift.config.SwiftConfigConstants;

/**
 * @author yee
 * @date 2018/5/24
 */
public class SwiftConfigContext {
    private static SwiftConfigContext self;
    private boolean initialized = false;

    private SwiftConfigContext() {
    }

    public static SwiftConfigContext getInstance() {
        if (null == self) {
            synchronized (SwiftConfigContext.class) {
                if (null == self) {
                    self = new SwiftConfigContext();
                }
            }
        }
        return self;
    }

    public SwiftConfigContext init() throws Exception {
        synchronized (this) {
            if (initialized) {
                return this;
            }
            DBOption option = FineDBProperties.getInstance().get();
            DBContext dbContext = BaseDBEnv.getDBContext();
            if (null == dbContext) {
                dbContext = DBContext.create();
            }
            for (Class<?> entity : SwiftConfigConstants.ENTITIES) {
                dbContext.addEntityClass(entity);
            }
            dbContext.init(option);
            BaseDBEnv.setDBContext(dbContext);
            this.initialized = true;
        }
        return this;
    }
}
