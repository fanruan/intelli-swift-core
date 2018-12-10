package com.fr.swift.util.qm.cal;

import com.fr.swift.util.qm.bool.BExpr;
import com.fr.swift.util.qm.bool.BExprConverter;
import com.fr.swift.util.qm.bool.BExprType;
import com.fr.swift.util.qm.bool.BNExpr;
import com.fr.swift.util.qm.bool.BVar;
import junit.framework.TestCase;

import java.util.List;

/**
 * Created by Lyon on 2018/7/11.
 */
public class QMUtilsTest extends TestCase {

    public void test() throws Exception {
        BExpr a = new MinTermsUtilsTest.VarExpr("a");
        BExpr b = new MinTermsUtilsTest.VarExpr("b");
        BExpr c = new MinTermsUtilsTest.VarExpr("c");

        // 当前测试用例map中a -> 低位，c -> 高位。a -> bit1，a' -> bit0
        // function(a, b, c) = ab + bc + a'b'c' = abc + abc' + a'bc + a'b'c' = cba + c'ba + cba' + c'b'a' = m(7, 3, 6, 0)
        BExpr and1 = new MinTermsUtilsTest.AndExpr(MinTermsUtilsTest.item2List(a, b));
        BExpr and2 = new MinTermsUtilsTest.AndExpr(MinTermsUtilsTest.item2List(b, c));
        BExpr and3 = new MinTermsUtilsTest.AndExpr(MinTermsUtilsTest.item2List(new MinTermsUtilsTest.NotExpr(a), new MinTermsUtilsTest.NotExpr(b), new MinTermsUtilsTest.NotExpr(c)));
        BExpr or = new MinTermsUtilsTest.OrExpr(MinTermsUtilsTest.item2List(and1, and2, and3));

        BExprConverter converter = new BExprConverter() {
            @Override
            public BExpr convertAndExpr(List<? extends BExpr> items) {
                return new MinTermsUtilsTest.AndExpr(items);
            }

            @Override
            public BExpr convertOrExpr(List<? extends BExpr> items) {
                return new MinTermsUtilsTest.OrExpr(items);
            }

            @Override
            public BExpr convertNotExpr(BVar var) {
                return new MinTermsUtilsTest.NotExpr(var);
            }
        };
        // 化简结果还是ab + bc + a'b'c'
        BExpr simplified = QMUtils.simplify(or, converter);
        assertTrue(simplified.type() == BExprType.OR);
        assertEquals(((BNExpr) simplified).getChildrenExpr().size(), 3);
        for (BExpr expr : ((BNExpr) simplified).getChildrenExpr()) {
            assertTrue(expr.type() == BExprType.AND);
            if (((BNExpr) expr).getChildrenExpr().size() == 3) {
                for (BExpr expr1 : ((BNExpr) expr).getChildrenExpr()) {
                    assertTrue(expr1.type() == BExprType.NOT);
                }
                continue;
            }
            assertTrue(((BNExpr) expr).getChildrenExpr().size() == 2);
        }

        // ab + ab' + ac = a = abc + abc' + ab'c + ab'c' = cba + c'ba + cb'a + c'b'a = m(7, 3, 5, 1)
        and1 = new MinTermsUtilsTest.AndExpr(MinTermsUtilsTest.item2List(a, b));
        and2 = new MinTermsUtilsTest.AndExpr(MinTermsUtilsTest.item2List(a, new MinTermsUtilsTest.NotExpr(b)));
        and3 = new MinTermsUtilsTest.AndExpr(MinTermsUtilsTest.item2List(a, c));
        or = new MinTermsUtilsTest.OrExpr(MinTermsUtilsTest.item2List(and1, and2, and3));
        simplified = QMUtils.simplify(or, converter);
        assertTrue(simplified.type() == BExprType.VAR);

        // ab + ab' = a = ba + b'a = m(3, 1)
        and1 = new MinTermsUtilsTest.AndExpr(MinTermsUtilsTest.item2List(a, b));
        and2 = new MinTermsUtilsTest.AndExpr(MinTermsUtilsTest.item2List(a, new MinTermsUtilsTest.NotExpr(b)));
        or = new MinTermsUtilsTest.OrExpr(MinTermsUtilsTest.item2List(and1, and2));
        simplified = QMUtils.simplify(or, converter);
        assertTrue(simplified.type() == BExprType.VAR);

        // ab + ab' + a'b + a'b' = 1 = ba + b'a + ba' + b'a' = m(3, 1, 2, 0)
        and1 = new MinTermsUtilsTest.AndExpr(MinTermsUtilsTest.item2List(a, b));
        and2 = new MinTermsUtilsTest.AndExpr(MinTermsUtilsTest.item2List(a, new MinTermsUtilsTest.NotExpr(b)));
        and3 = new MinTermsUtilsTest.AndExpr(MinTermsUtilsTest.item2List(new MinTermsUtilsTest.NotExpr(a), b));
        BExpr and4 = new MinTermsUtilsTest.AndExpr(MinTermsUtilsTest.item2List(new MinTermsUtilsTest.NotExpr(a), new MinTermsUtilsTest.NotExpr(b)));
        or = new MinTermsUtilsTest.OrExpr(MinTermsUtilsTest.item2List(and1, and2, and3, and4));
        simplified = QMUtils.simplify(or, converter);
        assertTrue(simplified.type() == BExprType.TRUE);

        // a'(ab + ac) = 0 = m()
        BExpr an = new MinTermsUtilsTest.NotExpr(a);
        BExpr ab = new MinTermsUtilsTest.AndExpr(MinTermsUtilsTest.item2List(a, b));
        BExpr ac = new MinTermsUtilsTest.AndExpr(MinTermsUtilsTest.item2List(a, c));
        or = new MinTermsUtilsTest.OrExpr(MinTermsUtilsTest.item2List(ab, ac));
        BExpr and = new MinTermsUtilsTest.AndExpr(MinTermsUtilsTest.item2List(an, or));
        simplified = QMUtils.simplify(and, converter);
        assertTrue(simplified.type() == BExprType.FALSE);
    }
}
