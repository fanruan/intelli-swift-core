package com.fr.swift.generate;

import com.fr.config.DBEnv;
import com.fr.config.dao.DaoContext;
import com.fr.config.dao.impl.HibernateClassHelperDao;
import com.fr.config.dao.impl.HibernateEntityDao;
import com.fr.config.dao.impl.HibernateXmlEnityDao;
import com.fr.config.entity.ClassHelper;
import com.fr.config.entity.Entity;
import com.fr.config.entity.XmlEntity;
import com.fr.stable.db.DBContext;
import com.fr.stable.db.option.DBOption;
import com.fr.swift.config.IConfigSegment;
import com.fr.swift.config.IMetaData;
import com.fr.swift.config.ISegmentKey;
import com.fr.swift.config.conf.MetaDataConfig;
import com.fr.swift.config.conf.MetaDataConvertUtil;
import com.fr.swift.config.conf.SegmentConfig;
import com.fr.swift.config.unique.SegmentKeyUnique;
import com.fr.swift.config.unique.SegmentUnique;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.service.LocalSwiftServerService;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.db.TestConnectionProvider;
import junit.framework.TestCase;

public abstract class BaseTest extends TestCase {
    protected static final SwiftLogger LOGGER = SwiftLoggers.getLogger(BaseTest.class);

    @Override
    public void setUp() throws Exception {
        new LocalSwiftServerService().start();
        TestConnectionProvider.createConnection();
    }

    protected void putMetaAndSegment(DataSource dataSource, int seg, IResourceLocation location, Types.StoreType storeType) throws Exception {
        IMetaData metaData = MetaDataConvertUtil.convert2ConfigMetaData(dataSource.getMetadata());
        MetaDataConfig.getInstance().addMetaData(dataSource.getSourceKey().getId(), metaData);
        IConfigSegment configSegment = new SegmentUnique();
        configSegment.setSourceKey(dataSource.getSourceKey().getId());
        ISegmentKey segmentKey = new SegmentKeyUnique();
        segmentKey.setSegmentOrder(seg);
        segmentKey.setUri(location.getUri().getPath());
        segmentKey.setSourceId(dataSource.getSourceKey().getId());
        segmentKey.setStoreType(storeType.name());
        configSegment.addSegment(segmentKey);
        SegmentConfig.getInstance().putSegment(configSegment);
    }

    public final void setConfDb() throws Exception {
        DBOption dbOption = new DBOption();
        dbOption.setPassword("");
        dbOption.setDialectClass("com.fr.third.org.hibernate.dialect.H2Dialect");
        dbOption.setDriverClass("org.h2.Driver");
        dbOption.setUsername("sa");
        dbOption.setUrl("jdbc:h2:~/config");
        dbOption.addRawProperty("hibernate.show_sql", false)
                .addRawProperty("hibernate.format_sql", true).addRawProperty("hibernate.connection.autocommit", true);
        DBContext dbProvider = DBContext.create();
        dbProvider.addEntityClass(Entity.class);
        dbProvider.addEntityClass(XmlEntity.class);
        dbProvider.addEntityClass(ClassHelper.class);
        dbProvider.init(dbOption);
        DBEnv.setDBContext(dbProvider);
        DaoContext.setClassHelperDao(new HibernateClassHelperDao());
        DaoContext.setXmlEntityDao(new HibernateXmlEnityDao());
        DaoContext.setEntityDao(new HibernateEntityDao());
    }
}
