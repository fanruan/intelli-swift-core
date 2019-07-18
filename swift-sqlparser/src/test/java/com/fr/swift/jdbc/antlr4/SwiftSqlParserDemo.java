package com.fr.swift.jdbc.antlr4;

import org.antlr.v4.gui.TestRig;

/**
 * @author anchore
 * @date 2019/7/17
 */
public class SwiftSqlParserDemo {

    public static void main(String[] args) throws Exception {
        String testClassesDir = System.getProperty("user.dir") + "/target/test-classes/";
        TestRig.main(new String[]{
                "com.fr.swift.jdbc.antlr4.SwiftSql", "root", "-gui", testClassesDir + "sqlparser-demo.sql"
        });
    }
}