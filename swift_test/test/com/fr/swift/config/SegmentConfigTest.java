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
import com.fr.swift.config.conf.SegmentConfig;
import com.fr.swift.config.unique.SegmentUnique;
import junit.framework.TestCase;

import java.util.List;

/**
 * @author yee
 * @date 2018/3/9
 */
public class SegmentConfigTest extends TestCase {


    public void setUp() throws Exception {
        super.setUp();
        DBOption dbOption = new DBOption();
        dbOption.setPassword("");
        dbOption.setDialectClass("com.fr.third.org.hibernate.dialect.H2Dialect");
        dbOption.setDriverClass("org.h2.Driver");
        dbOption.setUsername("sa");
        dbOption.setUrl("jdbc:h2:~/config");
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
        SegmentUnique source = SegmentCreater.getSegment();
        SegmentConfig.getInstance().addSegments(source);

        assertEquals(SegmentConfig.getInstance().getAllSegments().size(), 1);

        IConfigSegment unique = SegmentConfig.getInstance().getSegmentByKey(source.getSourceKey());
        assertEquals(unique.getSourceKey(), source.getSourceKey());
        assertSegmentSame(source, unique);
    }

    public void testAddAndRemove() {
        SegmentUnique source = SegmentCreater.getSegment();
        SegmentConfig.getInstance().addSegments(source);
        assertEquals(SegmentConfig.getInstance().getAllSegments().size(), 1);
        SegmentConfig.getInstance().removeSegment(source.getSourceKey());
        assertEquals(SegmentConfig.getInstance().getAllSegments().size(), 0);
    }

    /**
     * h2 获取的都是修改之后的数据，即target1 和 target2获取的数据都是修改后的
     */
    public void testAddAndModify() {
        SegmentUnique source = SegmentCreater.getSegment();
        SegmentConfig.getInstance().addSegments(source);
        IConfigSegment target1 = SegmentConfig.getInstance().getSegmentByKey(source.getSourceKey());
        SegmentUnique modify = SegmentCreater.getModify();

        assertSegmentSame(source, target1);

        SegmentConfig.getInstance().modifySegment(modify);

        IConfigSegment target2 = SegmentConfig.getInstance().getSegmentByKey(source.getSourceKey());

        assertEquals(SegmentConfig.getInstance().getAllSegments().size(), 1);
        assertEquals(source.getSourceKey(), target2.getSourceKey());
        assertSegmentNotSame(source, target2);
        assertSegmentSame(modify, target2);
    }

    private void assertSegmentSame(IConfigSegment source, IConfigSegment dest) {
        List<ISegmentKey> keyUniques = dest.getSegments();
        List<ISegmentKey> sourceKeyUniques = source.getSegments();
        assertEquals(keyUniques.size(), sourceKeyUniques.size());
        for (int i = 0, len = sourceKeyUniques.size(); i < len; i++) {
            ISegmentKey key = sourceKeyUniques.get(i);
            ISegmentKey target = keyUniques.get(i);
            assertEquals(target.getName(), key.getName());
            assertEquals(target.getSegmentOrder(), key.getSegmentOrder());
            assertEquals(target.getSourceId(), key.getSourceId());
            assertEquals(target.getStoreType(), key.getStoreType());
            assertEquals(target.getUri(), key.getUri());
        }
    }

    private void assertSegmentNotSame(IConfigSegment source, IConfigSegment dest) {
        List<ISegmentKey> keyUniques = dest.getSegments();
        List<ISegmentKey> sourceKeyUniques = source.getSegments();
        assertEquals(keyUniques.size(), sourceKeyUniques.size());
        for (int i = 0, len = sourceKeyUniques.size(); i < len; i++) {
            ISegmentKey key = sourceKeyUniques.get(i);
            ISegmentKey target = keyUniques.get(i);
            assertNotSame(target.getName(), key.getName());
            assertEquals(target.getSegmentOrder(), key.getSegmentOrder());
            assertEquals(target.getSourceId(), key.getSourceId());
            assertEquals(target.getStoreType(), key.getStoreType());
            assertNotSame(target.getUri(), key.getUri());
        }
    }
}