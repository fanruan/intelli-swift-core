//package com.fr.swift.config;
//
//import com.fr.config.DBEnv;
//import com.fr.config.dao.DaoContext;
//import com.fr.config.dao.impl.HibernateClassHelperDao;
//import com.fr.config.dao.impl.HibernateEntityDao;
//import com.fr.config.dao.impl.HibernateXmlEnityDao;
//import com.fr.config.entity.ClassHelper;
//import com.fr.config.entity.Entity;
//import com.fr.config.entity.XmlEntity;
//import com.fr.stable.db.DBContext;
//import com.fr.stable.db.option.DBOption;
//import SwiftConfigService;
//import SwiftConfigServiceProvider;
//import com.fr.swift.source.SourceKey;
//import junit.framework.TestCase;
//
///**
// * @Author: Lucifer
// * @Description:
// * @Date: Created in 2018-3-8
// */
//public class MetaDataConfigTest extends TestCase {
//
//    private SourceKey sourceKey = new SourceKey("A");
//    private SwiftConfigService configService;
//    @Override
//    public void setUp() throws Exception {
//        super.setUp();
//        TestConfDb.setConfDb();
//        configService = SwiftConfigServiceProvider.getInstance();
//    }
//
//    public void testAddAndGet() {
//        configService.addMetaData(sourceKey.getId(), MetaDataCreater.getMA());
//
//        assertEquals(configService.getAllMetaData().size(), 1);
//
//        IMetaData metaData = configService.getMetaDataByKey(sourceKey.getId());
//        assertMetaDataSame(MetaDataCreater.getMA(), metaData);
//    }
//
//    public void testAddAndRemove() {
//        configService.addMetaData(sourceKey.getId(), MetaDataCreater.getMA());
//        assertEquals(configService.getAllMetaData().size(), 1);
//        configService.removeMetaDatas(sourceKey.getId());
//        assertEquals(configService.getAllMetaData().size(), 0);
//    }
//
//    public void testAddAndModify() {
//        IMetaData before = MetaDataCreater.getMA();
//        configService.addMetaData(sourceKey.getId(), MetaDataCreater.getMA());
//        IMetaData metaData1 = configService.getMetaDataByKey(sourceKey.getId());
//        assertMetaDataSame(before, metaData1);
//        configService.updateMetaData(sourceKey.getId(), MetaDataCreater.getMAModify());
//
//        IMetaData metaData2 = configService.getMetaDataByKey(sourceKey.getId());
//        assertMetaDataNotSame(before, metaData2);
//        assertMetaDataSame(MetaDataCreater.getMAModify(), metaData2);
//    }
//
//    private void assertMetaDataSame(IMetaData source, IMetaData dest) {
//        assertEquals(source.getTableName(), dest.getTableName());
//        assertEquals(source.getRemark(), dest.getRemark());
//        assertEquals(source.getSchema(), dest.getSchema());
//        assertEquals(source.getFieldList().size(), dest.getFieldList().size());
//    }
//
//    private void assertMetaDataNotSame(IMetaData metaData1, IMetaData metaData2) {
//        assertEquals(metaData1.getTableName(), metaData2.getTableName());
//        assertNotSame(metaData1.getRemark(), metaData2.getRemark());
//        assertNotSame(metaData1.getSchema(), metaData2.getSchema());
//        assertEquals(metaData1.getFieldList().size(), metaData2.getFieldList().size());
//    }
//}
