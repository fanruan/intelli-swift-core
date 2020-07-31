package com.fr.swift.property;

import com.fr.swift.config.ConfigInputUtil;
import com.fr.swift.util.Crasher;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Properties;

/**
 * @author Moira
 * @date 2020/7/21
 * @description
 * @since swift-1.2.0
 */
public class MigrateProperty {
    private Properties properties;
    private String lifeCycle;
    private String backupPath;
    private int day;
    private String startTime;
    private int maxNum;


    private static final MigrateProperty INSTANCE = new MigrateProperty();

    public static MigrateProperty get() {
        return INSTANCE;
    }

    private MigrateProperty() {
        properties = new Properties();
        InputStream swiftIn = ConfigInputUtil.getConfigInputStream("migrate.properties");
        try {
            properties.load(swiftIn);
            day = Integer.parseInt(properties.getProperty("start.day"));
            lifeCycle = properties.getProperty("life.cycle");
            backupPath = properties.getProperty("backup.path");
            startTime = properties.getProperty("start.time");
            maxNum = Integer.parseInt(properties.getProperty("migrate.num"));
        } catch (IOException e) {
            Crasher.crash(e);
        }
    }

    public String getLifeCycle() {
        return lifeCycle;
    }

    public String getBackupPath() {
        return backupPath;
    }

    public int getDay() {
        return day;
    }

    public long getStartTime() {
        String[] split = startTime.split(":");
        if (split.length == 3) {
            LocalDate localDate = LocalDate.now();
            final LocalDateTime localDateTime = localDate.atTime(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
            return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }
        return 0;
    }

    public int getMaxNum() {
        return maxNum;
    }

}
