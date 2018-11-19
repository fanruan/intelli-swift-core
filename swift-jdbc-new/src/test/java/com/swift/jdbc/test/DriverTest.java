package com.swift.jdbc.test;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static junit.framework.TestCase.assertNotNull;

/**
 * @author yee
 * @date 2018/11/16
 */
public class DriverTest {
    @Test
    public void testDriver() throws SQLException, ClassNotFoundException {
        Class.forName("com.swift.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:swift:remote://192.168.0.1:7000/DB?connMaxIdle=50&timeout=30000&sttmMaxIdle=200", "root", "password");
        assertNotNull(connection);
    }
}
