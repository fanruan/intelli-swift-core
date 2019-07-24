package com.fr.swift.cloud.relation;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.result.RowSwiftResultSet;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Create by lifan on 2019-06-18 10:48
 */
public class InitResultSetUtils {

    //用于平衡测试
    //id_name  构建resultSet
    /*
    id  name
    0  name0
    1  name1
    2  name2
    3  name3
    4  name4
     */
    public static SwiftResultSet makeResultSetIdAndName1() {
        //构建SwiftResultSet1
        List<SwiftMetaDataColumn> swiftMetaDataColumns = Arrays.asList(
                new MetaDataColumnBean("id", Types.INTEGER),
                new MetaDataColumnBean("name", Types.VARCHAR));
        SwiftMetaData metaData = new SwiftMetaDataBean("id_name", swiftMetaDataColumns);
        List<Row> rowList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            List<Object> values = new ArrayList<>();
            ListBasedRow row = new ListBasedRow();
            String name = "name" + i;
            values.add(i);
            values.add(name);
            row.setValues(values);
            rowList.add(row);

        }
        SwiftResultSet resultSet1 = new RowSwiftResultSet(metaData, rowList);
        return resultSet1;
    }

    //左侧溢出测试
    /*
    id  name
    0  name0
    1  name1
    2  name2
    3  name3
    4  name4
    5  name5
    6  name6
     */
    public static SwiftResultSet makeResultSetIdAndName2() {
        //构建SwiftResultSet1
        List<SwiftMetaDataColumn> swiftMetaDataColumns = Arrays.asList(
                new MetaDataColumnBean("id", Types.INTEGER),
                new MetaDataColumnBean("name", Types.VARCHAR));
        SwiftMetaData metaData1 = new SwiftMetaDataBean("id_name", swiftMetaDataColumns);
        List<Row> rowList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            List<Object> values = new ArrayList<>();
            ListBasedRow row = new ListBasedRow();
            String name = "name" + i;
            values.add(i);
            values.add(name);
            row.setValues(values);
            rowList.add(row);
        }
        SwiftResultSet resultSet = new RowSwiftResultSet(metaData1, rowList);
        return resultSet;
    }

    //用于平衡测试 一对多
    /*
    id  name
    0   name00
    0   name01
    0   name02
    1   name10
    1   name11
    1   name12
    2   name20
    2   name21
    2   name22
    3   name30
    3   name31
    3   name32
    4   name40
    4   name41
    4   name42
     */
    public static SwiftResultSet makeResultSetIdAndName3() {
        //id_age
        //多对多用的swiftResultSet
        List<SwiftMetaDataColumn> swiftMetaDataColumns = Arrays.asList(
                new MetaDataColumnBean("id", Types.INTEGER),
                new MetaDataColumnBean("name", Types.VARCHAR));
        SwiftMetaData metaData = new SwiftMetaDataBean("id_age", swiftMetaDataColumns);
        List<Row> rowList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            List<Object> values1 = new ArrayList<>();
            List<Object> values2 = new ArrayList<>();
            List<Object> values3 = new ArrayList<>();

            ListBasedRow row1 = new ListBasedRow();
            ListBasedRow row2 = new ListBasedRow();
            ListBasedRow row3 = new ListBasedRow();
            String age1 = "name" + i + "0";
            String age2 = "name" + i + "1";
            String age3 = "name" + i + "2";
            values1.add(i);
            values1.add(age1);
            row1.setValues(values1);

            values2.add(i);
            values2.add(age2);
            row2.setValues(values2);

            values3.add(i);
            values3.add(age3);
            row3.setValues(values3);
            rowList.add(row1);
            rowList.add(row2);
            rowList.add(row3);

        }
        SwiftResultSet resultSet = new RowSwiftResultSet(metaData, rowList);
        return resultSet;
    }


    /* 用于平衡测试
    userId   age
    0    age0
    1    age1
    2    age2
    3    age3
    4    age4
     */
    public static SwiftResultSet makeResultSetIdAndAge1() {
        //构建SwiftResultSet2 一对一
        List<SwiftMetaDataColumn> swiftMetaDataColumns = Arrays.asList(
                new MetaDataColumnBean("userId", Types.INTEGER),
                new MetaDataColumnBean("age", Types.VARCHAR));
        SwiftMetaData metaData = new SwiftMetaDataBean("userId_age", swiftMetaDataColumns);
        List<Row> rowList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            List<Object> values = new ArrayList<>();
            ListBasedRow row = new ListBasedRow();
            String age = "age" + i;
            values.add(i);
            values.add(age);
            row.setValues(values);
            rowList.add(row);
        }
        SwiftResultSet resultSet = new RowSwiftResultSet(metaData, rowList);
        return resultSet;
    }


    /* 用于右边溢出测试
    userId   age
    0    age0
    1    age1
    2    age2
    3    age3
    4    age4
    7    age7
    8    age8
     */
    public static SwiftResultSet makeResultSetIdAndAge11() {
        //构建SwiftResultSet2 一对一
        List<SwiftMetaDataColumn> swiftMetaDataColumns = Arrays.asList(
                new MetaDataColumnBean("userId", Types.INTEGER),
                new MetaDataColumnBean("age", Types.VARCHAR));
        SwiftMetaData metaData = new SwiftMetaDataBean("userId_age", swiftMetaDataColumns);
        List<Row> rowList = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (i == 5 || i == 6) {
                continue;
            }
            List<Object> values = new ArrayList<>();
            ListBasedRow row = new ListBasedRow();
            String age = "age" + i;
            values.add(i);
            values.add(age);
            row.setValues(values);
            rowList.add(row);
        }
        SwiftResultSet resultSet = new RowSwiftResultSet(metaData, rowList);
        return resultSet;
    }

    //用于平衡测试 一对多
    /*
    userId  age
    0   age00
    0   age01
    1   age10
    1   age11
    2   age20
    2   age21
    3   age30
    3   age31
    4   age40
    4   age41
     */
    public static SwiftResultSet makeResultSetIdAndAge2() {
        //id_age
        //多对多用的swiftResultSet
        List<SwiftMetaDataColumn> swiftMetaDataColumns = Arrays.asList(
                new MetaDataColumnBean("userId", Types.INTEGER),
                new MetaDataColumnBean("age", Types.VARCHAR));
        SwiftMetaData metaData = new SwiftMetaDataBean("userId_age", swiftMetaDataColumns);
        List<Row> rowList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            List<Object> values1 = new ArrayList<>();
            List<Object> values2 = new ArrayList<>();

            ListBasedRow row1 = new ListBasedRow();
            ListBasedRow row2 = new ListBasedRow();
            String age1 = "age" + i + "0";
            String age2 = "age" + i + "1";
            values1.add(i);
            values1.add(age1);
            row1.setValues(values1);

            values2.add(i);
            values2.add(age2);
            row2.setValues(values2);
            rowList.add(row1);
            rowList.add(row2);

        }
        SwiftResultSet resultSet = new RowSwiftResultSet(metaData, rowList);
        return resultSet;
    }

    //用于右边溢出
    /*
    userId  age
    0   age00
    0   age01
    1   age10
    1   age11
    2   age20
    2   age21
    3   age30
    3   age31
    4   age40
    4   age41
    7   age70
    7   age71
    8   age80
    8   age81
     */
    public static SwiftResultSet makeResultSetIdAndAge3() {
        //id_age
        //多对多用的swiftResultSet
        List<SwiftMetaDataColumn> swiftMetaDataColumns = Arrays.asList(
                new MetaDataColumnBean("userId", Types.INTEGER),
                new MetaDataColumnBean("age", Types.VARCHAR));
        SwiftMetaData metaData = new SwiftMetaDataBean("userId_age", swiftMetaDataColumns);
        List<Row> rowList = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (i == 5 || i == 6) {
                continue;
            }
            List<Object> values1 = new ArrayList<>();
            List<Object> values2 = new ArrayList<>();

            ListBasedRow row1 = new ListBasedRow();
            ListBasedRow row2 = new ListBasedRow();
            String age1 = "age" + i + "0";
            String age2 = "age" + i + "1";
            values1.add(i);
            values1.add(age1);
            row1.setValues(values1);

            values2.add(i);
            values2.add(age2);
            row2.setValues(values2);
            rowList.add(row1);
            rowList.add(row2);

        }
        SwiftResultSet resultSet = new RowSwiftResultSet(metaData, rowList);
        return resultSet;
    }


    //用于右边溢出
    /*
    id    name     userId         age
    0     li         0           age00
    1     fan        0           age01
    2     li         1           age10
    3     fan        1           age11
    4     li         2           age20
    5     fan        2           age21
    6     li         3           age30
    7     fan        3           age31
    8     li         3           age40
    9     fan        4           age41
    10     li        4            age70
    11     fan       5            age71
    12     li        5            age80
    13     fan       5            age81
     */
    public static SwiftResultSet makeResultSetIdUserIdAndAge() {
        //id_age
        //多对多用的swiftResultSet
        List<SwiftMetaDataColumn> swiftMetaDataColumns = Arrays.asList(
                new MetaDataColumnBean("id", Types.INTEGER),
                new MetaDataColumnBean("name", Types.VARCHAR),
                new MetaDataColumnBean("userId", Types.INTEGER),
                new MetaDataColumnBean("age", Types.VARCHAR));
        SwiftMetaData metaData = new SwiftMetaDataBean("id_userId_age", swiftMetaDataColumns);
        List<Row> rowList = new ArrayList<>();

        String[] data = {"0,li,0,age00", "1,fan,0,age01", "2,li,1,age10", "3,fan,1,age11", "4,li,2,age20", "5,fan,2,age21", "6,li,3,age30",
                "7,fan,3,age31", "8,li,3,age40", "9,fan,4,age41", "10,li,4,age70", "11,fan,5,age71", "12,li,5,age80", "13,fan,5,age81"};
        for (String str : data) {
            List<Object> value = new ArrayList<>();
            ListBasedRow row = new ListBasedRow();
            String[] elements = str.split(",");
            for (int i = 0; i < elements.length; i++) {
                if (i == 0 || i == 2) {
                    value.add(Integer.valueOf(elements[i]));
                } else {
                    value.add(elements[i]);
                }
            }
            row.setValues(value);
            rowList.add(row);
        }

        SwiftResultSet resultSet = new RowSwiftResultSet(metaData, rowList);
        return resultSet;
    }


}
