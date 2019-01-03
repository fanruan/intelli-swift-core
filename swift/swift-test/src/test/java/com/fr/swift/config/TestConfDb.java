package com.fr.swift.config;

import com.fr.config.BaseDBEnv;
import com.fr.config.dao.DaoContext;
import com.fr.config.dao.impl.HibernateClassHelperDao;
import com.fr.config.dao.impl.HibernateEntityDao;
import com.fr.config.dao.impl.HibernateXmlEnityDao;
import com.fr.config.entity.ClassHelper;
import com.fr.config.entity.Entity;
import com.fr.config.entity.XmlEntity;
import com.fr.stable.db.DBContext;
import com.fr.stable.db.option.DBOption;
import com.fr.swift.config.entity.SwiftMetaDataEntity;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.config.entity.SwiftServiceInfoEntity;
import com.fr.swift.test.Preparer;
import com.fr.swift.util.FileUtil;
import com.fr.transaction.Configurations;
import com.fr.transaction.FineConfigurationHelper;

/**
 * @author anchore
 * @date 2018/4/8
 */
public class TestConfDb {
    private static final String PATH = System.getProperty("user.dir") + "/config";

    public static DBContext setConfDb() throws Exception {
        return setConfDb(Entity.class,
                XmlEntity.class,
                ClassHelper.class,
                SwiftMetaDataEntity.class,
                SwiftSegmentEntity.class,
                SwiftServiceInfoEntity.class);
    }

    public static DBContext setConfDb(Class<?>... entities) throws Exception {
        Preparer.prepareFrEnv();
        FileUtil.delete(PATH + ".mv.db");
        FileUtil.delete(PATH + ".trace.db");
        DBOption dbOption = new DBOption();
        dbOption.setUrl("jdbc:h2:" + PATH);
        dbOption.setUsername("sa");
        dbOption.setPassword("");
        dbOption.setDriverClass("org.h2.Driver");
        dbOption.setDialectClass("com.fr.third.org.hibernate.dialect.H2Dialect");
        dbOption.addRawProperty("hibernate.show_sql", false)
                .addRawProperty("hibernate.format_sql", true)
                .addRawProperty("hibernate.connection.autocommit", true);
        DBContext dbProvider = DBContext.create();

        for (Class<?> entity : entities) {
            dbProvider.addEntityClass(entity);
        }

        dbProvider.init(dbOption);
        BaseDBEnv.setDBContext(dbProvider);
        DaoContext.setClassHelperDao(new HibernateClassHelperDao());
        DaoContext.setXmlEntityDao(new HibernateXmlEnityDao());
        DaoContext.setEntityDao(new HibernateEntityDao());
        Configurations.setHelper(new FineConfigurationHelper());
        return dbProvider;
    }
}