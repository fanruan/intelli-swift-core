package com.fr.swift.cloud.result;

import com.fr.swift.cloud.result.table.ExecutionMetric;
import com.fr.swift.cloud.result.table.LatencyTopPercentileStatistic;
import com.fr.swift.cloud.result.table.TemplateAnalysisResult;
import com.fr.swift.cloud.result.table.TemplateProperty;
import com.fr.swift.cloud.result.table.TemplatePropertyRatio;
import com.fr.swift.cloud.result.table.CustomerInfo;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.File;

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
        Configuration configuration = new Configuration();
        // 默认从进程跟目录读
        File confFile = new File("archive.cfg.xml");
        if (confFile.exists()) {
            configuration.configure(confFile);
        } else {
            configuration.configure("archive.cfg.xml");
        }
        configuration.addAnnotatedClass(LatencyTopPercentileStatistic.class);
        configuration.addAnnotatedClass(TemplateAnalysisResult.class);
        configuration.addAnnotatedClass(ExecutionMetric.class);
        configuration.addAnnotatedClass(TemplateProperty.class);
        configuration.addAnnotatedClass(TemplatePropertyRatio.class);
        configuration.addAnnotatedClass(CustomerInfo.class);
        return configuration.buildSessionFactory();
    }
}
