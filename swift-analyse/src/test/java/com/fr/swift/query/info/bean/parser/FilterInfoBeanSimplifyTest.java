package com.fr.swift.query.info.bean.parser;

import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.AndFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.InFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.NumberInRangeFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.OrFilterBean;
import com.fr.swift.query.info.bean.parser.optimize.FilterInfoBeanSimplify;
import com.fr.swift.query.info.bean.query.DetailQueryInfoBean;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Moira
 * @date 2019/8/1
 * @description
 * @since swift 1.0
 */
public class FilterInfoBeanSimplifyTest {
    @Test
    public void test() {
        FilterInfoBean filter1 = NumberInRangeFilterBean.builder("plan_num").setStart("10", false).build();
        FilterInfoBean filter2 = NumberInRangeFilterBean.builder("plan_num").setStart("20", false).setEnd("50", true).build();
        FilterInfoBean filter3 = NumberInRangeFilterBean.builder("plan_num").setEnd("40", false).build();
        FilterInfoBean filter4 = new InFilterBean("plan_num", 30);
        FilterInfoBean filter5 = NumberInRangeFilterBean.builder("plan_num").setEnd("9", false).build();

        //a>10 OR 20<a<=50 OR a<40 = 全部
        List<FilterInfoBean> filters1 = new ArrayList<>();
        filters1.add(filter1);
        filters1.add(filter2);
        filters1.add(filter3);
        DetailQueryInfoBean queryInfoBean1 = new DetailQueryInfoBean.Builder("data").setFilter(new OrFilterBean(filters1)).build();
        FilterInfoBean filterInfo1 = FilterInfoBeanSimplify.simple(queryInfoBean1.getFilter());

        //a>10 AND 20<a<=50 AND a<40 = 20<a<40
        DetailQueryInfoBean queryInfoBean2 = new DetailQueryInfoBean.Builder("data").setFilter(new AndFilterBean(filters1)).build();
        FilterInfoBean filterInfo2 = FilterInfoBeanSimplify.simple(queryInfoBean2.getFilter());
        Assert.assertTrue(filterInfo2 instanceof NumberInRangeFilterBean);
        NumberInRangeFilterBean number2 = (NumberInRangeFilterBean) filterInfo2;
        Assert.assertEquals("20", number2.getFilterValue().getStart());
        Assert.assertEquals("40", number2.getFilterValue().getEnd());
        Assert.assertFalse(number2.getFilterValue().isStartIncluded());
        Assert.assertFalse(number2.getFilterValue().isEndIncluded());

        //a>10 AND (a IN 30) = a>10 AND (a IN 30)
        List<FilterInfoBean> filters2 = new ArrayList<>();
        filters2.add(filter1);
        filters2.add(filter4);
        DetailQueryInfoBean queryInfoBean3 = new DetailQueryInfoBean.Builder("data").setFilter(new AndFilterBean(filters2)).build();
        FilterInfoBean filterInfo3 = FilterInfoBeanSimplify.simple(queryInfoBean3.getFilter());
        Assert.assertTrue(filterInfo3 instanceof AndFilterBean);
        ArrayList<FilterInfoBean> b = (ArrayList<FilterInfoBean>) filterInfo3.getFilterValue();
        Assert.assertTrue(b.get(0) instanceof NumberInRangeFilterBean);
        Assert.assertTrue(b.get(1) instanceof InFilterBean);

        //a<9 AND a>10 = null
        List<FilterInfoBean> filters3 = new ArrayList<>();
        filters3.add(filter1);
        filters3.add(filter5);
        DetailQueryInfoBean queryInfoBean4 = new DetailQueryInfoBean.Builder("data").setFilter(new AndFilterBean(filters3)).build();
        FilterInfoBean filterInfo4 = FilterInfoBeanSimplify.simple(queryInfoBean4.getFilter());
        Assert.assertEquals(null, filterInfo4);

    }
}
