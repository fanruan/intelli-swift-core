package com.fr.bi.manager;

import com.fr.base.FRContext;
import com.fr.stable.StringUtils;
import com.fr.stable.project.ProjectConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Created by neil on 2017/8/4.
 */
public class DBCPConnectionPlugManager implements DBCPConnectionPlugInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(DBCPConnectionPlugManager.class);
    public static final String FILE_NAME = "dbcp.properties";
    private static final String FILE_PATH = FRContext.getCurrentEnv().getPath();
    public static final String NOT_TEST_ON_BORROW = "dbcp.avoidTestOnBorrow";

    private Properties properties;

    public DBCPConnectionPlugManager() {
        init();
    }

    @Override
    public Set<String> getDriversNotTestOnBorrow() {
        Set<String> values = new HashSet<String>();
        String avoidTestOnBorrow = properties.getProperty(NOT_TEST_ON_BORROW);

        if (avoidTestOnBorrow != null) {
            String[] splitedValues = avoidTestOnBorrow.split(";");
            for (String splitedValue : splitedValues) {
                String value = splitedValue.trim();
                if (!StringUtils.isEmpty(value)) {
                    values.add(value);
                }
            }
        }
        return values;
    }

    private void init() {
        try {
            File file = new File(FILE_PATH + File.separator + ProjectConstants.RESOURCES_NAME + File.separator + FILE_NAME);
            properties = new Properties();
            if (file.exists()) {
                InputStream in = new FileInputStream(file);
                properties.load(in);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }


}
