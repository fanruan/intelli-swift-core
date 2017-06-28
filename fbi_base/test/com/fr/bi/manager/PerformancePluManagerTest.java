package com.fr.bi.manager;

import java.io.*;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Kary on 2017/6/28.
 */
public class PerformancePluManagerTest extends PerformancePlugManager {

    PerformancePluManagerTest() {
        super();
    }

    protected void initInputStream() {
        try {
            in=new FileInputStream(this.getClass().getResource("").getPath()+ File.separator+"plugs.properties");
        } catch (Exception e) {
            in = emptyInputStream();
        }
    }
    protected boolean saveProperties(Map<String, Object> configs) {
        OutputStream out = null;
        try {
            readWriteLock.writeLock().lock();
            out = new FileOutputStream(this.getClass().getResource("").getPath()+ File.separator+"plugs.properties");
            Properties properties = new Properties();
            for (String key : configs.keySet()) {
                if (PropertyValidiationCheck(key)) {
                    properties.setProperty("performance."+key, String.valueOf(configs.get(key)));
                }
            }
            properties.store(out, "");
        } catch (Exception e) {
            return false;
        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
            readWriteLock.writeLock().unlock();
        }
        return true;
    }
}
