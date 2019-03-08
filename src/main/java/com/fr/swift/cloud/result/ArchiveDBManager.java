package com.fr.swift.cloud.result;

import com.fr.swift.cloud.result.table.ExecutionMetric;
import com.fr.swift.cloud.result.table.LatencyTopPercentileStatistic;
import com.fr.swift.cloud.result.table.TemplateAnalysisResult;
import com.fr.swift.cloud.result.table.TemplateProperty;
import com.fr.swift.cloud.result.table.TemplatePropertyRatio;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Created by lyon on 2019/3/1.
 */
public enum ArchiveDBManager {

    INSTANCE;

    private static volatile SessionFactory sessionFactory;

    public synchronized SessionFactory getFactory() {
        if (null == sessionFactory) {
            sessionFactory = initSessionFactory();
        }
        return sessionFactory;
    }

    private SessionFactory initSessionFactory() {
        Configuration configuration = new Configuration().configure("archive.cfg.xml");
        configuration.addAnnotatedClass(LatencyTopPercentileStatistic.class);
        configuration.addAnnotatedClass(TemplateAnalysisResult.class);
        configuration.addAnnotatedClass(ExecutionMetric.class);
        configuration.addAnnotatedClass(TemplateProperty.class);
        configuration.addAnnotatedClass(TemplatePropertyRatio.class);
        return configuration.buildSessionFactory();
    }
}
