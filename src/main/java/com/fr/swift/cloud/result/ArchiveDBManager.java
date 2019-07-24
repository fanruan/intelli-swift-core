package com.fr.swift.cloud.result;

import com.fr.swift.cloud.result.table.ConfEntity;
import com.fr.swift.cloud.result.table.CustomerBaseInfo;
import com.fr.swift.cloud.result.table.CustomerInfo;
import com.fr.swift.cloud.result.table.FunctionUsageRate;
import com.fr.swift.cloud.result.table.PluginUsage;
import com.fr.swift.cloud.result.table.SystemUsageInfo;
import com.fr.swift.cloud.result.table.TemplateUsageInfo;
import com.fr.swift.cloud.result.table.downtime.DowntimeExecutionResult;
import com.fr.swift.cloud.result.table.downtime.DowntimeResult;
import com.fr.swift.cloud.result.table.template.ExecutionMetric;
import com.fr.swift.cloud.result.table.template.LatencyTopPercentileStatistic;
import com.fr.swift.cloud.result.table.template.TemplateAnalysisResult;
import com.fr.swift.cloud.result.table.template.TemplateProperty;
import com.fr.swift.cloud.result.table.template.TemplatePropertyRatio;
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
        configuration.addAnnotatedClass(DowntimeResult.class);
        configuration.addAnnotatedClass(DowntimeExecutionResult.class);

        configuration.addAnnotatedClass(CustomerBaseInfo.class);
        configuration.addAnnotatedClass(FunctionUsageRate.class);
        configuration.addAnnotatedClass(ConfEntity.class);
        configuration.addAnnotatedClass(PluginUsage.class);
        configuration.addAnnotatedClass(SystemUsageInfo.class);
        configuration.addAnnotatedClass(TemplateUsageInfo.class);

        return configuration.buildSessionFactory();
    }
}
