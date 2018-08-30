package com.fr.swift.jdbc;

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author pony
 * @date 2018/8/17
 */
public class Driver extends NoRegisterDriver {

    static {
        try {
            DriverManager.registerDriver(new Driver());
        } catch (SQLException e) {
            throw new RuntimeException("Can't register driver!");
        }
    }
}
