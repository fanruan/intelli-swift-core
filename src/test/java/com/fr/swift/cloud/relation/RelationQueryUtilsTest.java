package com.fr.swift.cloud.relation;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.cloud.util.RelationQueryUtils;
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
public class RelationQueryUtilsTest {
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

    /**
     * 测试 一对一 没有溢出
     * @throws Exception
     */
    @Test
    public void test1to1WithBalance() throws Exception {

        String jsonString1 = QueryBeanFactory.queryBean2String(queryBean1);
        String jsonString2 = QueryBeanFactory.queryBean2String(queryBean2);


        SwiftResultSet rs1 = InitResultSetUtils.makeResultSetIdAndName1();
        SwiftResultSet rs2 = InitResultSetUtils.makeResultSetIdAndAge1();
        Mockito.when(queryRunnerProvider.query(jsonString1)).thenReturn(rs1);
        Mockito.when(queryRunnerProvider.query(jsonString2)).thenReturn(rs2);

        String columnRelation[] = {"id","userId"};
        List<String[]> columnName = new ArrayList<>();
        columnName.add(columnRelation);

        List<SingleInfoBean> beans1 = Arrays.asList(queryBean1,queryBean2);

        SwiftResultSet newResult1 = RelationQueryUtils.relationAllTablesDFS(columnName,beans1);
        newResult1.hasNext();
        Assert.assertEquals("[id, name, userId, age]",newResult1.getMetaData().getFieldNames().toString());
        Assert.assertEquals("[0, name0, 0, age0]",newResult1.getNextRow().toString());
        Assert.assertEquals("[1, name1, 1, age1]",newResult1.getNextRow().toString());
        Assert.assertEquals("[2, name2, 2, age2]",newResult1.getNextRow().toString());
        Assert.assertEquals("[3, name3, 3, age3]",newResult1.getNextRow().toString());
        Assert.assertEquals("[4, name4, 4, age4]",newResult1.getNextRow().toString());
    }

    /**
     * 测试 一对一 左边溢出
     * @throws Exception
     */
    @Test
    public void test1to1LeftOut() throws Exception {
        String jsonString1 = QueryBeanFactory.queryBean2String(queryBean1);
        String jsonString2 = QueryBeanFactory.queryBean2String(queryBean2);

        SwiftResultSet rs3 = InitResultSetUtils.makeResultSetIdAndName2();
        SwiftResultSet rs4 = InitResultSetUtils.makeResultSetIdAndAge1();
        Mockito.when(queryRunnerProvider.query(jsonString1)).thenReturn(rs3);
        Mockito.when(queryRunnerProvider.query(jsonString2)).thenReturn(rs4);
        List<SingleInfoBean> beans2 = Arrays.asList(queryBean1,queryBean2);

        String columnRelation[] = {"id","userId"};
        List<String[]> columnName = new ArrayList<>();
        columnName.add(columnRelation);

        SwiftResultSet newResult2 = RelationQueryUtils.relationAllTablesDFS(columnName,beans2);
        newResult2.hasNext();
        Assert.assertEquals("[0, name0, 0, age0]",newResult2.getNextRow().toString());
        Assert.assertEquals("[1, name1, 1, age1]",newResult2.getNextRow().toString());
        Assert.assertEquals("[2, name2, 2, age2]",newResult2.getNextRow().toString());
        Assert.assertEquals("[3, name3, 3, age3]",newResult2.getNextRow().toString());
        Assert.assertEquals("[4, name4, 4, age4]",newResult2.getNextRow().toString());
        Assert.assertEquals("[5, name5]",newResult2.getNextRow().toString());
        Assert.assertEquals("[6, name6]",newResult2.getNextRow().toString());
    }

    /**
     * 测试 一对一 右边溢出
     * @throws Exception
     */
    @Test
    public void test1To1RightOut() throws Exception {
        String jsonString1 = QueryBeanFactory.queryBean2String(queryBean1);
        String jsonString2 = QueryBeanFactory.queryBean2String(queryBean2);

        SwiftResultSet rs5 = InitResultSetUtils.makeResultSetIdAndName1();
        SwiftResultSet rs6 = InitResultSetUtils.makeResultSetIdAndAge11();
        Mockito.when(queryRunnerProvider.query(jsonString1)).thenReturn(rs5);
        Mockito.when(queryRunnerProvider.query(jsonString2)).thenReturn(rs6);
        List<SingleInfoBean> beans3 = Arrays.asList(queryBean1,queryBean2);
        String columnRelation[] = {"id","userId"};
        List<String[]> columnName = new ArrayList<>();
        columnName.add(columnRelation);

        SwiftResultSet newResult3 = RelationQueryUtils.relationAllTablesDFS(columnName,beans3);
        newResult3.hasNext();
        Assert.assertEquals("[0, name0, 0, age0]",newResult3.getNextRow().toString());
        Assert.assertEquals("[1, name1, 1, age1]",newResult3.getNextRow().toString());
        Assert.assertEquals("[2, name2, 2, age2]",newResult3.getNextRow().toString());
        Assert.assertEquals("[3, name3, 3, age3]",newResult3.getNextRow().toString());
        Assert.assertEquals("[4, name4, 4, age4]",newResult3.getNextRow().toString());
    }

    /**
     * 测试一对一 左右都溢出
     * @throws Exception
     */
    @Test
    public void test1To1LeftAndRightOut() throws Exception {
        String jsonString1 = QueryBeanFactory.queryBean2String(queryBean1);
        String jsonString2 = QueryBeanFactory.queryBean2String(queryBean2);

        SwiftResultSet rs7 = InitResultSetUtils.makeResultSetIdAndName2();
        SwiftResultSet rs8 = InitResultSetUtils.makeResultSetIdAndAge11();
        Mockito.when(queryRunnerProvider.query(jsonString1)).thenReturn(rs7);
        Mockito.when(queryRunnerProvider.query(jsonString2)).thenReturn(rs8);
        List<SingleInfoBean> beans4 = Arrays.asList(queryBean1,queryBean2);
        String columnRelation[] = {"id","userId"};
        List<String[]> columnName = new ArrayList<>();
        columnName.add(columnRelation);

        SwiftResultSet newResult4 = RelationQueryUtils.relationAllTablesDFS(columnName,beans4);
        newResult4.hasNext();
        Assert.assertEquals("[0, name0, 0, age0]",newResult4.getNextRow().toString());
        Assert.assertEquals("[1, name1, 1, age1]",newResult4.getNextRow().toString());
        Assert.assertEquals("[2, name2, 2, age2]",newResult4.getNextRow().toString());
        Assert.assertEquals("[3, name3, 3, age3]",newResult4.getNextRow().toString());
        Assert.assertEquals("[4, name4, 4, age4]",newResult4.getNextRow().toString());
        Assert.assertEquals("[5, name5]",newResult4.getNextRow().toString());
        Assert.assertEquals("[6, name6]",newResult4.getNextRow().toString());
    }


    /**
     * 测试一对一 无溢出
     * @throws Exception
     */
    @Test
    public void test1toNWithBalance() throws Exception {

        String jsonString1 = QueryBeanFactory.queryBean2String(queryBean1);
        String jsonString2 = QueryBeanFactory.queryBean2String(queryBean2);

        //测试 一对多 没有溢出
        SwiftResultSet rs1 = InitResultSetUtils.makeResultSetIdAndName1();
        SwiftResultSet rs2 = InitResultSetUtils.makeResultSetIdAndAge2();
        Mockito.when(queryRunnerProvider.query(jsonString1)).thenReturn(rs1);
        Mockito.when(queryRunnerProvider.query(jsonString2)).thenReturn(rs2);

        String columnRelation[] = {"id","userId"};
        List<String[]> columnName = new ArrayList<>();
        columnName.add(columnRelation);

        List<SingleInfoBean> beans1 = Arrays.asList(queryBean1,queryBean2);

        SwiftResultSet newResult1 = RelationQueryUtils.relationAllTablesDFS(columnName,beans1);
        newResult1.hasNext();
        Assert.assertEquals("[id, name, userId, age]",newResult1.getMetaData().getFieldNames().toString());
        Assert.assertEquals("[0, name0, 0, age00]",newResult1.getNextRow().toString());
        Assert.assertEquals("[0, name0, 0, age01]",newResult1.getNextRow().toString());
        Assert.assertEquals("[1, name1, 1, age10]",newResult1.getNextRow().toString());
        Assert.assertEquals("[1, name1, 1, age11]",newResult1.getNextRow().toString());
        Assert.assertEquals("[2, name2, 2, age20]",newResult1.getNextRow().toString());
        Assert.assertEquals("[2, name2, 2, age21]",newResult1.getNextRow().toString());
        Assert.assertEquals("[3, name3, 3, age30]",newResult1.getNextRow().toString());
        Assert.assertEquals("[3, name3, 3, age31]",newResult1.getNextRow().toString());
        Assert.assertEquals("[4, name4, 4, age40]",newResult1.getNextRow().toString());
        Assert.assertEquals("[4, name4, 4, age41]",newResult1.getNextRow().toString());
    }

    /**
     * 测试 一对N 左边溢出
     * @throws Exception
     */
    @Test
    public void test1ToNLeftOut() throws Exception {

        String jsonString1 = QueryBeanFactory.queryBean2String(queryBean1);
        String jsonString2 = QueryBeanFactory.queryBean2String(queryBean2);

        SwiftResultSet rs3 = InitResultSetUtils.makeResultSetIdAndName2();
        SwiftResultSet rs4 = InitResultSetUtils.makeResultSetIdAndAge2();
        Mockito.when(queryRunnerProvider.query(jsonString1)).thenReturn(rs3);
        Mockito.when(queryRunnerProvider.query(jsonString2)).thenReturn(rs4);

        List<SingleInfoBean> beans2 = Arrays.asList(queryBean1,queryBean2);
        String columnRelation[] = {"id","userId"};
        List<String[]> columnName = new ArrayList<>();
        columnName.add(columnRelation);

        SwiftResultSet newResult2 = RelationQueryUtils.relationAllTablesDFS(columnName,beans2);
        newResult2.hasNext();
        Assert.assertEquals("[0, name0, 0, age00]",newResult2.getNextRow().toString());
        Assert.assertEquals("[0, name0, 0, age01]",newResult2.getNextRow().toString());
        Assert.assertEquals("[1, name1, 1, age10]",newResult2.getNextRow().toString());
        Assert.assertEquals("[1, name1, 1, age11]",newResult2.getNextRow().toString());
        Assert.assertEquals("[2, name2, 2, age20]",newResult2.getNextRow().toString());
        Assert.assertEquals("[2, name2, 2, age21]",newResult2.getNextRow().toString());
        Assert.assertEquals("[3, name3, 3, age30]",newResult2.getNextRow().toString());
        Assert.assertEquals("[3, name3, 3, age31]",newResult2.getNextRow().toString());
        Assert.assertEquals("[4, name4, 4, age40]",newResult2.getNextRow().toString());
        Assert.assertEquals("[4, name4, 4, age41]",newResult2.getNextRow().toString());
        Assert.assertEquals("[5, name5]",newResult2.getNextRow().toString());
        Assert.assertEquals("[6, name6]",newResult2.getNextRow().toString());
    }

    /**
     * 测试 一对N 右边溢出
     * @throws Exception
     */
    @Test
    public void test1ToNRightOut() throws Exception {
        String jsonString1 = QueryBeanFactory.queryBean2String(queryBean1);
        String jsonString2 = QueryBeanFactory.queryBean2String(queryBean2);

        SwiftResultSet rs5 = InitResultSetUtils.makeResultSetIdAndName1();
        SwiftResultSet rs6 = InitResultSetUtils.makeResultSetIdAndAge3();
        Mockito.when(queryRunnerProvider.query(jsonString1)).thenReturn(rs5);
        Mockito.when(queryRunnerProvider.query(jsonString2)).thenReturn(rs6);
        List<SingleInfoBean> beans3 = Arrays.asList(queryBean1,queryBean2);

        String columnRelation[] = {"id","userId"};
        List<String[]> columnName = new ArrayList<>();
        columnName.add(columnRelation);


        SwiftResultSet newResult3 = RelationQueryUtils.relationAllTablesDFS(columnName,beans3);
        newResult3.hasNext();
        Assert.assertEquals("[0, name0, 0, age00]",newResult3.getNextRow().toString());
        Assert.assertEquals("[0, name0, 0, age01]",newResult3.getNextRow().toString());
        Assert.assertEquals("[1, name1, 1, age10]",newResult3.getNextRow().toString());
        Assert.assertEquals("[1, name1, 1, age11]",newResult3.getNextRow().toString());
        Assert.assertEquals("[2, name2, 2, age20]",newResult3.getNextRow().toString());
        Assert.assertEquals("[2, name2, 2, age21]",newResult3.getNextRow().toString());
        Assert.assertEquals("[3, name3, 3, age30]",newResult3.getNextRow().toString());
        Assert.assertEquals("[3, name3, 3, age31]",newResult3.getNextRow().toString());
        Assert.assertEquals("[4, name4, 4, age40]",newResult3.getNextRow().toString());
        Assert.assertEquals("[4, name4, 4, age41]",newResult3.getNextRow().toString());
    }

    /**
     * 测试 一对多 左右同时溢出
     * @throws Exception
     */
    @Test
    public void test1ToNLeftRightOut() throws Exception {
        String jsonString1 = QueryBeanFactory.queryBean2String(queryBean1);
        String jsonString2 = QueryBeanFactory.queryBean2String(queryBean2);

        SwiftResultSet rs7 = InitResultSetUtils.makeResultSetIdAndName2();
        SwiftResultSet rs8 = InitResultSetUtils.makeResultSetIdAndAge3();
        Mockito.when(queryRunnerProvider.query(jsonString1)).thenReturn(rs7);
        Mockito.when(queryRunnerProvider.query(jsonString2)).thenReturn(rs8);

        List<SingleInfoBean> beans4 = Arrays.asList(queryBean1,queryBean2);

        String columnRelation[] = {"id","userId"};
        List<String[]> columnName = new ArrayList<>();
        columnName.add(columnRelation);

        SwiftResultSet newResult4 = RelationQueryUtils.relationAllTablesDFS(columnName,beans4);
        newResult4.hasNext();
        Assert.assertEquals("[0, name0, 0, age00]",newResult4.getNextRow().toString());
        Assert.assertEquals("[0, name0, 0, age01]",newResult4.getNextRow().toString());
        Assert.assertEquals("[1, name1, 1, age10]",newResult4.getNextRow().toString());
        Assert.assertEquals("[1, name1, 1, age11]",newResult4.getNextRow().toString());
        Assert.assertEquals("[2, name2, 2, age20]",newResult4.getNextRow().toString());
        Assert.assertEquals("[2, name2, 2, age21]",newResult4.getNextRow().toString());
        Assert.assertEquals("[3, name3, 3, age30]",newResult4.getNextRow().toString());
        Assert.assertEquals("[3, name3, 3, age31]",newResult4.getNextRow().toString());
        Assert.assertEquals("[4, name4, 4, age40]",newResult4.getNextRow().toString());
        Assert.assertEquals("[4, name4, 4, age41]",newResult4.getNextRow().toString());
        Assert.assertEquals("[5, name5]",newResult4.getNextRow().toString());
        Assert.assertEquals("[6, name6]",newResult4.getNextRow().toString());
    }


    /**
     * 三表(以上) 联表 测试 平衡无溢出 1对1对1
     * @throws Exception
     */
    @Test
    public void testAllRelation1To1To1With3TablesBalance() throws Exception {
        String[] relationName1 = {"id","userId"};
        String[] relationName2 = {"userId","id"};

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
        SwiftResultSet newResult1 = RelationQueryUtils.relationAllTablesDFS(relationNameList1, beanList1);
        newResult1.hasNext();
        Assert.assertEquals("[id, name, userId, age, id, name]",newResult1.getMetaData().getFieldNames().toString());
        Assert.assertEquals("[0, name0, 0, age0, 0, name0]",newResult1.getNextRow().toString());
        Assert.assertEquals("[1, name1, 1, age1, 1, name1]",newResult1.getNextRow().toString());
        Assert.assertEquals("[2, name2, 2, age2, 2, name2]",newResult1.getNextRow().toString());
        Assert.assertEquals("[3, name3, 3, age3, 3, name3]",newResult1.getNextRow().toString());
        Assert.assertEquals("[4, name4, 4, age4, 4, name4]",newResult1.getNextRow().toString());

    }

    /**
     * 三表(以上) 联表 测试 平衡无溢出 1对多对多
     * @throws Exception
     */
    @Test
    public void testAllRelation1ToNToNWith3TablesBalance() throws Exception {
        String[] relationName1 = {"id","userId"};
        String[] relationName2 = {"userId","id"};

        String jsonString1 = QueryBeanFactory.queryBean2String(queryBean1);
        String jsonString2 = QueryBeanFactory.queryBean2String(queryBean2);
        String jsonString3 = QueryBeanFactory.queryBean2String(queryBean3);

        SwiftResultSet rs4 = InitResultSetUtils.makeResultSetIdAndName1();
        SwiftResultSet rs5 = InitResultSetUtils.makeResultSetIdAndAge2();
        SwiftResultSet rs6 = InitResultSetUtils.makeResultSetIdAndName3();
        Mockito.when(queryRunnerProvider.query(jsonString1)).thenReturn(rs4);
        Mockito.when(queryRunnerProvider.query(jsonString2)).thenReturn(rs5);
        Mockito.when(queryRunnerProvider.query(jsonString3)).thenReturn(rs6);

        List<SingleInfoBean> beanList2 = Arrays.asList(queryBean1,queryBean2,queryBean3);
        List<String[]> relationNameList2 = Arrays.asList(relationName1,relationName2);
        SwiftResultSet newResult2 = RelationQueryUtils.relationAllTables(relationNameList2, beanList2);
        newResult2.hasNext();
        Assert.assertEquals("[id, name, userId, age, id, name]",newResult2.getMetaData().getFieldNames().toString());

        List<Row> resList1 = new ArrayList<>();
        while (newResult2.hasNext()){
            Row row = newResult2.getNextRow();
            resList1.add(row);
        }
        Assert.assertEquals("[0, name0, 0, age00, 0, name00]",resList1.get(0).toString());
        Assert.assertEquals("[1, name1, 1, age10, 1, name10]",resList1.get(6).toString());
        Assert.assertEquals("[4, name4, 4, age40, 4, name40]",resList1.get(24).toString());
        Assert.assertEquals(30,resList1.size());
    }

    /**
     * 三表(以上) 联表 测试 平衡有溢出 1对多对多
     * @throws Exception
     */
    @Test
    public void testAllRelation1ToNToNWith3TablesOut() throws Exception {
        String[] relationName1 = {"id", "userId"};
        String[] relationName2 = {"userId", "id"};

        String jsonString1 = QueryBeanFactory.queryBean2String(queryBean1);
        String jsonString2 = QueryBeanFactory.queryBean2String(queryBean2);
        String jsonString3 = QueryBeanFactory.queryBean2String(queryBean3);

        //测试一对多对多  有溢出
        SwiftResultSet rs7 = InitResultSetUtils.makeResultSetIdAndName2();
        SwiftResultSet rs8 = InitResultSetUtils.makeResultSetIdAndAge3();
        SwiftResultSet rs9 = InitResultSetUtils.makeResultSetIdAndName3();
        Mockito.when(queryRunnerProvider.query(jsonString1)).thenReturn(rs7);
        Mockito.when(queryRunnerProvider.query(jsonString2)).thenReturn(rs8);
        Mockito.when(queryRunnerProvider.query(jsonString3)).thenReturn(rs9);

        List<SingleInfoBean> beanList3 = Arrays.asList(queryBean1, queryBean2, queryBean3);
        List<String[]> relationNameList3 = Arrays.asList(relationName1, relationName2);
        SwiftResultSet newResult3 = RelationQueryUtils.relationAllTablesDFS(relationNameList3, beanList3);
        newResult3.hasNext();
        List<Row> resList3 = new ArrayList<>();
        while (newResult3.hasNext()) {
            Row row = newResult3.getNextRow();
            resList3.add(row);
        }
        Assert.assertEquals("[id, name, userId, age, id, name]", newResult3.getMetaData().getFieldNames().toString());
        Assert.assertEquals("[0, name0, 0, age00, 0, name00]", resList3.get(0).toString());
        Assert.assertEquals("[1, name1, 1, age10, 1, name10]", resList3.get(6).toString());
        Assert.assertEquals("[1, name1, 1, age10, 1, name11]", resList3.get(7).toString());
        Assert.assertEquals("[4, name4, 4, age40, 4, name40]", resList3.get(24).toString());
        Assert.assertEquals("[4, name4, 4, age40, 4, name42]", resList3.get(26).toString());
        Assert.assertEquals("[5, name5]", resList3.get(30).toString());
        Assert.assertEquals("[6, name6]", resList3.get(31).toString());
        Assert.assertEquals(32, resList3.size());
    }

    /**
     * 测试 一对多对多 右边溢出
     * @throws Exception
     */
    @Test
    public void testAllRelation1ToNToNWith3TablesRightOut() throws Exception {

        String jsonString1 = QueryBeanFactory.queryBean2String(queryBean1);
        String jsonString2 = QueryBeanFactory.queryBean2String(queryBean2);
        String jsonString3 = QueryBeanFactory.queryBean2String(queryBean3);
        //测试一对多对多 右边有溢出
        SwiftResultSet res1 = InitResultSetUtils.makeResultSetIdAndName1();
        SwiftResultSet res2 = InitResultSetUtils.makeResultSetIdAndAge2();
        SwiftResultSet res3 = InitResultSetUtils.makeResultSetIdUserIdAndAge();
        Mockito.when(queryRunnerProvider.query(jsonString1)).thenReturn(res1);
        Mockito.when(queryRunnerProvider.query(jsonString2)).thenReturn(res2);
        Mockito.when(queryRunnerProvider.query(jsonString3)).thenReturn(res3);

        String []relation1 = {"id","userId"};
        String []relation2 = {"userId","userId"};

        List<SingleInfoBean> beanList4 = Arrays.asList(queryBean1,queryBean2,queryBean3);
        List<String[]> relationNameList4 = Arrays.asList(relation1,relation2);
        SwiftResultSet newResult4 = RelationQueryUtils.relationAllTablesDFS(relationNameList4, beanList4);
        newResult4.hasNext();
        List<Row> rowList4 = new ArrayList<>();
        while (newResult4.hasNext()){
            Row row = newResult4.getNextRow();
            rowList4.add(row);
        }
        Assert.assertEquals("[id, name, userId, age, id, name, userId, age]",newResult4.getMetaData().getFieldNames().toString());
        Assert.assertEquals(22,rowList4.size());
        Assert.assertEquals("[0, name0, 0, age00, 0, li, 0, age00]",rowList4.get(0).toString());
        Assert.assertEquals("[0, name0, 0, age00, 1, fan, 0, age01]",rowList4.get(1).toString());
        Assert.assertEquals("[0, name0, 0, age01, 0, li, 0, age00]",rowList4.get(2).toString());
        Assert.assertEquals("[0, name0, 0, age01, 1, fan, 0, age01]",rowList4.get(3).toString());
        Assert.assertEquals("[1, name1, 1, age10, 2, li, 1, age10]",rowList4.get(4).toString());
        Assert.assertEquals("[1, name1, 1, age10, 3, fan, 1, age11]",rowList4.get(5).toString());
        Assert.assertEquals("[1, name1, 1, age11, 2, li, 1, age10]",rowList4.get(6).toString());
        Assert.assertEquals("[1, name1, 1, age11, 3, fan, 1, age11]",rowList4.get(7).toString());
        Assert.assertEquals("[2, name2, 2, age20, 4, li, 2, age20]",rowList4.get(8).toString());
        Assert.assertEquals("[2, name2, 2, age20, 5, fan, 2, age21]",rowList4.get(9).toString());
        Assert.assertEquals("[2, name2, 2, age21, 4, li, 2, age20]",rowList4.get(10).toString());
        Assert.assertEquals("[2, name2, 2, age21, 5, fan, 2, age21]",rowList4.get(11).toString());
        Assert.assertEquals("[3, name3, 3, age30, 6, li, 3, age30]",rowList4.get(12).toString());
        Assert.assertEquals("[3, name3, 3, age30, 7, fan, 3, age31]",rowList4.get(13).toString());
        Assert.assertEquals("[3, name3, 3, age30, 8, li, 3, age40]",rowList4.get(14).toString());
        Assert.assertEquals("[3, name3, 3, age31, 6, li, 3, age30]",rowList4.get(15).toString());
        Assert.assertEquals("[3, name3, 3, age31, 7, fan, 3, age31]",rowList4.get(16).toString());
        Assert.assertEquals("[3, name3, 3, age31, 8, li, 3, age40]",rowList4.get(17).toString());
        Assert.assertEquals("[4, name4, 4, age40, 9, fan, 4, age41]",rowList4.get(18).toString());
        Assert.assertEquals("[4, name4, 4, age40, 10, li, 4, age70]",rowList4.get(19).toString());
        Assert.assertEquals("[4, name4, 4, age41, 9, fan, 4, age41]",rowList4.get(20).toString());
        Assert.assertEquals("[4, name4, 4, age41, 10, li, 4, age70]",rowList4.get(21).toString());





    }

    /**
     * 测试一对多对多 中间溢出
     * @throws Exception
     */
    @Test
    public void testAllRelation1ToNToNWith3TablesMidOut() throws Exception {
        String jsonString1 = QueryBeanFactory.queryBean2String(queryBean1);
        String jsonString2 = QueryBeanFactory.queryBean2String(queryBean2);
        String jsonString3 = QueryBeanFactory.queryBean2String(queryBean3);
        //测试一对多对多 中间溢出
        SwiftResultSet res4 = InitResultSetUtils.makeResultSetIdAndName1();
        SwiftResultSet res5 = InitResultSetUtils.makeResultSetIdUserIdAndAge();
        SwiftResultSet res6 = InitResultSetUtils.makeResultSetIdAndAge2();
        Mockito.when(queryRunnerProvider.query(jsonString1)).thenReturn(res4);
        Mockito.when(queryRunnerProvider.query(jsonString2)).thenReturn(res5);
        Mockito.when(queryRunnerProvider.query(jsonString3)).thenReturn(res6);

        String []relations1 = {"id","userId"};
        String []relations2 = {"id","userId"};

        List<SingleInfoBean> beanList5 = Arrays.asList(queryBean1,queryBean2,queryBean3);
        List<String[]> relationNameList5 = Arrays.asList(relations1,relations2);
        SwiftResultSet newResult5 = RelationQueryUtils.relationAllTablesDFS(relationNameList5, beanList5);
        newResult5.hasNext();
        List<Row> rowList5 = new ArrayList<>();
        while (newResult5.hasNext()){
            Row row = newResult5.getNextRow();
            rowList5.add(row);
        }
        Assert.assertEquals("[id, name, id, name, userId, age, userId, age]",newResult5.getMetaData().getFieldNames().toString());
        Assert.assertEquals(16,rowList5.size());
        Assert.assertEquals("[0, name0, 0, li, 0, age00, 0, age00]",rowList5.get(0).toString());
        Assert.assertEquals("[0, name0, 0, li, 0, age00, 0, age01]",rowList5.get(1).toString());
        Assert.assertEquals("[0, name0, 1, fan, 0, age01, 1, age10]",rowList5.get(2).toString());
        Assert.assertEquals("[0, name0, 1, fan, 0, age01, 1, age11]",rowList5.get(3).toString());
        Assert.assertEquals("[1, name1, 2, li, 1, age10, 2, age20]",rowList5.get(4).toString());
        Assert.assertEquals("[1, name1, 2, li, 1, age10, 2, age21]",rowList5.get(5).toString());
        Assert.assertEquals("[1, name1, 3, fan, 1, age11, 3, age30]",rowList5.get(6).toString());
        Assert.assertEquals("[1, name1, 3, fan, 1, age11, 3, age31]",rowList5.get(7).toString());
        Assert.assertEquals("[2, name2, 4, li, 2, age20, 4, age40]",rowList5.get(8).toString());
        Assert.assertEquals("[2, name2, 4, li, 2, age20, 4, age41]",rowList5.get(9).toString());
        Assert.assertEquals("[2, name2, 5, fan, 2, age21]",rowList5.get(10).toString());
        Assert.assertEquals("[3, name3, 6, li, 3, age30]",rowList5.get(11).toString());
        Assert.assertEquals("[3, name3, 7, fan, 3, age31]",rowList5.get(12).toString());
        Assert.assertEquals("[3, name3, 8, li, 3, age40]",rowList5.get(13).toString());
        Assert.assertEquals("[4, name4, 9, fan, 4, age41]",rowList5.get(14).toString());
        Assert.assertEquals( "[4, name4, 10, li, 4, age70]",rowList5.get(15).toString());


    }

}
