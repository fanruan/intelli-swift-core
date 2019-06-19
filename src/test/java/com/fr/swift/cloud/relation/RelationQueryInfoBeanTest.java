package com.fr.swift.cloud.relation;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.query.QueryRunnerProvider;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.query.info.bean.query.SingleInfoBean;
import com.fr.swift.query.session.factory.SessionFactory;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.Row;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Create by lifan on 2019-06-14 17:24
 */


@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({QueryRunnerProvider.class, SwiftContext.class, QueryBeanFactory.class})
public class RelationQueryInfoBeanTest {
    @Mock
    SingleInfoBean queryBean1;

    @Mock
    SingleInfoBean queryBean2;

    @Mock
    SingleInfoBean queryBean3;

    @Mock
    BeanFactory beanFactory;

    @Mock
    SessionFactory sessionFactory;

    QueryRunnerProvider queryRunnerProvider;

    @Before
    public void setUp() throws Exception {
        //mock QueryRunnerProvider
        PowerMockito.mockStatic(SwiftContext.class);
        Mockito.when(SwiftContext.get()).thenReturn(beanFactory);
        Mockito.when(beanFactory.getBean("swiftQuerySessionFactory", SessionFactory.class)).thenReturn(sessionFactory);

        queryRunnerProvider = Mockito.mock(QueryRunnerProvider.class);
        PowerMockito.mockStatic(QueryRunnerProvider.class);
        Mockito.when(QueryRunnerProvider.getInstance()).thenReturn(queryRunnerProvider);

        PowerMockito.mockStatic(QueryBeanFactory.class);
        PowerMockito.when(QueryBeanFactory.queryBean2String(queryBean1)).thenReturn("queryBean1");
        PowerMockito.when(QueryBeanFactory.queryBean2String(queryBean2)).thenReturn("queryBean2");
        PowerMockito.when(QueryBeanFactory.queryBean2String(queryBean3)).thenReturn("queryBean3");
    }

    @Test
    public void test1to1() throws Exception {

        String relationColomnBean1 = "id";
        String relationColomnBean2 = "id";

        RelationQueryInfoBean bean = new RelationQueryInfoBean();
        String jsonString1 = QueryBeanFactory.queryBean2String(queryBean1);
        String jsonString2 = QueryBeanFactory.queryBean2String(queryBean2);

        //测试 一对一 没有溢出
        SwiftResultSet rs1 = InitResultSetUtils.makeResultSetIdAndName1();
        SwiftResultSet rs2 = InitResultSetUtils.makeResultSetIdAndAge1();
        Mockito.when(queryRunnerProvider.query(jsonString1)).thenReturn(rs1);
        Mockito.when(queryRunnerProvider.query(jsonString2)).thenReturn(rs2);

        SwiftResultSet newResult1 = bean.relationTable(relationColomnBean1, relationColomnBean2, queryBean1, queryBean2);
        newResult1.hasNext();
        Assert.assertEquals("[id, name, id, age]",newResult1.getMetaData().getFieldNames().toString());
        Assert.assertEquals("[0, name0, 0, age0]",newResult1.getNextRow().getValues().toString());
        Assert.assertEquals("[1, name1, 1, age1]",newResult1.getNextRow().getValues().toString());
        Assert.assertEquals("[2, name2, 2, age2]",newResult1.getNextRow().getValues().toString());
        Assert.assertEquals("[3, name3, 3, age3]",newResult1.getNextRow().getValues().toString());
        Assert.assertEquals("[4, name4, 4, age4]",newResult1.getNextRow().getValues().toString());

        //测试 一对一 左边溢出
        SwiftResultSet rs3 = InitResultSetUtils.makeResultSetIdAndName2();
        SwiftResultSet rs4 = InitResultSetUtils.makeResultSetIdAndAge1();
        Mockito.when(queryRunnerProvider.query(jsonString1)).thenReturn(rs3);
        Mockito.when(queryRunnerProvider.query(jsonString2)).thenReturn(rs4);

        SwiftResultSet newResult2 = bean.relationTable(relationColomnBean1, relationColomnBean2, queryBean1, queryBean2);
        newResult2.hasNext();
        Assert.assertEquals("[0, name0, 0, age0]",newResult2.getNextRow().getValues().toString());
        Assert.assertEquals("[1, name1, 1, age1]",newResult2.getNextRow().getValues().toString());
        Assert.assertEquals("[2, name2, 2, age2]",newResult2.getNextRow().getValues().toString());
        Assert.assertEquals("[3, name3, 3, age3]",newResult2.getNextRow().getValues().toString());
        Assert.assertEquals("[4, name4, 4, age4]",newResult2.getNextRow().getValues().toString());

        //测试 一对多 右边溢出
        SwiftResultSet rs5 = InitResultSetUtils.makeResultSetIdAndName1();
        SwiftResultSet rs6 = InitResultSetUtils.makeResultSetIdAndAge11();
        Mockito.when(queryRunnerProvider.query(jsonString1)).thenReturn(rs5);
        Mockito.when(queryRunnerProvider.query(jsonString2)).thenReturn(rs6);

        SwiftResultSet newResult3 = bean.relationTable(relationColomnBean1, relationColomnBean2, queryBean1, queryBean2);
        newResult3.hasNext();
        Assert.assertEquals("[0, name0, 0, age0]",newResult3.getNextRow().getValues().toString());
        Assert.assertEquals("[1, name1, 1, age1]",newResult3.getNextRow().getValues().toString());
        Assert.assertEquals("[2, name2, 2, age2]",newResult3.getNextRow().getValues().toString());
        Assert.assertEquals("[3, name3, 3, age3]",newResult3.getNextRow().getValues().toString());
        Assert.assertEquals("[4, name4, 4, age4]",newResult3.getNextRow().getValues().toString());

        //测试两边同时溢出
        SwiftResultSet rs7 = InitResultSetUtils.makeResultSetIdAndName2();
        SwiftResultSet rs8 = InitResultSetUtils.makeResultSetIdAndAge11();
        Mockito.when(queryRunnerProvider.query(jsonString1)).thenReturn(rs7);
        Mockito.when(queryRunnerProvider.query(jsonString2)).thenReturn(rs8);
        SwiftResultSet newResult4 = bean.relationTable(relationColomnBean1, relationColomnBean2, queryBean1, queryBean2);
        newResult4.hasNext();
        Assert.assertEquals("[0, name0, 0, age0]",newResult4.getNextRow().getValues().toString());
        Assert.assertEquals("[1, name1, 1, age1]",newResult4.getNextRow().getValues().toString());
        Assert.assertEquals("[2, name2, 2, age2]",newResult4.getNextRow().getValues().toString());
        Assert.assertEquals("[3, name3, 3, age3]",newResult4.getNextRow().getValues().toString());
        Assert.assertEquals("[4, name4, 4, age4]",newResult4.getNextRow().getValues().toString());
    }


    @Test
    public void test1toN() throws Exception {

        String relationColomnBean1 = "id";
        String relationColomnBean2 = "id";

        RelationQueryInfoBean bean = new RelationQueryInfoBean();
        String jsonString1 = QueryBeanFactory.queryBean2String(queryBean1);
        String jsonString2 = QueryBeanFactory.queryBean2String(queryBean2);

        //测试 一对多 没有溢出
        SwiftResultSet rs1 = InitResultSetUtils.makeResultSetIdAndName1();
        SwiftResultSet rs2 = InitResultSetUtils.makeResultSetIdAndAge2();
        Mockito.when(queryRunnerProvider.query(jsonString1)).thenReturn(rs1);
        Mockito.when(queryRunnerProvider.query(jsonString2)).thenReturn(rs2);

        SwiftResultSet newResult1 = bean.relationTable(relationColomnBean1, relationColomnBean2, queryBean1, queryBean2);
        newResult1.hasNext();
        Assert.assertEquals("[0, name0, 0, age00]",newResult1.getNextRow().getValues().toString());
        Assert.assertEquals("[0, name0, 0, age01]",newResult1.getNextRow().getValues().toString());
        Assert.assertEquals("[1, name1, 1, age10]",newResult1.getNextRow().getValues().toString());
        Assert.assertEquals("[1, name1, 1, age11]",newResult1.getNextRow().getValues().toString());
        Assert.assertEquals("[2, name2, 2, age20]",newResult1.getNextRow().getValues().toString());
        Assert.assertEquals("[2, name2, 2, age21]",newResult1.getNextRow().getValues().toString());
        Assert.assertEquals("[3, name3, 3, age30]",newResult1.getNextRow().getValues().toString());
        Assert.assertEquals("[3, name3, 3, age31]",newResult1.getNextRow().getValues().toString());
        Assert.assertEquals("[4, name4, 4, age40]",newResult1.getNextRow().getValues().toString());
        Assert.assertEquals("[4, name4, 4, age41]",newResult1.getNextRow().getValues().toString());


        //测试 一对多 左边溢出
        SwiftResultSet rs3 = InitResultSetUtils.makeResultSetIdAndName2();
        SwiftResultSet rs4 = InitResultSetUtils.makeResultSetIdAndAge2();
        Mockito.when(queryRunnerProvider.query(jsonString1)).thenReturn(rs3);
        Mockito.when(queryRunnerProvider.query(jsonString2)).thenReturn(rs4);

        SwiftResultSet newResult2 = bean.relationTable(relationColomnBean1, relationColomnBean2, queryBean1, queryBean2);
        newResult2.hasNext();
        Assert.assertEquals("[0, name0, 0, age00]",newResult2.getNextRow().getValues().toString());
        Assert.assertEquals("[0, name0, 0, age01]",newResult2.getNextRow().getValues().toString());
        Assert.assertEquals("[1, name1, 1, age10]",newResult2.getNextRow().getValues().toString());
        Assert.assertEquals("[1, name1, 1, age11]",newResult2.getNextRow().getValues().toString());
        Assert.assertEquals("[2, name2, 2, age20]",newResult2.getNextRow().getValues().toString());
        Assert.assertEquals("[2, name2, 2, age21]",newResult2.getNextRow().getValues().toString());
        Assert.assertEquals("[3, name3, 3, age30]",newResult2.getNextRow().getValues().toString());
        Assert.assertEquals("[3, name3, 3, age31]",newResult2.getNextRow().getValues().toString());
        Assert.assertEquals("[4, name4, 4, age40]",newResult2.getNextRow().getValues().toString());
        Assert.assertEquals("[4, name4, 4, age41]",newResult2.getNextRow().getValues().toString());

        //测试 一对多 右边溢出
        SwiftResultSet rs5 = InitResultSetUtils.makeResultSetIdAndName1();
        SwiftResultSet rs6 = InitResultSetUtils.makeResultSetIdAndAge3();
        Mockito.when(queryRunnerProvider.query(jsonString1)).thenReturn(rs5);
        Mockito.when(queryRunnerProvider.query(jsonString2)).thenReturn(rs6);

        SwiftResultSet newResult3 = bean.relationTable(relationColomnBean1, relationColomnBean2, queryBean1, queryBean2);
        newResult3.hasNext();
        Assert.assertEquals("[0, name0, 0, age00]",newResult3.getNextRow().getValues().toString());
        Assert.assertEquals("[0, name0, 0, age01]",newResult3.getNextRow().getValues().toString());
        Assert.assertEquals("[1, name1, 1, age10]",newResult3.getNextRow().getValues().toString());
        Assert.assertEquals("[1, name1, 1, age11]",newResult3.getNextRow().getValues().toString());
        Assert.assertEquals("[2, name2, 2, age20]",newResult3.getNextRow().getValues().toString());
        Assert.assertEquals("[2, name2, 2, age21]",newResult3.getNextRow().getValues().toString());
        Assert.assertEquals("[3, name3, 3, age30]",newResult3.getNextRow().getValues().toString());
        Assert.assertEquals("[3, name3, 3, age31]",newResult3.getNextRow().getValues().toString());
        Assert.assertEquals("[4, name4, 4, age40]",newResult3.getNextRow().getValues().toString());
        Assert.assertEquals("[4, name4, 4, age41]",newResult3.getNextRow().getValues().toString());

        //测试两边同时溢出
        SwiftResultSet rs7 = InitResultSetUtils.makeResultSetIdAndName2();
        SwiftResultSet rs8 = InitResultSetUtils.makeResultSetIdAndAge3();
        Mockito.when(queryRunnerProvider.query(jsonString1)).thenReturn(rs7);
        Mockito.when(queryRunnerProvider.query(jsonString2)).thenReturn(rs8);

        SwiftResultSet newResult4 = bean.relationTable(relationColomnBean1, relationColomnBean2, queryBean1, queryBean2);
        newResult4.hasNext();
        Assert.assertEquals("[0, name0, 0, age00]",newResult4.getNextRow().getValues().toString());
        Assert.assertEquals("[0, name0, 0, age01]",newResult4.getNextRow().getValues().toString());
        Assert.assertEquals("[1, name1, 1, age10]",newResult4.getNextRow().getValues().toString());
        Assert.assertEquals("[1, name1, 1, age11]",newResult4.getNextRow().getValues().toString());
        Assert.assertEquals("[2, name2, 2, age20]",newResult4.getNextRow().getValues().toString());
        Assert.assertEquals("[2, name2, 2, age21]",newResult4.getNextRow().getValues().toString());
        Assert.assertEquals("[3, name3, 3, age30]",newResult4.getNextRow().getValues().toString());
        Assert.assertEquals("[3, name3, 3, age31]",newResult4.getNextRow().getValues().toString());
        Assert.assertEquals("[4, name4, 4, age40]",newResult4.getNextRow().getValues().toString());
        Assert.assertEquals("[4, name4, 4, age41]",newResult4.getNextRow().getValues().toString());
    }

    //三表(以上) 联表 测试
    @Test
    public void testAllRelation() throws Exception {
        String[] relationName1 = {"id","id"};
        String[] relationName2 = {"id","id"};

        RelationQueryInfoBean bean = new RelationQueryInfoBean();
        String jsonString1 = QueryBeanFactory.queryBean2String(queryBean1);
        String jsonString2 = QueryBeanFactory.queryBean2String(queryBean2);
        String jsonString3 = QueryBeanFactory.queryBean2String(queryBean3);

        //测试 一对一对一 没有溢出
        SwiftResultSet rs1 = InitResultSetUtils.makeResultSetIdAndName1();
        SwiftResultSet rs2 = InitResultSetUtils.makeResultSetIdAndAge1();
        SwiftResultSet rs3 = InitResultSetUtils.makeResultSetIdAndName1();
        Mockito.when(queryRunnerProvider.query(jsonString1)).thenReturn(rs1);
        Mockito.when(queryRunnerProvider.query(jsonString2)).thenReturn(rs2);
        Mockito.when(queryRunnerProvider.query(jsonString3)).thenReturn(rs3);

        List<SingleInfoBean> beanList1 = Arrays.asList(queryBean1,queryBean2,queryBean3);
        List<String[]> relationNameList1 = Arrays.asList(relationName1,relationName2);
        SwiftResultSet newResult1 = bean.relationAllTables(relationNameList1, beanList1);
        newResult1.hasNext();
        Assert.assertEquals("[id, name, id, age, id, name]",newResult1.getMetaData().getFieldNames().toString());
        Assert.assertEquals("[0, name0, 0, age0, 0, name0]",newResult1.getNextRow().getValues().toString());
        Assert.assertEquals("[1, name1, 1, age1, 1, name1]",newResult1.getNextRow().getValues().toString());
        Assert.assertEquals("[2, name2, 2, age2, 2, name2]",newResult1.getNextRow().getValues().toString());
        Assert.assertEquals("[3, name3, 3, age3, 3, name3]",newResult1.getNextRow().getValues().toString());
        Assert.assertEquals("[4, name4, 4, age4, 4, name4]",newResult1.getNextRow().getValues().toString());

        //测试 一对多对多 没有溢出
        SwiftResultSet rs4 = InitResultSetUtils.makeResultSetIdAndName1();
        SwiftResultSet rs5 = InitResultSetUtils.makeResultSetIdAndAge2();
        SwiftResultSet rs6 = InitResultSetUtils.makeResultSetIdAndName3();
        Mockito.when(queryRunnerProvider.query(jsonString1)).thenReturn(rs4);
        Mockito.when(queryRunnerProvider.query(jsonString2)).thenReturn(rs5);
        Mockito.when(queryRunnerProvider.query(jsonString3)).thenReturn(rs6);

        List<SingleInfoBean> beanList2 = Arrays.asList(queryBean1,queryBean2,queryBean3);
        List<String[]> relationNameList2 = Arrays.asList(relationName1,relationName2);
        SwiftResultSet newResult2 = bean.relationAllTables(relationNameList2, beanList2);
        newResult2.hasNext();
        Assert.assertEquals("[id, name, id, age, id, name]",newResult1.getMetaData().getFieldNames().toString());

       List<Row> resList1 = new ArrayList<>();
       while (newResult2.hasNext()){
           Row row = newResult2.getNextRow();
           resList1.add(row);
       }
        Assert.assertEquals("[0, name0, 0, age00, 0, name00]",resList1.get(0).getValues().toString());
        Assert.assertEquals("[1, name1, 1, age10, 1, name10]",resList1.get(6).getValues().toString());
        Assert.assertEquals("[4, name4, 4, age40, 4, name40]",resList1.get(24).getValues().toString());
        Assert.assertEquals(30,resList1.size());

        //测试一对多对多  有溢出
        SwiftResultSet rs7 = InitResultSetUtils.makeResultSetIdAndName2();
        SwiftResultSet rs8 = InitResultSetUtils.makeResultSetIdAndAge3();
        SwiftResultSet rs9 = InitResultSetUtils.makeResultSetIdAndName3();
        Mockito.when(queryRunnerProvider.query(jsonString1)).thenReturn(rs7);
        Mockito.when(queryRunnerProvider.query(jsonString2)).thenReturn(rs8);
        Mockito.when(queryRunnerProvider.query(jsonString3)).thenReturn(rs9);

        List<SingleInfoBean> beanList3 = Arrays.asList(queryBean1,queryBean2,queryBean3);
        List<String[]> relationNameList3 = Arrays.asList(relationName1,relationName2);
        SwiftResultSet newResult3 = bean.relationAllTables(relationNameList3, beanList3);
        newResult3.hasNext();
        Assert.assertEquals("[id, name, id, age, id, name]",newResult1.getMetaData().getFieldNames().toString());
        Assert.assertEquals("[0, name0, 0, age00, 0, name00]",resList1.get(0).getValues().toString());
        Assert.assertEquals("[1, name1, 1, age10, 1, name10]",resList1.get(6).getValues().toString());
        Assert.assertEquals("[1, name1, 1, age10, 1, name11]",resList1.get(7).getValues().toString());
        Assert.assertEquals("[4, name4, 4, age40, 4, name40]",resList1.get(24).getValues().toString());
        Assert.assertEquals("[4, name4, 4, age40, 4, name42]",resList1.get(26).getValues().toString());
        Assert.assertEquals(30,resList1.size());
    }



}
