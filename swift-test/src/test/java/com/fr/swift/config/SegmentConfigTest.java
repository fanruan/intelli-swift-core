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
//import com.fr.swift.config.unique.SegmentUnique;
//import junit.framework.TestCase;
//
//import java.util.List;
//
///**
// * @author yee
// * @date 2018/3/9
// */
//public class SegmentConfigTest extends TestCase {
//
//    private SwiftConfigService configService;
//
//    public void setUp() throws Exception {
//        super.setUp();
//        TestConfDb.setConfDb();
//        configService = SwiftConfigServiceProvider.getInstance();
//    }
//
//    public void testAddAndGet() {
//        SegmentUnique source = SegmentCreater.getSegment();
//        configService.addSegments(source);
//
//        assertEquals(configService.getAllSegments().size(), 1);
//
//        IConfigSegment unique = configService.getSegmentByKey(source.getSourceKey());
//        assertEquals(unique.getSourceKey(), source.getSourceKey());
//        assertSegmentSame(source, unique);
//    }
//
//    public void testAddAndRemove() {
//        SegmentUnique source = SegmentCreater.getSegment();
//        configService.addSegments(source);
//        assertEquals(configService.getAllSegments().size(), 1);
//        configService.removeSegments(source.getSourceKey());
//        assertEquals(configService.getAllSegments().size(), 0);
//    }
//
//    public void testAddAndModify() {
//        SegmentUnique source = SegmentCreater.getSegment();
//        configService.addSegments(source);
//        IConfigSegment target1 = configService.getSegmentByKey(source.getSourceKey());
//        SegmentUnique modify = SegmentCreater.getModify();
//
//        assertSegmentSame(source, target1);
//
//        configService.updateSegments(modify);
//
//        IConfigSegment target2 = configService.getSegmentByKey(source.getSourceKey());
//
//        assertEquals(configService.getAllSegments().size(), 1);
//        assertEquals(source.getSourceKey(), target2.getSourceKey());
//        assertSegmentNotSame(target1, target2);
//        assertSegmentSame(modify, target2);
//        assertSegmentSame(source, target1);
//    }
//
//    private void assertSegmentSame(IConfigSegment source, IConfigSegment dest) {
//        List<ISegmentKey> keyUniques = dest.getSegments();
//        List<ISegmentKey> sourceKeyUniques = source.getSegments();
//        assertEquals(keyUniques.size(), sourceKeyUniques.size());
//        for (int i = 0, len = sourceKeyUniques.size(); i < len; i++) {
//            ISegmentKey key = sourceKeyUniques.get(i);
//            ISegmentKey target = keyUniques.get(i);
//            assertEquals(target.getName(), key.getName());
//            assertEquals(target.getSegmentOrder(), key.getSegmentOrder());
//            assertEquals(target.getSourceId(), key.getSourceId());
//            assertEquals(target.getStoreType(), key.getStoreType());
//            assertEquals(target.getUri(), key.getUri());
//        }
//    }
//
//    private void assertSegmentNotSame(IConfigSegment source, IConfigSegment dest) {
//        List<ISegmentKey> keyUniques = dest.getSegments();
//        List<ISegmentKey> sourceKeyUniques = source.getSegments();
//        assertEquals(keyUniques.size(), sourceKeyUniques.size());
//        for (int i = 0, len = sourceKeyUniques.size(); i < len; i++) {
//            ISegmentKey key = sourceKeyUniques.get(i);
//            ISegmentKey target = keyUniques.get(i);
//            assertNotSame(target.getName(), key.getName());
//            assertEquals(target.getSegmentOrder(), key.getSegmentOrder());
//            assertEquals(target.getSourceId(), key.getSourceId());
//            assertEquals(target.getStoreType(), key.getStoreType());
//            assertNotSame(target.getUri(), key.getUri());
//        }
//    }
//}