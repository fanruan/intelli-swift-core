package com.fr.swift.jdbc.checker.impl;

import com.fr.swift.jdbc.checker.GrammarChecker;
import com.fr.swift.jdbc.sql.SwiftPreparedStatement;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author yee
 * @date 2018-12-24
 */
public class SwiftGrammarCheckerTest {
    private GrammarChecker checker = new SwiftGrammarChecker();

    @Test
    public void check() {
        String normal = "select * from tableA";
        boolean check = false;
        try {
            checker.check(normal);
        } catch (SQLException e) {
            check = true;
        }
        assertFalse(check);
        String param = "select * from tableA where a= ? and b=?";
        try {
            checker.check(param, "paramA", "paramB");
        } catch (SQLException e) {
            check = true;
        }
        assertFalse(check);
        try {
            checker.check(param, "paramA", SwiftPreparedStatement.NullValue.INSTANCE);
        } catch (SQLException e) {
            check = true;
        }
        assertTrue(check);
        check = false;
        try {
            checker.check(param);
        } catch (SQLException e) {
            check = true;
        }
        assertTrue(check);
    }
}