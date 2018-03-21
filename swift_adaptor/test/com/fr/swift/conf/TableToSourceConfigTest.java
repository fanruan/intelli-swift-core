package com.fr.swift.config;

import com.fr.config.DBEnv;
import com.fr.config.dao.DaoContext;
import com.fr.config.dao.impl.HibernateClassHelperDao;
import com.fr.config.dao.impl.HibernateEntityDao;
import com.fr.config.dao.impl.HibernateXmlEnityDao;
import com.fr.config.entity.ClassHelper;
import com.fr.config.entity.XmlEntity;
import com.fr.config.entity.Entity;
import com.fr.stable.StringUtils;
import com.fr.stable.db.DBContext;
import com.fr.stable.db.option.DBOption;
import com.fr.swift.conf.TableToSourceConfig;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/3/14
 */
public class TableToSourceConfigTest extends TestCase {
    private TableToSourceConfig config;
    @Override
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
        config = TableToSourceConfig.getInstance();
    }

    public void testAddAndGet() {
        int size = 10;
        Map<String, String> conf = Table2SourceCreator.create(size);
        Iterator<Map.Entry<String, String>> iterator = conf.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> value = iterator.next();
            config.addConfig(value.getKey(), value.getValue());
        }

        assertEquals(size, config.getAllConfig().size());
        iterator = conf.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> value = iterator.next();
            assertEquals(value.getValue(), config.getConfigByTableId(value.getKey()));
        }
    }

    public void testModifyAndGet() {
        int size = 3;
        Map<String, String> modify = Table2SourceCreator.modify(size);
        Map<String, String> conf = Table2SourceCreator.create(10);
        Iterator<Map.Entry<String, String>> iterator = conf.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> value = iterator.next();
            config.addConfig(value.getKey(), value.getValue());
        }

        assertEquals(10, config.getAllConfig().size());
        iterator = conf.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> value = iterator.next();
            assertEquals(value.getValue(), config.getConfigByTableId(value.getKey()));
        }

        iterator = modify.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> value = iterator.next();
            config.updateConfig(value.getKey(), value.getValue());
        }

        iterator = conf.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> value = iterator.next();
            String modifySource = modify.get(value.getKey());
            if (StringUtils.isEmpty(modifySource)) {
                assertEquals(value.getValue(), config.getConfigByTableId(value.getKey()));
            } else {
                assertEquals(modifySource, config.getConfigByTableId(value.getKey()));
                assertNotSame(value.getValue(), config.getConfigByTableId(value.getKey()));
            }
        }
    }

    public void testRemoveAndGet() {
        int size = 10;
        Map<String, String> conf = Table2SourceCreator.create(size);
        Iterator<Map.Entry<String, String>> iterator = conf.entrySet().iterator();
        List<String> removeKey = new ArrayList<String>();
        int i = 0;
        while (iterator.hasNext()) {
            Map.Entry<String, String> value = iterator.next();
            config.addConfig(value.getKey(), value.getValue());
            if (i++ < 3) {
                removeKey.add(value.getKey());
            }
        }

        assertEquals(size, config.getAllConfig().size());
        for (String key : removeKey) {
            config.removeConfig(key);
        }

        assertEquals(size - 3, config.getAllConfig().size());

        for (String key : removeKey) {
            assertNull(config.getConfigByTableId(key));
        }

    }

    public void testRemoveAll() {
        int size = 10;
        Map<String, String> conf = Table2SourceCreator.create(size);
        Iterator<Map.Entry<String, String>> iterator = conf.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> value = iterator.next();
            config.addConfig(value.getKey(), value.getValue());
        }
        assertEquals(size, config.getAllConfig().size());
        config.removeAllConfigs();
        assertEquals(0, config.getAllConfig().size());
    }
}