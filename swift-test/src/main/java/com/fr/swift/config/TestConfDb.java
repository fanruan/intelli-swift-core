package com.fr.swift.config;

import com.fr.base.FRContext;
import com.fr.config.BaseDBEnv;
import com.fr.config.dao.DaoContext;
import com.fr.config.dao.impl.HibernateClassHelperDao;
import com.fr.config.dao.impl.HibernateEntityDao;
import com.fr.config.dao.impl.HibernateXmlEnityDao;
import com.fr.config.entity.ClassHelper;
import com.fr.config.entity.Entity;
import com.fr.config.entity.XmlEntity;
import com.fr.dav.LocalEnv;
import com.fr.stable.db.DBContext;
import com.fr.stable.db.option.DBOption;
import com.fr.swift.util.FileUtil;
import com.fr.transaction.Configurations;
import com.fr.transaction.FineConfigurationHelper;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author anchore
 * @date 2018/4/8
 */
public class TestConfDb {
    private static final Path PATH = Paths.get(System.getProperty("user.dir") + "/config");

    public static DBContext setConfDb() throws Exception {
        FRContext.setCurrentEnv(new LocalEnv());
        FileUtil.delete(PATH + ".mv.db");
        FileUtil.delete(PATH + ".trace.db");
        DBOption dbOption = new DBOption();
        dbOption.setUrl("jdbc:h2:" + PATH.toString());
        dbOption.setUsername("sa");
        dbOption.setPassword("");
        dbOption.setDriverClass("org.h2.Driver");
        dbOption.setDialectClass("com.fr.third.org.hibernate.dialect.H2Dialect");
        dbOption.addRawProperty("hibernate.show_sql", false)
                .addRawProperty("hibernate.format_sql", true).addRawProperty("hibernate.connection.autocommit", true);
        DBContext dbProvider = DBContext.create();
        dbProvider.addEntityClass(Entity.class);
        dbProvider.addEntityClass(XmlEntity.class);
        dbProvider.addEntityClass(ClassHelper.class);
        dbProvider.init(dbOption);
        BaseDBEnv.setDBContext(dbProvider);
        DaoContext.setClassHelperDao(new HibernateClassHelperDao());
        DaoContext.setXmlEntityDao(new HibernateXmlEnityDao());
        DaoContext.setEntityDao(new HibernateEntityDao());
        Configurations.setHelper(new FineConfigurationHelper());
        return dbProvider;
    }
}