package com.fr.swift.executor.task.impl;

import com.fr.swift.db.Where;
import com.fr.swift.db.impl.SwiftWhere;
import com.fr.swift.executor.task.ExecutorTask;
import com.fr.swift.executor.type.DBStatusType;
import com.fr.swift.executor.type.LockType;
import com.fr.swift.executor.type.SwiftTaskType;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.AndFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.StringOneValueFilterBean;
import com.fr.swift.source.SourceKey;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2019/3/7
 *
 * @author Lucifer
 * @description
 */
public class DeleteExecutorTaskTest {

    String json = "{\"filterValue\":[{\"filterValue\":\"100\",\"column\":\"ID\",\"type\":\"STRING_LIKE\"},{\"filterValue\":\"l\",\"column\":\"NAME\",\"type\":\"STRING_STARTS_WITH\"}" +
            ",{\"filterValue\":\"FM\",\"column\":\"TYPE\",\"type\":\"STRING_ENDS_WITH\"}],\"type\":\"AND\"}";

    final StringOneValueFilterBean bean1 = new StringOneValueFilterBean("ID", SwiftDetailFilterType.STRING_LIKE, "100");
    final StringOneValueFilterBean bean2 = new StringOneValueFilterBean("NAME", SwiftDetailFilterType.STRING_STARTS_WITH, "l");
    final StringOneValueFilterBean bean3 = new StringOneValueFilterBean("TYPE", SwiftDetailFilterType.STRING_ENDS_WITH, "FM");

    @Test
    public void testSerialize() throws Exception {
        List<FilterInfoBean> filterInfoBeanList = new ArrayList<FilterInfoBean>() {{
            add(bean1);
            add(bean2);
            add(bean3);
        }};
        AndFilterBean andFilterBean = new AndFilterBean(filterInfoBeanList);
        Where where = new SwiftWhere(andFilterBean);
        DeleteExecutorTask deleteExecutorTask = new DeleteExecutorTask(new SourceKey("test"), where);
        Assert.assertEquals(json, deleteExecutorTask.getTaskContent());
    }

    @Test
    public void testDeserialize() throws Exception {
        ExecutorTask executorTask = new DeleteExecutorTask(new SourceKey("test"), true, SwiftTaskType.DELETE, LockType.TABLE,
                "test", DBStatusType.ACTIVE, String.valueOf(System.nanoTime()), System.nanoTime(), json);
        List<FilterInfoBean> filterInfoBeanList = ((AndFilterBean) executorTask.getJob().serializedTag()).getFilterValue();
        Assert.assertEquals(filterInfoBeanList.get(0).getFilterValue(), bean1.getFilterValue());
        Assert.assertEquals(filterInfoBeanList.get(0).getType(), bean1.getType());
        Assert.assertEquals(((StringOneValueFilterBean) filterInfoBeanList.get(0)).getColumn(), bean1.getColumn());

        Assert.assertEquals(filterInfoBeanList.get(1).getFilterValue(), bean2.getFilterValue());
        Assert.assertEquals(filterInfoBeanList.get(1).getType(), bean2.getType());
        Assert.assertEquals(((StringOneValueFilterBean) filterInfoBeanList.get(1)).getColumn(), bean2.getColumn());

        Assert.assertEquals(filterInfoBeanList.get(2).getFilterValue(), bean3.getFilterValue());
        Assert.assertEquals(filterInfoBeanList.get(2).getType(), bean3.getType());
        Assert.assertEquals(((StringOneValueFilterBean) filterInfoBeanList.get(2)).getColumn(), bean3.getColumn());
    }
}
