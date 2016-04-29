package com.fr.bi.conf.fs.develop;


import com.fr.bi.stable.constant.BIBaseConstant;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Connery on 2014/12/31.
 */
public class DeveloperConfig {
    public static final String FILESEPARATOR = System.getProperty("file.separator");
    private static DeveloperConfig instance;
    private boolean developerMode = false;
    private String host;
    private String service;
    private String port;
    private String name;
    private String password;
    private String testFoldPath;

    private DeveloperConfig() {

        Properties properties = new Properties();
        try {
            InputStream in = DeveloperConfig.class.getClassLoader().getResourceAsStream("dev_config.properties");
            if (BIBaseConstant.BI_MODEL == BIBaseConstant.HIHIDATA) {
                return;
            }
            //    properties.load(in);
            host = properties.getProperty("host");
            service = properties.getProperty("service");
            port = properties.getProperty("port");
            name = properties.getProperty("name");
            password = properties.getProperty("password");
            testFoldPath = properties.getProperty("testFoldPath");
            File file = new File(getTestFoldPath());
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception ex) {
//             BILogger.getLogger().error(e.getMessage(), e);
//            System.out.print(ex.toString());
        }
    }

    public static DeveloperConfig getInstance() {
        if (instance == null) {
            instance = new DeveloperConfig();
        }
        return instance;
    }

    public static String getTestBasicPath() {
        return DeveloperConfig.getInstance().getTestFoldPath();
    }

    public String getHost() {
        return host;
    }

    public String getService() {
        return service;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return Integer.parseInt(port);
    }

    public String getTestFoldPath() {
        return testFoldPath;
    }

    public void turnOnDeveloperMode() {
        this.developerMode = true;
    }

    public void turnOffDeveloperMode() {
        this.developerMode = false;
    }

    public boolean isDeveloperMode() {
        return this.developerMode;
    }
}