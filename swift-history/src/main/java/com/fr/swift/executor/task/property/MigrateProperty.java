package com.fr.swift.executor.task.property;

import com.fr.swift.config.ConfigInputUtil;
import com.fr.swift.util.Crasher;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Hoky
 * @date 2020/7/21
 * @description
 * @since swift-1.2.0
 */
public class MigrateProperty {
    private Properties properties;
    private int lifeCycle;
    private String backupPath;
    private int maxNum;
    /**
     * LRU系数，LRU是根据块访问时间从远到近排序，根据系数取前百分之多少
     */
    private double lruCoefficient;
    private String cornExpression;


    private static final MigrateProperty INSTANCE = new MigrateProperty();

    public static MigrateProperty get() {
        return INSTANCE;
    }

    private MigrateProperty() {
        properties = new Properties();
        InputStream swiftIn = ConfigInputUtil.getConfigInputStream("migrate.properties");
        try {
            properties.load(swiftIn);
            lifeCycle = Integer.parseInt(properties.getProperty("life.cycle"));
            backupPath = properties.getProperty("backup.path");
            maxNum = Integer.parseInt(properties.getProperty("migrate.num"));
            lruCoefficient = Double.parseDouble(properties.getProperty("lru.coefficient"));
            cornExpression = properties.getProperty("migrate.corn.expression");
        } catch (IOException e) {
            Crasher.crash(e);
        }
    }

    public int getLifeCycle() {
        return lifeCycle;
    }

    public String getBackupPath() {
        return backupPath;
    }

    public int getMaxNum() {
        return maxNum;
    }

    public double getLruCoefficient() {
        return lruCoefficient;
    }

    public String getCornExpression() {
        return cornExpression;
    }
}
