package com.fr.swift.cloud.util;

import com.fr.swift.annotation.Negative;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.query.QueryRunnerProvider;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.query.info.bean.query.SingleInfoBean;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.result.RowSwiftResultSet;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.ListMutableRow;
import com.fr.swift.source.MutableRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create by lifan on 2019-06-14 10:00
 */
public class RelationQueryUtils {
    /**
     * @description 对多表进行关联  columnNames的元素个数要比queryBeans的元素个数少一个
     * @param columnNames 关联表的关联字段名
     * @param queryBeans 被关联的querybeans
     * @return SwiftResultSet的结果集
     * @throws Exception
     */
    public static SwiftResultSet relationAllTablesDFS(List<String[]> columnNames, List<SingleInfoBean> queryBeans) throws Exception {
        List<Row> res = new ArrayList<>();
        //初始化resultset
        List<SwiftResultSet> resultSets = new ArrayList<>();
        for(QueryBean queryBean:queryBeans){
            SwiftResultSet resultSet = QueryRunnerProvider.getInstance().query(QueryBeanFactory.queryBean2String(queryBean));
            resultSets.add(resultSet);
        }

        List<Map> maps = new ArrayList<>(); //只存从第一个开始的resultset
        int index = 0;//第几个关联
        for (int i=1;i<resultSets.size();i++) {
            SwiftResultSet originResultSet = resultSets.get(i);
            Map<Object, List<Row>> map = new HashMap<>();
            //计算字段所在第几列
            int rowIndex = originResultSet.getMetaData().getColumnIndex(columnNames.get(index)[1]) - 1;
            while (originResultSet.hasNext()) {
                Row row = originResultSet.getNextRow();
                Object obj = row.getValue(rowIndex);
                if (!map.containsKey(obj)) {
                    List<Row> rowList = new ArrayList<>();
                    rowList.add(row);
                    map.put(obj, rowList);
                } else {
                    List<Row> rowList = map.get(obj);
                    rowList.add(row);
                    map.put(obj, rowList);
                }
            }
            maps.add(map);
            index++;
        }

        SwiftResultSet firstResultSet = resultSets.get(0);
        while(firstResultSet.hasNext()) {
            Row row = firstResultSet.getNextRow();
            DFS(columnNames, res, 0, maps,row,row,resultSets);
        }
        //构建返回的结果SwiftResultSet  使用result1和result2的metadatacolumn
        List<SwiftMetaDataColumn> swiftMetaDataColumns = new ArrayList<>();

        for (SwiftResultSet resultSet:resultSets) {
            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                swiftMetaDataColumns.add(resultSet.getMetaData().getColumn(i));
            }
            resultSet.close();
        }

        SwiftMetaData metaData = new SwiftMetaDataBean("table", swiftMetaDataColumns);
        SwiftResultSet swiftResultSet = new RowSwiftResultSet(metaData, res);
        return swiftResultSet;
    }

    /**
     * @description 深度遍历算法 实现多表关联
     * @param columnNames 关联字段
     * @param res 结果集
     * @param resultSetIndex 遍历到第几个resultSet
     * @param maps 把从第二个resultset开始的结果集放到map中便于查询
     * @param row 已经关联累加的行
     * @param previousRow 上一个row
     * @param resultSets 所有结果集列表
     * @throws SQLException
     */
    private static void DFS(List<String[]> columnNames, List<Row> res, int resultSetIndex, List<Map> maps, Row row,Row previousRow,
                            List<SwiftResultSet> resultSets) throws SQLException {
        if(resultSetIndex==columnNames.size()){
            return;
        }
        SwiftResultSet previousResultSet = resultSets.get(resultSetIndex);//下一个需要被关联的resultset
        int relationIndex = previousResultSet.getMetaData().getColumnIndex(columnNames.get(resultSetIndex)[0])-1;//被关联字段所在位置
        Map<Object,List<Row>> map = maps.get(resultSetIndex);//获取对应的map
        List<Row> rowList = map.get(previousRow.getValue(relationIndex)); //获取关联的List<Row>
        if(rowList==null){
            res.add(row);
            return;
        }
        for (Row row1:rowList){
            //row1 = row+row1
            MutableRow newRow = new ListMutableRow(row);
            MutableRow newRow1 = new ListMutableRow(row1);
            newRow.addAllRowElement(newRow1);//变成新的row
            if (resultSetIndex==columnNames.size()-1){
                res.add(newRow);
                continue;
            }
            DFS(columnNames,res,resultSetIndex+1,maps,newRow,row1,resultSets);
        }
    }


    /**
     * @description 对多表进行关联
     * @param columnNames 关联表的关联字段名
     * @param queryBeans 被关联的querybeans
     * @return SwiftResultSet的结果集
     * @throws Exception
     */
    @Deprecated
    @Negative(until = "201907")
    public static SwiftResultSet relationAllTables(List<String[]> columnNames, List<SingleInfoBean> queryBeans) throws Exception {
        int index = 0;
        SwiftResultSet resultSet1 = QueryRunnerProvider.getInstance().query(QueryBeanFactory.queryBean2String(queryBeans.get(0)));
        SwiftResultSet resultSet2;
        for (String[] names : columnNames) {
            resultSet2 = QueryRunnerProvider.getInstance().query(QueryBeanFactory.queryBean2String(queryBeans.get(++index)));
            resultSet1 = relation(names[0], names[1], resultSet1, resultSet2);
        }
        return resultSet1;
    }

    /**
     * @description 根据queryBean和关联字段两表关联
     * @param columnName1 第一个表的关联字段
     * @param columnName2 第二个表的关联字段
     * @param q1 第一个querybean
     * @param q2 第二个querybean
     * @return 结果集
     * @throws Exception
     */
    @Deprecated
    @Negative(until = "201907")
    public static SwiftResultSet relationTable(String columnName1, String columnName2, SingleInfoBean q1, SingleInfoBean q2) throws Exception {
        SwiftResultSet resultSet1 = QueryRunnerProvider.getInstance().query(QueryBeanFactory.queryBean2String(q1));
        SwiftResultSet resultSet2 = QueryRunnerProvider.getInstance().query(QueryBeanFactory.queryBean2String(q2));
        return relation(columnName1, columnName2, resultSet1, resultSet2);
    }

    /**
     * @description 根据SwiftResultSet和关联字段 进行关联
     * @param columnName1
     * @param columnName2
     * @param resultSet1
     * @param resultSet2
     * @return 关联后的结果集
     * @throws Exception
     */
    @Deprecated
    @Negative(until = "201907")
    public static SwiftResultSet relation(String columnName1, String columnName2, SwiftResultSet resultSet1, SwiftResultSet resultSet2) throws Exception {

        //第一个关联字段在第一张表的列数
        int index1 = resultSet1.getMetaData().getColumnIndex(columnName1)-1;
        //第二个关联字段在第二张表的列数
        int index2 = resultSet2.getMetaData().getColumnIndex(columnName2)-1;
        //联表后结果字段名 resultColumnName
        List<String> resultColumnName = resultSet1.getMetaData().getFieldNames();
        List<String> resultColumn2 = resultSet2.getMetaData().getFieldNames();
        resultColumnName.addAll(resultColumn2);

        //为了可以反复遍历 存在list中
        List<MutableRow> rowList = new ArrayList<>();
        while (resultSet2.hasNext()) {
            MutableRow row = new ListMutableRow(resultSet2.getNextRow());
            rowList.add(row);
        }

        //结果集
        List<Row> res = new ArrayList<>();
        while (resultSet1.hasNext()) {
            MutableRow row1 = new ListMutableRow(resultSet1.getNextRow());
//            String columnData = String.valueOf(row1.getValue(index1));
            Object columnData = row1.getValue(index1);
            for (MutableRow row2 : rowList) {
                //把关联字段对应数据组后 存放到新的row中并加入到结果集中
                if(row2.getValue(index2)!=null) {
                    if (row2.getValue(index2).equals(columnData)) {
                        MutableRow newRow = new ListMutableRow(row1);
                        newRow.addAllRowElement(row2);
                        res.add(newRow);
                    }
                }
            }
        }

        //构建返回的结果SwiftResultSet  使用result1和result2的metadatacolumn
        List<SwiftMetaDataColumn> swiftMetaDataColumns = new ArrayList<>();
        for (int i=1;i<=resultSet1.getMetaData().getColumnCount();i++){
            swiftMetaDataColumns.add(resultSet1.getMetaData().getColumn(i));
        }
        for(int i=1;i<=resultSet2.getMetaData().getColumnCount();i++){
            swiftMetaDataColumns.add(resultSet2.getMetaData().getColumn(i));
        }
        resultSet1.close();
        resultSet2.close();
        SwiftMetaData metaData = new SwiftMetaDataBean("table", swiftMetaDataColumns);
        SwiftResultSet swiftResultSet = new RowSwiftResultSet(metaData, res);
        return swiftResultSet;
    }
}
