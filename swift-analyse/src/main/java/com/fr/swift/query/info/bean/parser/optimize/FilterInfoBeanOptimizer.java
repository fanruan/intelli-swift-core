package com.fr.swift.query.info.bean.parser.optimize;

import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.AllShowFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.AndFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.EmptyFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.NotFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.OrFilterBean;
import com.fr.swift.util.qm.bool.BExpr;
import com.fr.swift.util.qm.bool.BExprConverter;
import com.fr.swift.util.qm.bool.BVar;
import com.fr.swift.util.qm.cal.QMUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2018/7/11.
 */
public class FilterInfoBeanOptimizer {

    // TODO: 2018/7/26 平台日志查询过滤多的场景发现性能问题。。。
    // TODO: 2018/7/26 给客户端的建议：过滤变量最小原则，例如能用in条件就不要用多个eq加and过滤
    // TODO: 2018/7/26 这边也可以根据count变量的时候看一下重复变量多少来决定是否进行优化
    public static FilterInfoBean optimize(FilterInfoBean bean) {
        try {
            BExpr optimized = QMUtils.simplify(bean, new FilterInfoBeanConverter());
            if (optimized == BExpr.FALSE) {
                return new EmptyFilterBean();
            }
            if (optimized == BExpr.TRUE) {
                return new AllShowFilterBean();
            }
            return (FilterInfoBean) optimized;
        } catch (Exception e) {

        }
        return bean;
    }

    private static class FilterInfoBeanConverter implements BExprConverter {

        @Override
        public BExpr convertAndExpr(List<? extends BExpr> items) {
            AndFilterBean bean = new AndFilterBean();
            List<FilterInfoBean> filterInfoBeans = new ArrayList<FilterInfoBean>();
            for (BExpr expr : items) {
                filterInfoBeans.add((FilterInfoBean) expr);
            }
            bean.setFilterValue(filterInfoBeans);
            return bean;
        }

        @Override
        public BExpr convertOrExpr(List<? extends BExpr> items) {
            OrFilterBean bean = new OrFilterBean();
            List<FilterInfoBean> filterInfoBeans = new ArrayList<FilterInfoBean>();
            for (BExpr expr : items) {
                filterInfoBeans.add((FilterInfoBean) expr);
            }
            bean.setFilterValue(filterInfoBeans);
            return bean;
        }

        @Override
        public BExpr convertNotExpr(BVar var) {
            NotFilterBean bean = new NotFilterBean();
            bean.setFilterValue((FilterInfoBean) var);
            return bean;
        }
    }
}
