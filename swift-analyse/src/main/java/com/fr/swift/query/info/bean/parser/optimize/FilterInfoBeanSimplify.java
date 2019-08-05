package com.fr.swift.query.info.bean.parser.optimize;

import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.AndFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.DetailFilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.EmptyFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.NumberInRangeFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.OrFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.value.RangeFilterValueBean;
import com.fr.swift.util.Strings;
import com.fr.swift.util.function.Function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Moira
 * @date 2019/7/30
 * @description 不同于布尔逻辑的optimize，simple方法用来合并多个过滤的数字范围
 * @since swift 1.0
 */
public class FilterInfoBeanSimplify {

    private static final String NEGATIVE_INFINITY = "-Infinity";
    private static final String POSITIVE_INFINITY = "Infinity";
    public static FilterInfoBean simple(FilterInfoBean bean) {

        switch (bean.getType()) {
            case AND:
                return simpleAnd(bean);
            case OR:
                return simpleOr(bean);
            default:
                return bean;
        }
    }

    private static FilterInfoBean simpleOr(FilterInfoBean bean) {
        List<FilterInfoBean> beans = transferFilterList(bean, new Function<List<RangeFilterValueBean>, List<RangeFilterValueBean>>() {
            @Override
            public List<RangeFilterValueBean> apply(List<RangeFilterValueBean> p) {
                return prepareUnionOr(p);
            }
        });
        if (beans.size() == 1) {
            return beans.get(0);
        } else {
            return new OrFilterBean(beans);
        }
    }

    private static FilterInfoBean simpleAnd(FilterInfoBean bean) {
        List<FilterInfoBean> beans = transferFilterList(bean, new Function<List<RangeFilterValueBean>, List<RangeFilterValueBean>>() {
            @Override
            public List<RangeFilterValueBean> apply(List<RangeFilterValueBean> p) {
                return prepareUnionAnd(p);
            }
        });
        if (beans.size() == 1) {
            return beans.get(0);
        } else if (beans.isEmpty()) {
            return new EmptyFilterBean();
        } else {
            return new AndFilterBean(beans);
        }
    }

    private static List<FilterInfoBean> transferFilterList(FilterInfoBean bean,
                                                           Function<List<RangeFilterValueBean>, List<RangeFilterValueBean>> fn) {
        List<FilterInfoBean> beans = new ArrayList<>((List<FilterInfoBean>) bean.getFilterValue());
        List<FilterInfoBean> result = new ArrayList<>();
        Map<String, List<DetailFilterInfoBean>> filters = groupByColumnName(beans);
        Set<String> keySet = filters.keySet();
        List<FilterInfoBean> resultBeans = new ArrayList<>();
        for (String key : keySet) {
            List<DetailFilterInfoBean> filterList = filters.get(key);
            List<RangeFilterValueBean> set = new ArrayList<>();
            for (DetailFilterInfoBean filter : filterList) {
                RangeFilterValueBean b = (RangeFilterValueBean) filter.getFilterValue();
                set.add(b);
            }
            resultBeans.addAll(listTransferToFilterBean(fn.apply(set), key));
        }
        result.addAll(resultBeans);
        result.addAll(beans);
        return result;
    }

    //把beans中rangefilter取出来根据column分组，并且从beans中删掉
    private static Map<String, List<DetailFilterInfoBean>> groupByColumnName(List<FilterInfoBean> beans) {
        List<DetailFilterInfoBean> numberInRangeFilterBeans = new ArrayList<>();
        Iterator<FilterInfoBean> iterator = beans.iterator();
        while (iterator.hasNext()) {
            FilterInfoBean singleBean = iterator.next();
            if (singleBean.getType().equals(SwiftDetailFilterType.NUMBER_IN_RANGE)) {
                numberInRangeFilterBeans.add((DetailFilterInfoBean) singleBean);
                iterator.remove();
            }
        }
        Map<String, List<DetailFilterInfoBean>> filters = new HashMap<>();
        List<DetailFilterInfoBean> list = new ArrayList<>();
        for (DetailFilterInfoBean a : numberInRangeFilterBeans) {
            if (!filters.containsKey(a.getColumn())) {
                list = new ArrayList<>();
                filters.put(a.getColumn(), list);
            }
            list.add(a);
        }
        return filters;
    }

    //例如 a and b,合并这两个filterBean
    private static List<RangeFilterValueBean> prepareUnionAnd(List<RangeFilterValueBean> set) {
        List<RangeFilterValueBean> result = new ArrayList<>();
        Map<RangeFilterValueBean, List<RangeFilterValueBean>> map = buildCombineMap(set, result);
        if (map.isEmpty()) {
            if (result.size() == 1) {
                return result;
            } else {
                return null;
            }
        } else {
            result.addAll(unionAnd(map));
            return prepareUnionAnd(result);
        }
    }

    //例如 a or b or c,合并abc三个filterBean
    private static List<RangeFilterValueBean> prepareUnionOr(List<RangeFilterValueBean> set) {
        List<RangeFilterValueBean> result = new ArrayList<>();
        Map<RangeFilterValueBean, List<RangeFilterValueBean>> map = buildCombineMap(set, result);
        if (map.isEmpty()) {
            return result;
        } else {
            result.addAll(unionOr(map));
            return prepareUnionOr(result);
        }
    }

    //把可以合并的bean放入map
    private static Map<RangeFilterValueBean, List<RangeFilterValueBean>> buildCombineMap(List<RangeFilterValueBean> set, List<RangeFilterValueBean> result) {
        Map<RangeFilterValueBean, List<RangeFilterValueBean>> map = new HashMap<>();
        for (int i = 0; i < set.size(); i++) {
            List<RangeFilterValueBean> list = new ArrayList<>();
            for (int j = i + 1; j < set.size(); j++) {
                if (isCombinable(set.get(i), set.get(j))) {
                    if (!map.containsKey(set.get(i))) {
                        list = new ArrayList<>();
                        map.put(set.get(i), list);
                    }
                    list.add(set.get(j));
                }
            }
            if (map.isEmpty()) {
                if (NEGATIVE_INFINITY.equals(set.get(i).getStart())) {
                    set.get(i).setStart(null);
                }
                if (POSITIVE_INFINITY.equals(set.get(i).getEnd())) {
                    set.get(i).setEnd(null);
                }
                result.add(set.get(i));
            }
        }
        return map;
    }

    private static List<RangeFilterValueBean> unionAnd(Map<RangeFilterValueBean, List<RangeFilterValueBean>> map) {
        List<RangeFilterValueBean> results = new ArrayList<>();
        for (RangeFilterValueBean product1 : map.keySet()) {
            RangeFilterValueBean product2 = buildProduct(map, results, product1);
            RangeFilterValueBean result = new RangeFilterValueBean();
            //如果区间一的终点大于区间二的终点 将区间二的终点赋予结果
            if (Double.parseDouble(product2.getEnd()) < Double.parseDouble(product1.getEnd())) {
                result.setEnd(product2.getEnd());
                result.setEndIncluded(product2.isEndIncluded());
            } else if (product2.getEnd().equals(product1.getEnd()) && product2.isEndIncluded() != product1.isEndIncluded()) {
                result.setEndIncluded(false);
            } else {
                result.setEnd(product1.getEnd());
                result.setEndIncluded(product1.isEndIncluded());
            }
            //如果区间二的起点大于区间一的起点 将区间二的起点赋予结果
            if (Double.parseDouble(product2.getStart()) > Double.parseDouble(product1.getStart())) {
                result.setStart(product2.getStart());
                result.setStartIncluded(product2.isStartIncluded());
            } else if (product2.getStart().equals(product1.getStart()) && product2.isStartIncluded() != product1.isStartIncluded()) {
                result.setStartIncluded(false);
            } else {
                result.setStart(product1.getStart());
                result.setStartIncluded(product1.isStartIncluded());
            }
            results.add(result);
        }
        return results;
    }

    private static RangeFilterValueBean buildProduct(Map<RangeFilterValueBean, List<RangeFilterValueBean>> map, List<RangeFilterValueBean> results, RangeFilterValueBean product1) {
        List<RangeFilterValueBean> values = map.get(product1);
        RangeFilterValueBean product2 = values.get(0);
        if (values.size() > 1) {
            List<RangeFilterValueBean> subValues = values.subList(1, values.size());
            results.addAll(subValues);
        }
        product1.setStart(startTransfer(product1.getStart()));
        product1.setEnd(endTransfer(product1.getEnd()));
        product2.setStart(startTransfer(product2.getStart()));
        product2.setEnd(endTransfer(product2.getEnd()));
        return product2;
    }

    private static List<RangeFilterValueBean> unionOr(Map<RangeFilterValueBean, List<RangeFilterValueBean>> map) {
        List<RangeFilterValueBean> results = new ArrayList<>();
        for (RangeFilterValueBean product1 : map.keySet()) {
            RangeFilterValueBean product2 = buildProduct(map, results, product1);
            RangeFilterValueBean result = new RangeFilterValueBean();
            //如果区间一的终点大于区间二的终点 将区间一的终点赋予结果
            if (Double.parseDouble(product2.getEnd()) < Double.parseDouble(product1.getEnd())) {
                result.setEnd(product1.getEnd());
                result.setEndIncluded(product1.isEndIncluded());
            } else if (product2.getEnd().equals(product1.getEnd()) && product2.isEndIncluded() != product1.isEndIncluded()) {
                result.setEndIncluded(true);
            } else {
                result.setEnd(product2.getEnd());
                result.setEndIncluded(product2.isEndIncluded());
            }
            //如果区间二的起点大于区间一的起点 将区间一的起点赋予结果
            if (Double.parseDouble(product2.getStart()) > Double.parseDouble(product1.getStart())) {
                result.setStart(product1.getStart());
                result.setStartIncluded(product1.isStartIncluded());
            } else if (product2.getStart().equals(product1.getStart()) && product2.isStartIncluded() != product1.isStartIncluded()) {
                result.setStartIncluded(true);
            } else {
                result.setStart(product2.getStart());
                result.setStartIncluded(product2.isStartIncluded());
            }
            results.add(result);
        }
        return results;
    }

    private static boolean isCombinable(RangeFilterValueBean a, RangeFilterValueBean b) {
        a.setStart(startTransfer(a.getStart()));
        b.setStart(startTransfer(b.getStart()));
        a.setEnd(endTransfer(a.getEnd()));
        b.setEnd(endTransfer(b.getEnd()));
        //是否有交集
        return Double.parseDouble(b.getEnd()) - Double.parseDouble(a.getStart()) > -1 && (Double.parseDouble(b.getStart()) - Double.parseDouble(a.getEnd())) < 1;
    }

    private static String startTransfer(String string) {
        return Strings.isEmpty(string) ? String.valueOf(Double.NEGATIVE_INFINITY) : string;
    }

    private static String endTransfer(String string) {
        return Strings.isEmpty(string) ? String.valueOf(Double.POSITIVE_INFINITY) : string;
    }

    private static List<FilterInfoBean> listTransferToFilterBean(List<RangeFilterValueBean> set, String column) {
        List<FilterInfoBean> beans = new ArrayList<>();
        if (set != null) {
            for (RangeFilterValueBean a : set) {
                beans.add(NumberInRangeFilterBean.builder(column).setStart(a.getStart(), a.isStartIncluded()).setEnd(a.getEnd(), a.isEndIncluded()).build());
            }
        }
        return beans;
    }
}
