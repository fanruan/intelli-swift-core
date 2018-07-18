package com.fr.swift.config.context;

import com.fr.config.BaseDBEnv;
import com.fr.finedb.FineDBProperties;
import com.fr.stable.db.DBContext;
import com.fr.stable.db.option.DBOption;
import com.fr.swift.config.entity.SwiftConfigEntity;
import com.fr.swift.config.entity.SwiftMetaDataEntity;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.config.entity.SwiftSegmentLocationEntity;
import com.fr.swift.config.entity.SwiftServiceInfoEntity;
import com.fr.swift.config.indexing.impl.SwiftColumnIndexingConf;
import com.fr.swift.config.indexing.impl.SwiftTableIndexingConf;

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
            dbContext.addEntityClass(SwiftConfigEntity.class);
            dbContext.addEntityClass(SwiftMetaDataEntity.class);
            dbContext.addEntityClass(SwiftSegmentEntity.class);
            dbContext.addEntityClass(SwiftSegmentLocationEntity.class);
            dbContext.addEntityClass(SwiftServiceInfoEntity.class);
            dbContext.addEntityClass(SwiftColumnIndexingConf.class);
            dbContext.addEntityClass(SwiftTableIndexingConf.class);
            dbContext.init(option);
            BaseDBEnv.setDBContext(dbContext);
            this.initialized = true;
        }
        return this;
    }
}
