package com.fr.swift.cloud.config;

import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.property.SwiftProperty;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @author Heng.J
 * @date 2019/10/14
 * @description 读取配置文件的工具类
 * @since swift 1.1
 */
public class ConfigInputUtil {

    public static InputStream getConfigInputStream(String fileName) {
        InputStream tempIn = null;
        try {
            SwiftLoggers.getLogger().info("read external {} !", fileName);
            tempIn = new BufferedInputStream(new FileInputStream(fileName));
        } catch (FileNotFoundException e) {
            SwiftLoggers.getLogger().info("Failed to read external {}, read internal {} instead!", fileName, fileName);
            tempIn = SwiftProperty.class.getClassLoader().getResourceAsStream(fileName);
        }
        return tempIn;
    }
}
