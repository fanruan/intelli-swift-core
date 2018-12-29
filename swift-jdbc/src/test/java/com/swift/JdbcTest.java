package com.swift;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author yee
 * @date 2018-12-27
 */
public class JdbcTest {
    @Test
    public void test() throws SQLException, ClassNotFoundException {
        Class.forName("com.fr.swift.jdbc.Driver");
        Connection connector = DriverManager.getConnection("jdbc:swift:remote://192.168.1.67:7000/cube");

        Statement statement = connector.createStatement();
        ResultSet resultSet = statement.executeQuery("select count(id) from test_yiguan");
        int i = 0;
        while (resultSet.next() && i++ < 100) {
            System.out.println(resultSet.getObject(1));
        }
        resultSet.close();
//        statement.executeUpdate("create table test_create (id int, name varchar)");
//        System.out.println(statement.executeUpdate("drop table test_create"));
    }

    @Test
    public void netty() {

    }
}
