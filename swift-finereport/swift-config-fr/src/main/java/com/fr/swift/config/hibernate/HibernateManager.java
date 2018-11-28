package com.fr.swift.config.hibernate;

import com.fr.swift.config.SwiftConfigConstants;
import com.fr.third.org.hibernate.SessionFactory;
import com.fr.third.org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import com.fr.third.org.hibernate.cfg.Configuration;
import com.fr.third.org.hibernate.service.ServiceRegistry;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.util.Properties;

/**
 * @author yee
 * @date 2018/6/29
 */
@Service
public class HibernateManager {
    private SessionFactory sessionFactory;
    @Autowired(required = false)
    private SwiftConfigProperties properties;

    public HibernateManager() {

    }

    public Configuration getConfiguration(Properties properties) {
        Configuration configuration = new Configuration();
        for (Class<?> entity : SwiftConfigConstants.ENTITIES) {
            configuration.addAnnotatedClass(entity);
        }
        configuration.setProperties(properties);
        return configuration;
    }

    synchronized
    public SessionFactory getFactory() {
        Properties prop = properties.reConfiguration();
        if (null != prop) {
            if (null != sessionFactory) {
                sessionFactory.close();
            }
            sessionFactory = initSessionFactory(prop);
        } else if (null == sessionFactory) {
            sessionFactory = initSessionFactory(properties.getProperties());
        }
        return sessionFactory;
    }

    private SessionFactory initSessionFactory(Properties properties) {
        Configuration configuration = getConfiguration(properties);
        ServiceRegistry registry = new StandardServiceRegistryBuilder().applySettings(
                configuration.getProperties()).build();
        return configuration.buildSessionFactory(registry);
    }
}
