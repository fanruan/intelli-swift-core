package com.fr.swift.executor.task.util;

import java.util.UUID;

/**
 * @author Hoky
 * @date 2020/7/21
 * @description
 * @since swift-1.2.0
 */
public class IDUtil {

    public static String randomId() {
        return UUID.randomUUID().toString().split("-")[0];
    }

}
