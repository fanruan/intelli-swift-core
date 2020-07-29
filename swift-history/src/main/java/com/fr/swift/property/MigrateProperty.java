package com.fr.swift.property;

import com.fr.swift.config.ConfigInputUtil;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.Crasher;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * @author Moira
 * @date 2020/7/21
 * @description
 * @since swift-1.2.0
 */
public class MigrateProperty {
    private Properties properties;
    private String lifeCircle;
    private boolean isRemote;
    private String backupPath;
    private int day;
    private String startTime;
    private int maxNum;
    private String backupCluster;


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
            lifeCircle = properties.getProperty("life.circle");
            isRemote = Boolean.parseBoolean(properties.getProperty("remote"));
            backupPath = properties.getProperty("backup.path");
            startTime = properties.getProperty("start.time");
            maxNum = Integer.parseInt(properties.getProperty("migrate.num"));
            backupCluster = properties.getProperty("backup.cluster");
        } catch (IOException e) {
            Crasher.crash(e);
        }
    }

    public String getLifeCircle() {
        return lifeCircle;
    }

    public boolean isRemote() {
        return isRemote;
    }

    public String getBackupPath() {
        return backupPath;
    }

    public int getDay() {
        return day;
    }

    public long getStartTime() {
        DateFormat DATE_FORMAT = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        final DateFormat DAY_FORMAT = new SimpleDateFormat("yy-MM-dd");
        Date currentDate;
        try {
            currentDate = DATE_FORMAT.parse(DAY_FORMAT.format(new Date()) + " " + startTime);
        } catch (ParseException e) {
            SwiftLoggers.getLogger().error(e.getMessage());
            return 0;
        }
        return currentDate.getTime();
    }

    public int getMaxNum() {
        return maxNum;
    }

    public String getBackupCluster() {
        return backupCluster;
    }
}
