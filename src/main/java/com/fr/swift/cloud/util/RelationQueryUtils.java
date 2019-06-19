package com.fr.swift.cloud.util;

import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.query.QueryRunnerProvider;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.query.info.bean.query.SingleInfoBean;
import com.fr.swift.result.RowSwiftResultSet;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.ListRelationRow;
import com.fr.swift.source.RelationRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by lifan on 2019-06-14 10:00
 */
public class RelationQueryUtils {

    /**
     * @description 对多表进行关联
     * @param columnNames 关联表的关联字段名
     * @param queryBeans 被关联的querybeans
     * @return SwiftResultSet的结果集
     * @throws Exception
     */
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
        List<RelationRow> rowList = new ArrayList<>();
        while (resultSet2.hasNext()) {
            RelationRow row = new ListRelationRow(resultSet2.getNextRow());
            rowList.add(row);
        }

        //结果集
        List<Row> res = new ArrayList<>();
        while (resultSet1.hasNext()) {
            RelationRow row1 = new ListRelationRow(resultSet1.getNextRow());
//            String columnData = String.valueOf(row1.getValue(index1));
            Object columnData = row1.getValue(index1);
            for (RelationRow row2 : rowList) {
                //把关联字段对应数据组后 存放到新的row中并加入到结果集中
                if(row2.getValue(index2)!=null) {
                    if (row2.getValue(index2).equals(columnData)) {
                        RelationRow newRow = new ListRelationRow(row1);
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
        SwiftMetaData metaData = new SwiftMetaDataBean("table", swiftMetaDataColumns);
        SwiftResultSet swiftResultSet = new RowSwiftResultSet(metaData, res);
        return swiftResultSet;
    }

}
