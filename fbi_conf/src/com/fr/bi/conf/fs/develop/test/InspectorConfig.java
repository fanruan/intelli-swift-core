package com.fr.bi.conf.fs.develop.test;


import com.fr.bi.conf.fs.develop.DeveloperConfig;

/**
 * Created by Connery on 2015/1/4.
 */
public class InspectorConfig {
    public static final String NAME = DeveloperConfig.getInstance().getName();
    public static final String PSW = DeveloperConfig.getInstance().getPassword();
    public static final String STANDARD_PATH = DeveloperConfig.getTestBasicPath() + DeveloperConfig.FILESEPARATOR + "standard" + DeveloperConfig.FILESEPARATOR;
    public static final String TEMP_PATH = DeveloperConfig.getTestBasicPath() + DeveloperConfig.FILESEPARATOR + "currentResult" + DeveloperConfig.FILESEPARATOR;
}