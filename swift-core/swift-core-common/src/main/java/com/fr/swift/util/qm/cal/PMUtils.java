package com.fr.swift.util.qm.cal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Petrick's method 实现
 * <p>
 * Created by Lyon on 2018/7/9.
 */
class PMUtils {

    /**
     * @param minTerm2RowListMap key为最小项，value为该最小项相关的化简行
     */
    static List<Row> reduce(Map<Long, List<Row>> minTerm2RowListMap) {
        Map<Row, Long> row2Bit = new HashMap<Row, Long>();
        for (List<Row> rows : minTerm2RowListMap.values()) {
            for (Row row : rows) {
                if (row2Bit.containsKey(row)) {
                    continue;
                }
                row2Bit.put(row, 1L << row2Bit.size());
                // 因为变量小于64，目测最后的剩余项也应该小于64，有待分析一下
                if (row2Bit.size() > 63) {
                    throw new RuntimeException("permitted size of remaining prime implicants exceeded!");
                }
            }
        }
        // rowBit最小项对应的和之积(POS)
        List<List<Long>> pos = getPOS(minTerm2RowListMap, row2Bit);
        Set<Long> sop = pos2sop(pos);
        long productWithLeastTerms = getProductWithLeastTerms(sop);
        return getRowsSpecifiedByProduct(productWithLeastTerms, row2Bit);
    }

    private static List<Row> getRowsSpecifiedByProduct(long product, Map<Row, Long> row2Bit) {
        List<Row> rows = new ArrayList<Row>();
        int i = 0;
        for (Map.Entry<Row, Long> entry : row2Bit.entrySet()) {
            // product是否包含当前entry对应的row
            if ((1L << i & product) != 0) {
                rows.add(entry.getKey());
            }
            i++;
        }
        return rows;
    }

    private static long getProductWithLeastTerms(Set<Long> sop) {
        int numberOf1s = 64;
        long product = 0;
        for (long p : sop) {
            int count = BitUtils.countOf1InBinary(p);
            if (count < numberOf1s) {
                numberOf1s = count;
                product = p;
            }
        }
        return product;
    }

    private static Set<Long> pos2sop(List<List<Long>> pos) {
        if (pos.isEmpty()) {
            return new HashSet<Long>(0);
        }
        Set<Long> result = new HashSet<Long>(pos.get(0));
        for (int i = 1; i < pos.size(); i++) {
            List<Long> sum = pos.get(i);
            Set<Long> newResult = new HashSet<Long>();
            // 分配律
            for (long rightTerm : sum) {
                for (long leftTerm : result) {
                    newResult.add(rightTerm | leftTerm);
                }
            }
            result = newResult;
        }
        return result;
    }

    private static List<List<Long>> getPOS(Map<Long, List<Row>> minTerm2RowListMap, Map<Row, Long> row2Bit) {
        List<List<Long>> pos = new ArrayList<List<Long>>();
        for (List<Row> rows : minTerm2RowListMap.values()) {
            List<Long> sum = new ArrayList<Long>();
            for (Row row : rows) {
                Long bit = row2Bit.get(row);
                if (bit != null) {
                    sum.add(bit);
                }
            }
            pos.add(sum);
        }
        return pos;
    }
}
