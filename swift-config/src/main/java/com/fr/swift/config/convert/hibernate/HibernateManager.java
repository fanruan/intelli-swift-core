package com.fr.swift.config.convert.hibernate;

import com.fr.swift.config.SwiftConfigConstants;
import com.fr.third.org.hibernate.SessionFactory;
import com.fr.third.org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import com.fr.third.org.hibernate.cfg.Configuration;
import com.fr.third.org.hibernate.service.ServiceRegistry;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.context.annotation.Bean;

/**
 * @author yee
 * @date 2018/6/29
 */
@com.fr.third.springframework.context.annotation.Configuration
public class HibernateManager {
    @Autowired
    private SwiftConfigProperties properties;

    public HibernateManager() {

    }

    @Bean
    public Configuration getConfiguration() {
        Configuration configuration = new Configuration();
        for (Class<?> entity : SwiftConfigConstants.ENTITIES) {
            configuration.addAnnotatedClass(entity);
        }
        configuration.setProperties(properties.getProperties());
        return configuration;
    }

    @Bean(name = "swiftConfigSessionFactory")
    public SessionFactory getFactory() {
        ServiceRegistry registry = new StandardServiceRegistryBuilder().applySettings(
                getConfiguration().getProperties()).build();
        return getConfiguration().buildSessionFactory(registry);
    }
}
