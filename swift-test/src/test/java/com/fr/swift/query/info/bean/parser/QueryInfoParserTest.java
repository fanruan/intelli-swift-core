package com.fr.swift.query.info.bean.parser;

import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.info.bean.query.DetailQueryInfoBean;
import com.fr.swift.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.query.info.bean.query.QueryInfoBeanFactory;
import com.fr.swift.query.query.QueryType;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.resource.ResourceUtils;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;

/**
 * Created by Lyon on 2018/6/7.
 */
public class QueryInfoParserTest extends TestCase {

    public void testGroupQueryInfoBean() {
        String path = ResourceUtils.getFileAbsolutePath("json");
        String filePath = path + File.separator + "group.json";
        assertTrue(new File(filePath).exists());
        GroupQueryInfoBean queryBean = null;
        try {
            queryBean = (GroupQueryInfoBean) QueryInfoBeanFactory.create(new File(filePath).toURI().toURL());
        } catch (IOException e) {
            assertTrue(false);
        }
        assertTrue(queryBean.getQueryType() == QueryType.GROUP);
        assertTrue(queryBean.getMetricBeans().size() == 1);
        assertTrue(queryBean.getDimensionBeans().size() == 1);
        assertTrue(queryBean.getMetricBeans().get(0).getColumn().equals("value"));
        assertTrue(queryBean.getMetricBeans().get(0).getType() == AggregatorType.SUM);
        assertTrue(queryBean.getDimensionBeans().get(0).getColumn().equals("name"));
    }

    public void testDetailQueryInfoBean() {
        String path = ResourceUtils.getFileAbsolutePath("json");
        String filePath = path + File.separator + "detail.json";
        assertTrue(new File(filePath).exists());
        DetailQueryInfoBean queryBean = null;
        try {
            queryBean = (DetailQueryInfoBean) QueryInfoBeanFactory.create(new File(filePath).toURI().toURL());
        } catch (IOException e) {
            assertTrue(false);
        }
        assertTrue(queryBean.getQueryType() == QueryType.DETAIL);
        assertTrue(queryBean.getDimensionBeans().size() == 4);
        assertTrue(queryBean.getDimensionBeans().get(0).getColumn().equals("time"));
        assertTrue(queryBean.getSortBeans().size() == 1);
        assertTrue(queryBean.getSortBeans().get(0).getColumn().equals("memory"));
        assertTrue(queryBean.getSortBeans().get(0).getType() == SortType.DESC);
    }
}