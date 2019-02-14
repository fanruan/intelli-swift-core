package com.fr.swift.query.info.bean.parser;

import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.AndFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.NFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.NotFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.OrFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.StringOneValueFilterBean;
import com.fr.swift.query.info.bean.parser.optimize.FilterInfoBeanOptimizer;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Lyon on 2018/7/11.
 */
public class FilterInfoBeanOptimizerTest extends TestCase {

    public void test() {
        StringOneValueFilterBean a = new StringOneValueFilterBean();
        a.setType(SwiftDetailFilterType.STRING_LIKE);
        a.setColumn("name");
        a.setFilterValue("L");
        FilterInfoBean optimized = FilterInfoBeanOptimizer.optimize(a);
        assertTrue(optimized == a);

        NFilterBean b = new NFilterBean();
        b.setType(SwiftDetailFilterType.TOP_N);
        b.setColumn("value");
        b.setFilterValue(40);

        List<FilterInfoBean> beanList = new ArrayList<FilterInfoBean>();
        beanList.add(a);
        beanList.add(b);
        FilterInfoBean or = new OrFilterBean();
        or.setFilterValue(beanList);
        optimized = FilterInfoBeanOptimizer.optimize(or);
        assertTrue(optimized.getType() == SwiftDetailFilterType.OR);

        NotFilterBean an = new NotFilterBean();
        an.setFilterValue(a);

        NotFilterBean bn = new NotFilterBean();
        bn.setFilterValue(b);

        AndFilterBean ab = new AndFilterBean();
        ab.setFilterValue(Arrays.<FilterInfoBean>asList(a, b));
        AndFilterBean abn = new AndFilterBean();
        abn.setFilterValue(Arrays.<FilterInfoBean>asList(a, bn));

        // ab + ab' = a
        or = new OrFilterBean();
        or.setFilterValue(Arrays.asList(ab, abn));
        optimized = FilterInfoBeanOptimizer.optimize(or);
        assertTrue(optimized == a);
    }
}
