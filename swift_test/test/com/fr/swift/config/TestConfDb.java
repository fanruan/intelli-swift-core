package com.fr.swift.config;

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

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author anchore
 * @date 2018/4/8
 */
public class TestConfDb {
    private static final Path PATH = Paths.get(System.getProperty("user.dir") + "/config");

    public static void setConfDb() throws Exception {
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
        DBEnv.setDBContext(dbProvider);
        DaoContext.setClassHelperDao(new HibernateClassHelperDao());
        DaoContext.setXmlEntityDao(new HibernateXmlEnityDao());
        DaoContext.setEntityDao(new HibernateEntityDao());
    }
}