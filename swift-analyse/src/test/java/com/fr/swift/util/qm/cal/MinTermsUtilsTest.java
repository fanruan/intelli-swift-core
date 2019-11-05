package com.fr.swift.util.qm.cal;

import com.fr.swift.util.qm.bool.BExpr;
import com.fr.swift.util.qm.bool.BExprType;
import com.fr.swift.util.qm.bool.BNExpr;
import com.fr.swift.util.qm.bool.BUExpr;
import com.fr.swift.util.qm.bool.BVar;
import junit.framework.TestCase;
import org.junit.Ignore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/4/24.
 */
public class MinTermsUtilsTest extends TestCase {

    public void testCountVariables() {
        BExpr var1 = new VarExpr("a");
        Map<BVar, Integer> vars = MinTermUtils.getBVarMap(var1);
        assertTrue(vars.size() == 1);

        BExpr var2 = new VarExpr("b");
        List<BExpr> exprList = new ArrayList<BExpr>();
        exprList.add(var1);
        exprList.add(var2);
        BExpr or = new OrExpr(exprList);
        vars = MinTermUtils.getBVarMap(or);
        assertTrue(vars.size() == 2);

        BExpr var3 = new VarExpr("b");
        exprList.add(var3);
        BExpr and = new AndExpr(exprList);
        vars = MinTermUtils.getBVarMap(and);
        assertTrue(vars.size() == 2);

        BExpr var4 = new VarExpr("c");
        List<BExpr> exprList1 = new ArrayList<BExpr>();
        exprList1.addAll(exprList);
        exprList1.add(var4);
        exprList1.add(or);
        and = new AndExpr(exprList1);
        vars = MinTermUtils.getBVarMap(and);
        assertTrue(vars.size() == 3);
    }

    @Ignore
    public void testMinTerms() {
//        BExpr a = new VarExpr("a");
//        BExpr b = new VarExpr("b");
//        BExpr c = new VarExpr("c");
//
//        // 当前测试用例map中a -> 低位，c -> 高位。a -> bit1，a' -> bit0
//        // function(a, b, c) = ab + bc + a'b'c' = abc + abc' + a'bc + a'b'c' = cba + c'ba + cba' + c'b'a' = m(7, 3, 6, 0)
//        BExpr and1 = new AndExpr(item2List(a, b));
//        BExpr and2 = new AndExpr(item2List(b, c));
//        BExpr and3 = new AndExpr(item2List(new NotExpr(a), new NotExpr(b), new NotExpr(c)));
//        BExpr or = new OrExpr(item2List(and1, and2, and3));
//        Map<BVar, Integer> bVarIntegerMap = MinTermUtils.getBVarMap(or);
//        long[] terms = MinTermUtils.expr2MinTerm(or, bVarIntegerMap);
//        Set<Long> minTerms = IntStream.range(0, terms.length).mapToObj(i -> terms[i]).collect(Collectors.toSet());
//
//        assertTrue(minTerms.size() == 4);
//        assertTrue(minTerms.contains(0L));
//        assertTrue(minTerms.contains(3L));
//        assertTrue(minTerms.contains(6L));
//        assertTrue(minTerms.contains(7L));
//
//        // a -> 低位，c -> 高位。a -> bit1，a' -> bit0
//        // ac + bc + a'b'c' = abc + ab'c + a'bc + a'b'c' = cba + cb'a + cba' + c'b'a' = m(7, 5, 6, 0)
//        and1 = new AndExpr(item2List(a, c));
//        and2 = new AndExpr(item2List(b, c));
//        or = new OrExpr(item2List(and1, and2, and3));
//        bVarIntegerMap = MinTermUtils.getBVarMap(or);
//        long[] terms1 = MinTermUtils.expr2MinTerm(or, bVarIntegerMap);
//        minTerms = IntStream.range(0, terms1.length).mapToObj(i -> terms1[i]).collect(Collectors.toSet());
//
//        assertTrue(minTerms.size() == 4);
//        assertTrue(minTerms.contains(0L));
//        assertTrue(minTerms.contains(6L));
//        assertTrue(minTerms.contains(5L));
//        assertTrue(minTerms.contains(7L));
    }

    static List<BExpr> item2List(BExpr... expr) {
        List<BExpr> list = new ArrayList<BExpr>(expr.length);
        for (BExpr ep : expr) {
            list.add(ep);
        }
        return list;
    }

    static class VarExpr extends BVar {

        private String var;

        public VarExpr(String var) {
            this.var = var;
        }

        public BExprType type() {
            return BExprType.VAR;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            VarExpr varExpr = (VarExpr) o;

            return var != null ? var.equals(varExpr.var) : varExpr.var == null;
        }

        @Override
        public int hashCode() {
            return var != null ? var.hashCode() : 0;
        }
    }

    static class OrExpr implements BNExpr {

        private List<? extends BExpr> exprList;

        public OrExpr(List<? extends BExpr> exprList) {
            this.exprList = exprList;
        }

        public List<? extends BExpr> getChildrenExpr() {
            return exprList;
        }

        public BExprType type() {
            return BExprType.OR;
        }
    }

    static class AndExpr implements BNExpr {

        private List<? extends BExpr> exprList;

        public AndExpr(List<? extends BExpr> exprList) {
            this.exprList = exprList;
        }

        public List<? extends BExpr> getChildrenExpr() {
            return exprList;
        }

        public BExprType type() {
            return BExprType.AND;
        }
    }

    static class NotExpr implements BUExpr {

        private BExpr expr;

        public NotExpr(BExpr expr) {
            this.expr = expr;
        }

        public BExpr getChild() {
            return expr;
        }

        public BExprType type() {
            return BExprType.NOT;
        }
    }
}
