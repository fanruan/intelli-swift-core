package com.fr.swift.util.qm.cal;

import com.fr.swift.util.qm.bool.BExpr;
import com.fr.swift.util.qm.bool.BExprType;
import com.fr.swift.util.qm.bool.BNExpr;
import com.fr.swift.util.qm.bool.BUExpr;
import com.fr.swift.util.qm.bool.BVar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Lyon on 2018/7/6.
 */
class MinTermUtils {

    static long[] expr2MinTerm(BExpr expr, Map<BVar, Integer> bVarIntegerMap) {
        List<Product> sop = expr2InternalSOP(expr, bVarIntegerMap);
        return sop2MinTerms(sop, bVarIntegerMap.size());
    }

    private static long[] sop2MinTerms(List<Product> products, int bVarSize) {
        List<Product> minTermProducts = new ArrayList<Product>();
        for (Product product : products) {
            minTermProducts.addAll(product2MinTerms(product, bVarSize));
        }
        Set<Long> terms = new HashSet<Long>();
        for (Product product : minTermProducts) {
            terms.add(product.toMinTerm());
        }
        long[] minTerms = new long[terms.size()];
        int i = 0;
        for (Long term : terms) {
            minTerms[i++] = term;
        }
        return minTerms;
    }

    private static List<Product> product2MinTerms(Product product, int bVarSize) {
        List<Product> products = new ArrayList<Product>();
        products.add(product);
        for (int i = 0; i < bVarSize; i++) {
            if (product.contains(i)) {
                continue;
            }
            List<Product> productList = new ArrayList<Product>();
            for (Product p : products) {
                productList.add(p.andAndGet(createBVarProduct(bVarSize, i, 0)));
                productList.add(p.andAndGet(createBVarProduct(bVarSize, i, 1)));
            }
            products = productList;
        }
        return products;
    }

    private static List<Product> expr2InternalSOP(BExpr expr, Map<BVar, Integer> bVarIntegerMap) {
        List<Product> sop = new ArrayList<Product>();
        BExprType type = expr.type();
        switch (type) {
            case VAR:
                sop.add(createBVarProduct(bVarIntegerMap.size(), bVarIntegerMap.get(expr), 1));
                break;
            case NOT: {
                BExpr child = ((BUExpr) expr).getChild();
                if (child.type() == BExprType.VAR) {
                    sop.add(createBVarProduct(bVarIntegerMap.size(), bVarIntegerMap.get(child), 0));
                    break;
                }
                List<Product> subSop = expr2InternalSOP(child, bVarIntegerMap);
                if (subSop.isEmpty()) {
                    break;
                }
                // 这边应该应用一下德摩根定律，(a + b)' = a'b'
                List<Product> notList = new ArrayList<Product>();
                for (Product p : subSop) {
                    notList.add(p.notAndGet());
                }
                Product product = notList.get(0);
                for (int i = 1; i < notList.size(); i++) {
                    product = product.andAndGet(notList.get(i));
                    if (product == null) {
                        break;
                    }
                }
                if (product != null) {
                    sop.add(product);
                }
                break;
            }
            case OR: {
                for (BExpr ep : ((BNExpr) expr).getChildrenExpr()) {
                    sop.addAll(expr2InternalSOP(ep, bVarIntegerMap));
                }
                break;
            }
            case AND: {
                List<Product> lastSop = new ArrayList<Product>();
                for (BExpr ep : ((BNExpr) expr).getChildrenExpr()) {
                    List<Product> current = expr2InternalSOP(ep, bVarIntegerMap);
                    if (lastSop.isEmpty()) {
                        lastSop = current;
                        continue;
                    }
                    lastSop = distributeProduct(lastSop, current);
                }
                sop.addAll(lastSop);
            }
        }
        return sop;
    }

    private static List<Product> distributeProduct(List<Product> sopA, List<Product> sopB) {
        List<Product> sop = new ArrayList<Product>();
        for (Product a : sopA) {
            for (Product b : sopB) {
                Product product = a.andAndGet(b);
                if (product != null) {
                    sop.add(product);
                }
            }
        }
        return sop;
    }

    private static Product createBVarProduct(int size, int term, int value) {
        Product product = new Product(size);
        product.set(term, value);
        return product;
    }

    static Map<BVar, Integer> getBVarMap(BExpr expr) {
        Set<BVar> bVars = countVars(expr);
        Map<BVar, Integer> bVarIntegerMap = new HashMap<BVar, Integer>();
        for (BVar bVar : bVars) {
            bVarIntegerMap.put(bVar, bVarIntegerMap.size());
        }
        return bVarIntegerMap;
    }

    private static Set<BVar> countVars(BExpr expr) {
        Set<BVar> bVarSet = new HashSet<BVar>();
        BExprType type = expr.type();
        switch (type) {
            case VAR:
                bVarSet.add((BVar) expr);
                break;
            case NOT:
                bVarSet.addAll(countVars(((BUExpr) expr).getChild()));
                break;
            case AND:
            case OR:
                for (BExpr ep : ((BNExpr) expr).getChildrenExpr()) {
                    bVarSet.addAll(countVars(ep));
                }
        }
        return bVarSet;
    }

    private static class Product {

        private static final int SIZE = 63;

        private int size;
        // 每一位对应一个布尔变量，数组偏移0~SIZE-1对应布尔变量的索引
        private int[] terms;

        Product(int size) {
            assert size <= SIZE;
            this.size = size;
            terms = new int[size];
            Arrays.fill(terms, -1);
        }

        long toMinTerm() {
            // long的高位对应布尔变量索引较大值
            long minTerm = 0L;
            for (int i = 0; i < terms.length; i++) {
                if (terms[i] == 1) {
                    minTerm |= 1L << i;
                }
            }
            return minTerm;
        }

        boolean contains(int term) {
            return terms[term] != -1;
        }

        Product andAndGet(Product product) {
            Product result = new Product(size);
            result.terms = Arrays.copyOf(product.terms, product.terms.length);
            for (int i = 0; i < terms.length; i++) {
                if (terms[i] != -1 && !result.set(i, terms[i])) {
                    // 为0返回null
                    return null;
                }
            }
            return result;
        }

        Product notAndGet() {
            Product product = new Product(size);
            for (int i = 0; i < terms.length; i++) {
                if (terms[i] != -1) {
                    product.terms[i] = terms[i] == 1 ? 0 : 1;
                }
            }
            return product;
        }

        boolean set(int term, int value) {
            if (terms[term] == 1 && value == 0) {
                return false;
            }
            if (terms[term] == 0 && value == 1) {
                return false;
            }
            terms[term] = value;
            return true;
        }
    }
}
