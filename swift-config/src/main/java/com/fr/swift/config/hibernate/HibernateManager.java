package com.fr.swift.config.hibernate;

import com.fr.swift.config.entity.SwiftConfigEntity;
import com.fr.swift.config.entity.SwiftMetaDataEntity;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.config.entity.SwiftSegmentLocationEntity;
import com.fr.swift.config.entity.SwiftServiceInfoEntity;
import com.fr.swift.config.indexing.impl.SwiftColumnIndexingConf;
import com.fr.swift.config.indexing.impl.SwiftTableIndexingConf;
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
        configuration.addAnnotatedClass(SwiftConfigEntity.class);
        configuration.addAnnotatedClass(SwiftMetaDataEntity.class);
        configuration.addAnnotatedClass(SwiftSegmentEntity.class);
        configuration.addAnnotatedClass(SwiftServiceInfoEntity.class);
        configuration.addAnnotatedClass(SwiftSegmentLocationEntity.class);

        configuration.addAnnotatedClass(SwiftTableIndexingConf.class);
        configuration.addAnnotatedClass(SwiftColumnIndexingConf.class);

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
