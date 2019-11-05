package com.fr.swift.util.qm.cal;

import com.fr.swift.util.qm.bool.BExpr;
import com.fr.swift.util.qm.bool.BExprConverter;
import com.fr.swift.util.qm.bool.BVar;
import com.fr.swift.util.qm.cal.exception.UnsupportedBooleanVariableNumberException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


/**
 * Quine–McCluskey algorithm实现
 * <p>
 * Created by Lyon on 2018/7/6.
 */
// TODO: 2018/7/11 分析时间和空间复杂度
public class QMUtils {

    /**
     * 化简布尔表达式对象
     * 由于实现方式限制至多只能处理63个布尔变量的表达式
     * 能支持don't care项，有需求再加接口
     *
     * @param expr      布尔表达式
     * @param converter 表达式对象转换器
     * @return
     */
    public static BExpr simplify(BExpr expr, BExprConverter converter) throws Exception {
        Map<BVar, Integer> bVarIntegerMap = MinTermUtils.getBVarMap(expr);
        if (bVarIntegerMap.isEmpty()) {
            return BExpr.TRUE;
        }
        if (bVarIntegerMap.size() > 63) {
            throw new UnsupportedBooleanVariableNumberException("number of boolean variable exceed the limit: 63!");
        }
        long[] minTerms = MinTermUtils.expr2MinTerm(expr, bVarIntegerMap);
        List<Row> rows = simplifyMinTerms(minTerms, new long[0]);
        if (rows.isEmpty()) {
            return BExpr.FALSE;
        }
        if (isTrue(rows, bVarIntegerMap.size())) {
            return BExpr.TRUE;
        }
        return decodeSimplifiedTerms(rows, bVarIntegerMap, converter);
    }

    private static boolean isTrue(List<Row> rows, int size) {
        if (rows.size() != 1) {
            return false;
        }
        long removeBits = rows.get(0).getRemovedBits();
        return removeBits == ((1L << size) - 1);    // n个bit全是1
    }

    // 从化简结果行中取出化简项
    private static BExpr decodeSimplifiedTerms(List<Row> rows, Map<BVar, Integer> bVarIntegerMap, BExprConverter converter) {
        assert !rows.isEmpty();
        Map<Integer, BVar> integerBVarMap = reverseMap(bVarIntegerMap);
        List<BExpr> exprList = new ArrayList<BExpr>();
        for (Row row : rows) {
            exprList.add(decodeTermFromRow(row, integerBVarMap, converter));
        }
        return exprList.size() == 1 ? exprList.get(0) : converter.convertOrExpr(exprList);
    }

    private static BExpr decodeTermFromRow(Row row, Map<Integer, BVar> integerBVarMap, BExprConverter converter) {
        long minTerm = row.getMinTerm();
        long removeBits = row.getRemovedBits();
        List<BExpr> vars = new ArrayList<BExpr>();
        int size = integerBVarMap.size();
        for (int i = 0; i < size; i++) {
            long bit = 1L << i;
            if ((bit & removeBits) > 0) {
                // 说明该位对应的BVar是被消除项
                continue;
            }
            if ((bit & minTerm) == 0) {
                // 要对改变量取反
                vars.add(converter.convertNotExpr(integerBVarMap.get(i)));
            } else {
                vars.add(integerBVarMap.get(i));
            }
        }
        // size有没有可能是0呢？有没有可能removeBits为 ~0L？有，已经在前面isTrue判断过了
        assert !vars.isEmpty();
        return vars.size() == 1 ? vars.get(0) : converter.convertAndExpr(vars);
    }

    private static Map<Integer, BVar> reverseMap(Map<BVar, Integer> bVarIntegerMap) {
        Map<Integer, BVar> integerBVarMap = new HashMap<Integer, BVar>();
        for (Map.Entry<BVar, Integer> entry : bVarIntegerMap.entrySet()) {
            integerBVarMap.put(entry.getValue(), entry.getKey());
        }
        return integerBVarMap;
    }

    private static List<Row> simplifyMinTerms(long[] minTerms, long[] doNotCares) {
        Set<Row> rows = combineMinTerms(minTerms);
        return reducePIChart(rows, doNotCares);
    }

    private static List<Row> reducePIChart(Set<Row> rows, long[] doNotCares) {
        Map<Long, List<Row>> minTerm2RowListMap = countRowsByMinTerms(rows, doNotCares);
        List<Row> result = new ArrayList<Row>();
        // 找出实质蕴含项(essential prime implicant)
        Set<Row> epis = getEPIList(minTerm2RowListMap);
        result.addAll(epis);
        // 删除minTerm2RowListMap中epis已经覆盖的minTerm分组
        removeGroupCoveredByEPIs(minTerm2RowListMap, epis);
        // 此时minTerm2RowListMap中仅包含剩余PI，通过Petrick's method取出剩余PI中的最小覆盖
        List<Row> minCoverFromRemain = PMUtils.reduce(minTerm2RowListMap);
        result.addAll(minCoverFromRemain);
        return result;
    }

    private static void removeGroupCoveredByEPIs(Map<Long, List<Row>> minTerm2RowListMap, Set<Row> epis) {
        for (Row row : epis) {
            long[] coveredMinTerms = row.getSimplifiedTerms();
            for (long minTerm : coveredMinTerms) {
                minTerm2RowListMap.remove(minTerm);
            }
        }
    }

    private static Set<Row> getEPIList(Map<Long, List<Row>> minTerm2RowListMap) {
        Set<Row> rows = new HashSet<Row>();
        for (Map.Entry<Long, List<Row>> entry : minTerm2RowListMap.entrySet()) {
            if (entry.getValue().size() == 1) {
                rows.add(entry.getValue().get(0));
            }
        }
        return rows;
    }

    private static Map<Long, List<Row>> countRowsByMinTerms(Set<Row> rows, long[] dotNotCares) {
        Map<Long, List<Row>> minTerm2RowListMap = new HashMap<Long, List<Row>>();
        for (Row row : rows) {
            long[] simplifiedTerms = row.getSimplifiedTerms();
            for (long minTerm : simplifiedTerms) {
                if (isDoNotCare(minTerm, dotNotCares)) {
                    continue;
                }
                if (!minTerm2RowListMap.containsKey(minTerm)) {
                    minTerm2RowListMap.put(minTerm, new ArrayList<Row>());
                }
                minTerm2RowListMap.get(minTerm).add(row);
            }
        }
        return minTerm2RowListMap;
    }

    private static boolean isDoNotCare(long minTerm, long[] doNotCares) {
        for (long doNotCare : doNotCares) {
            if (minTerm == doNotCare) {
                return true;
            }
        }
        return false;
    }

    private static Set<Row> combineMinTerms(long[] minTerms) {
        QMTable table = initTable(minTerms);
        QMTable next = simplifyTable(table);
        // 质蕴含项(prime implicant)集合
        Set<Row> pis = new HashSet<Row>(getPI(table));
        QMTable prev = null;    // 释放不再使用的临时表
        while (next != null) {
            if (prev != null) {
                pis.addAll(getPI(prev));
            }
            prev = next;
            next = simplifyTable(next);
        }
        if (prev != null) {
            pis.addAll(getPI(prev));
        }
        return pis;
    }

    private static QMTable initTable(long[] minTerms) {
        Map<Integer, Set<Row>> groupMap = new TreeMap<Integer, Set<Row>>();
        for (long minTerm : minTerms) {
            int numberOf1s = BitUtils.countOf1InBinary(minTerm);
            if (!groupMap.containsKey(numberOf1s)) {
                groupMap.put(numberOf1s, new HashSet<Row>());
            }
            groupMap.get(numberOf1s).add(new Row(minTerm, 0, numberOf1s, new long[]{minTerm}));
        }
        return new QMTable(groupMap);
    }

    private static Set<Row> getPI(QMTable table) {
        Set<Row> rows = new HashSet<Row>();
        for (Integer groupValue : table.getGroupValues()) {
            for (Row row : table.getRowList(groupValue)) {
                if (!row.isPairedOf()) {
                    rows.add(row);
                }
            }
        }
        return rows;
    }

    private static QMTable simplifyTable(QMTable table) {
        List<Integer> groupValues = table.getGroupValues();
        if (groupValues.size() <= 1) {
            return null;
        }
        Map<Integer, Set<Row>> groupMap = new TreeMap<Integer, Set<Row>>();
        for (int groupIndex = 0; groupIndex < groupValues.size() - 1; groupIndex++) {
            Set<Row> currentRows = table.getRowList(groupValues.get(groupIndex));
            Set<Row> nextRows = table.getRowList(groupValues.get(groupIndex + 1));
            Set<Row> combinedRows = new HashSet<Row>();
            Set<RowKey> distinctKeys = new HashSet<RowKey>();
            for (Row current : currentRows) {
                for (Row next : nextRows) {
                    if (canBeCombined(current, next)) {
                        current.setPairedOf(true);
                        next.setPairedOf(true);
                        long removeBits = getRemoveBit(current.getRemovedBits(), current.getMinTerm(), next.getMinTerm());
                        RowKey key = new RowKey(current.getMinTerm(), removeBits);
                        if (distinctKeys.contains(key)) {
                            continue;
                        }
                        distinctKeys.add(key);
                        // n个1的行和n+1个1的行进行合并，结果还是n个1
                        combinedRows.add(new Row(current.getMinTerm(), removeBits, current.getNumberOf1s(),
                                combineSimplifiedTerms(current.getSimplifiedTerms(), next.getSimplifiedTerms())));
                    }
                }
            }
            if (!combinedRows.isEmpty()) {
                groupMap.put(groupValues.get(groupIndex), combinedRows);
            }
        }
        return new QMTable(groupMap);
    }

    private static long getRemoveBit(long currentRemoveBits, long currentMinTerm, long nextMinTerm) {
        long term1 = currentMinTerm | currentRemoveBits;
        long term2 = nextMinTerm | currentRemoveBits;
        return currentRemoveBits | (term1 ^ term2);
    }

    private static long[] combineSimplifiedTerms(long[] terms1, long[] terms2) {
        long[] result = Arrays.copyOf(terms1, terms1.length * 2);
        System.arraycopy(terms2, 0, result, terms1.length, terms2.length);
        return result;
    }

    private static boolean canBeCombined(Row row1, Row row2) {
        long remove1 = row1.getRemovedBits();
        long remove2 = row2.getRemovedBits();
        if (remove1 != remove2) {
            return false;
        }
        // 不考虑removeBits的情况下，判断term1和term2只有一个bit不同
        long term1 = row1.getMinTerm() | remove1;
        long term2 = row2.getMinTerm() | remove1;
        return BitUtils.countOf1InBinary(term1 ^ term2) == 1;
    }

    // 用于去重合并过程中的化简行
    private static class RowKey {
        private long maskedTerm;
        private long removeBits;

        public RowKey(long minTerm, long removeBits) {
            this.maskedTerm = minTerm | removeBits;
            this.removeBits = removeBits;
        }

        @Override
        public boolean equals(Object o) {
            RowKey key = (RowKey) o;
            if (removeBits != key.removeBits) {
                return false;
            }
            return maskedTerm == key.maskedTerm;
        }

        @Override
        public int hashCode() {
            int result = (int) (maskedTerm ^ (maskedTerm >>> 32));
            result = 31 * result + (int) (removeBits ^ (removeBits >>> 32));
            return result;
        }
    }
}
