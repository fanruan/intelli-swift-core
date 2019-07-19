package com.fr.swift.jdbc.antlr4;

import org.antlr.v4.Tool;

/**
 * @author anchore
 * @date 2019/7/17
 * <p>
 * 生成parser的工具
 */
public class Antlr4CodeGen {
    public static void main(String[] args) {
        String classesDir = System.getProperty("user.dir") + "/target/classes/";
        String outputDir = System.getProperty("user.dir") + "/intelli-swift-core/swift-sqlparser/src/main/java/com/fr/swift/jdbc/antlr4";
        Tool.main(new String[]{
                "-package", "com.fr.swift.jdbc.antlr4",
                "-o", outputDir, classesDir + "SwiftSqlLexer.g4", classesDir + "SwiftSqlParser.g4"
        });
    }
}