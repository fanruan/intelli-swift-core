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
import com.fr.swift.config.conf.MetaDataConfig;
import junit.framework.TestCase;

/**
 * @Author: Lucifer
 * @Description:
 * @Date: Created in 2018-3-8
 */
public class MetaDataConfigTest extends TestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        DBOption dbOption = new DBOption();
        dbOption.setPassword("root123");
        dbOption.setDialectClass("com.fr.third.org.hibernate.dialect.MySQL5InnoDBDialect");
        dbOption.setDriverClass("com.mysql.jdbc.Driver");
        dbOption.setUsername("root");
        dbOption.setUrl("jdbc:mysql://localhost:3306/config");
        dbOption.addRawProperty("hibernate.show_sql", true)
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

    public void testAddAndGet() {
        MetaDataConfig.getInstance().addMetaData(MetaDataCreater.getMA());

        assertEquals(MetaDataConfig.getInstance().getAllMetaData().size(), 1);

        IMetaData metaData = MetaDataConfig.getInstance().getMetaDataByKey("A");
        assertEquals(metaData.getTableName(), MetaDataCreater.getMA().getTableName());
        assertEquals(metaData.getRemark(), MetaDataCreater.getMA().getRemark());
        assertEquals(metaData.getSchema(), MetaDataCreater.getMA().getSchema());
        assertEquals(metaData.getFieldList().size(), MetaDataCreater.getMA().getFieldList().size());
    }

    public void testAddAndRemove() {
        MetaDataConfig.getInstance().addMetaData(MetaDataCreater.getMA());
        assertEquals(MetaDataConfig.getInstance().getAllMetaData().size(), 1);
        MetaDataConfig.getInstance().removeMetaData(MetaDataCreater.getMA().getTableName());
        assertEquals(MetaDataConfig.getInstance().getAllMetaData().size(), 0);
    }

    public void testAddAndModify() {
        MetaDataConfig.getInstance().addMetaData(MetaDataCreater.getMA());
        IMetaData metaData1 = MetaDataConfig.getInstance().getMetaDataByKey("A");

        MetaDataConfig.getInstance().modifyMetaData(MetaDataCreater.getMAModify());

        IMetaData metaData2 = MetaDataConfig.getInstance().getMetaDataByKey("A");

        assertEquals(metaData1.getTableName(), metaData2.getTableName());
        assertNotSame(metaData1.getRemark(), metaData2.getRemark());
        assertNotSame(metaData1.getSchema(), metaData2.getSchema());
        assertEquals(metaData1.getFieldList().size(), metaData2.getFieldList().size());
    }
}
