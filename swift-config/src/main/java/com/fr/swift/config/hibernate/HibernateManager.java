package com.fr.swift.config.hibernate;

import com.fr.swift.config.entity.SwiftMetaDataEntity;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.config.entity.SwiftServiceInfoEntity;
import com.fr.third.org.hibernate.Session;
import com.fr.third.org.hibernate.SessionFactory;
import com.fr.third.org.hibernate.Transaction;
import com.fr.third.org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import com.fr.third.org.hibernate.cfg.Configuration;
import com.fr.third.org.hibernate.service.ServiceRegistry;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.context.annotation.Bean;
import com.fr.third.springframework.stereotype.Service;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/6/29
 */
@Service
public class HibernateManager {
    @Autowired
    private SwiftConfigProperties properties;

    public HibernateManager() {

    }

    @Bean
    public Configuration getConfiguration() {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(SwiftMetaDataEntity.class);
        configuration.addAnnotatedClass(SwiftSegmentEntity.class);
        configuration.addAnnotatedClass(SwiftServiceInfoEntity.class);
        configuration.setProperties(properties.getProperties());
        return configuration;
    }

    @Bean
    public SessionFactory getFactory() {
        ServiceRegistry registry = new StandardServiceRegistryBuilder().applySettings(
                getConfiguration().getProperties()).build();
        return getConfiguration().buildSessionFactory(registry);
    }

    public <T> T doTransactionIfNeed(HibernateWorker<T> worker) throws SQLException {
        Session session = getFactory().openSession();
        if (worker.needTransaction()) {
            Transaction tx = session.beginTransaction();
            try {
                T result = worker.work(session);
                tx.commit();
                return result;
            } catch (Throwable throwable) {
                tx.rollback();
                throw new SQLException(throwable);
            } finally {
                session.close();
            }
        } else {
            try {
                return worker.work(session);
            } finally {
                session.close();
            }
        }
    }

    public interface HibernateWorker<T> {
        boolean needTransaction();

        T work(Session session) throws SQLException;
    }
}
