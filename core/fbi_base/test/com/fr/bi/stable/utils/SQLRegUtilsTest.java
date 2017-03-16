package com.fr.bi.stable.utils;

import junit.framework.TestCase;

/**
 * Created by kary on 16/7/15.
 */
public class SQLRegUtilsTest extends TestCase {
    private String correctStr="Select c1,c2,c3 From t1,t2,t3 Where condi1=5 and condi6=6 or condi7=7 Group  by g1,g2,g3 order  by g2,g3";
    public SQLRegUtilsTest() {

    }

    public void testIsSql() throws Exception {
        SQLRegUtils sqlRegUtils = new SQLRegUtils(correctStr);
        assertTrue(sqlRegUtils.isSql());
        String errorSql="Select  t1,t2,t3  condi1=5 and condi6=6 or condi7=7 Group  by g1,g2,g3 order  by g2,g3";
        SQLRegUtils sqlRegUtils2 = new SQLRegUtils(errorSql);
        assertFalse(sqlRegUtils2.isSql());
    }
public void testSqlPart(){
    SQLRegUtils sqlRegUtils = new SQLRegUtils(correctStr);
    assertTrue(sqlRegUtils.getCols().equals(" c1,c2,c3 "));
    assertTrue(sqlRegUtils.getConditions().equals(" condi1=5 and condi6=6 or condi7=7 "));
    assertTrue(sqlRegUtils.getGroupCols().equals(" g1,g2,g3 "));
    assertTrue(sqlRegUtils.getOrderCols().equals(" g2,g3"));
}
}
