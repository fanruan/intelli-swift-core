package com.fr.swift.result;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.source.ListMutableRow;
import com.fr.swift.source.MutableRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.split.json.JsonColumnSplitRule;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lucifer
 * @date 2019-07-31
 * @description
 * @decripton ResultSet系集成测试
 * @since advanced swift 1.0
 */
public class SwiftMutableResultSetTest {


    public static String[] lines1 = new String[]{"1;1562550000001;a;8;string;a-string",
            "2;1562550000004;b;8;json;{\"id\":\"1\",\"score\":100,\"age\":18}",
            "3;1562550000005;c;8;string;c-string",
            "4;1562550000007;d;8;json;{\"id\":\"3\",\"score\":98,\"age\":20,\"head\":\"black\"}",
            "5;1562550000009;e;8;json;{\"id\":\"5\",\"age\":15,\"type\":\"true\"}"};

    public static String[] lines2 = new String[]{"11;1562550000001;a;8;string;a-string",
            "12;1562550000004;b;8;json;{\"id\":\"12\",\"score\":99,\"age\":28}",
            "13;1562550000006;c;8;string;c-string",
            "14;1562550000007;d;8;json;{\"id\":\"13\",\"score\":98,\"age\":20,\"head\":\"black\"}",
            "15;1562550000010;e;8;json;{\"id\":\"16\",\"age\":25,\"type\":\"false\"}"};

    public static String[] lines3 = new String[]{"21;1562550000001;a;8;string;a-string",
            "22;1562550000004;b;8;json;{\"a\":\"12\",\"b\":99,\"c\":28}",
            "23;1562550000006;c;8;string;c-string",
            "24;1562550000007;d;8;json;{\"a\":\"13\",\"b\":98,\"c\":20,\"d\":\"white\"}",
            "25;1562550000010;e;8;json;{\"a\":\"16\",\"c\":25,\"e\":\"false\"}"};

    @Test
    public void testEmptyRule() throws SQLException {
        SwiftMetaData baseMetadata = getFirstMetadata();
        SwiftMutableResultSet swiftMutableResultSet = new SwiftMutableResultSet(baseMetadata, new MutableTestResultSet(baseMetadata, lines1));
        List<Row> rowList = new ArrayList<>();

        Assert.assertEquals(swiftMutableResultSet.getMetaData().getColumnCount(), 6);

        while (swiftMutableResultSet.hasNext()) {
            Row row = swiftMutableResultSet.getNextRow();
            rowList.add(row);
        }
        Assert.assertEquals(swiftMutableResultSet.getFetchSize(), 0);
        Assert.assertEquals(swiftMutableResultSet.getMetaData().getColumnCount(), 6);
        Assert.assertEquals(rowList.get(0).getSize(), 6);
        Assert.assertEquals(rowList.get(1).getSize(), 6);
        Assert.assertEquals(rowList.get(2).getSize(), 6);
        Assert.assertEquals(rowList.get(3).getSize(), 6);
        Assert.assertEquals(rowList.get(4).getSize(), 6);
    }

    /**
     * 首次导入，初始6个字段，需要实时拓展到11个字段。
     *
     * @throws SQLException
     */
    @Test
    public void integrationFirstTest() throws SQLException {
        SwiftMetaData baseMetadata = getFirstMetadata();
        SwiftMutableResultSet swiftMutableResultSet = new SwiftMutableResultSet(baseMetadata, new MutableTestResultSet(baseMetadata, lines1), new JsonColumnSplitRule("body", baseMetadata));
        List<Row> rowList = new ArrayList<>();

        Assert.assertEquals(swiftMutableResultSet.getMetaData().getColumnCount(), 6);

        int addFieldTimes = 0;
        while (swiftMutableResultSet.hasNext()) {
            Row row = swiftMutableResultSet.getNextRow();
            if (swiftMutableResultSet.hasNewSubfields()) {
                addFieldTimes++;
            }
            rowList.add(row);
        }
        Assert.assertEquals(addFieldTimes, 3);
        swiftMutableResultSet.close();
        Assert.assertEquals(swiftMutableResultSet.getMetaData().getColumnCount(), 11);
        Assert.assertEquals(rowList.get(0).getSize(), 6);

        Assert.assertEquals(rowList.get(1).getSize(), 9);
        Assert.assertEquals(rowList.get(1).getValue(6), 18.0);
        Assert.assertEquals(rowList.get(1).getValue(7), "1");
        Assert.assertEquals(rowList.get(1).getValue(8), 100.0);

        Assert.assertEquals(rowList.get(2).getSize(), 9);
        Assert.assertNull(rowList.get(2).getValue(6));
        Assert.assertNull(rowList.get(2).getValue(7));
        Assert.assertNull(rowList.get(2).getValue(8));

        Assert.assertEquals(rowList.get(3).getSize(), 10);
        Assert.assertEquals(rowList.get(3).getValue(6), 20.0);
        Assert.assertEquals(rowList.get(3).getValue(7), "3");
        Assert.assertEquals(rowList.get(3).getValue(8), 98.0);
        Assert.assertEquals(rowList.get(3).getValue(9), "black");

        Assert.assertEquals(rowList.get(4).getSize(), 11);
        Assert.assertEquals(rowList.get(4).getValue(6), 15.0);
        Assert.assertEquals(rowList.get(4).getValue(7), "5");
        Assert.assertNull(rowList.get(4).getValue(8));
        Assert.assertNull(rowList.get(4).getValue(9));
        Assert.assertEquals(rowList.get(4).getValue(10), "true");
    }

    /**
     * 第二次导入，初始就是11个字段，且不再需要动态增加字段
     *
     * @throws SQLException
     */
    @Test
    public void integrationSecondTest() throws SQLException {
        SwiftMetaData baseMetadata2 = getSecondMetadata();
        SwiftMutableResultSet swiftMutableResultSet2 = new SwiftMutableResultSet(baseMetadata2, new MutableTestResultSet(baseMetadata2, lines2), new JsonColumnSplitRule("body", baseMetadata2));
        List<Row> rowList2 = new ArrayList<>();

        Assert.assertEquals(swiftMutableResultSet2.getMetaData().getColumnCount(), 11);
        int addFieldTimes = 0;
        while (swiftMutableResultSet2.hasNext()) {
            Row row = swiftMutableResultSet2.getNextRow();
            if (swiftMutableResultSet2.hasNewSubfields()) {
                addFieldTimes++;
            }
            rowList2.add(row);
        }
        Assert.assertEquals(addFieldTimes, 3);
        swiftMutableResultSet2.close();
        Assert.assertEquals(swiftMutableResultSet2.getMetaData().getColumnCount(), 11);

        Assert.assertEquals(rowList2.get(0).getSize(), 11);
        Assert.assertNull(rowList2.get(0).getValue(6));
        Assert.assertNull(rowList2.get(0).getValue(7));
        Assert.assertNull(rowList2.get(0).getValue(8));
        Assert.assertNull(rowList2.get(0).getValue(9));
        Assert.assertNull(rowList2.get(0).getValue(10));

        Assert.assertEquals(rowList2.get(1).getSize(), 11);
        Assert.assertEquals(rowList2.get(1).getValue(6), "12");
        Assert.assertEquals(rowList2.get(1).getValue(7), 28.0);
        Assert.assertEquals(rowList2.get(1).getValue(8), 99.0);
        Assert.assertNull(rowList2.get(1).getValue(9));
        Assert.assertNull(rowList2.get(1).getValue(10));

        Assert.assertEquals(rowList2.get(2).getSize(), 11);
        Assert.assertNull(rowList2.get(2).getValue(6));
        Assert.assertNull(rowList2.get(2).getValue(7));
        Assert.assertNull(rowList2.get(2).getValue(8));
        Assert.assertNull(rowList2.get(2).getValue(9));
        Assert.assertNull(rowList2.get(2).getValue(10));

        Assert.assertEquals(rowList2.get(3).getSize(), 11);
        Assert.assertEquals(rowList2.get(3).getValue(6), "13");
        Assert.assertEquals(rowList2.get(3).getValue(7), 20.0);
        Assert.assertEquals(rowList2.get(3).getValue(8), 98.0);
        Assert.assertNull(rowList2.get(3).getValue(9));
        Assert.assertEquals(rowList2.get(3).getValue(10), "black");

        Assert.assertEquals(rowList2.get(4).getSize(), 11);
        Assert.assertEquals(rowList2.get(4).getValue(6), "16");
        Assert.assertEquals(rowList2.get(4).getValue(7), 25.0);
        Assert.assertNull(rowList2.get(4).getValue(8));
        Assert.assertEquals(rowList2.get(4).getValue(9), "false");
        Assert.assertNull(rowList2.get(4).getValue(10));
    }

    /**
     * 第三次导入，再第二次11个字段基础上，再新增5个子字段。
     *
     * @throws SQLException
     */
    @Test
    public void integrationThirdTest() throws SQLException {
        SwiftMetaData baseMetadata3 = getSecondMetadata();
        SwiftMutableResultSet swiftMutableResultSet3 = new SwiftMutableResultSet(baseMetadata3, new MutableTestResultSet(baseMetadata3, lines3), new JsonColumnSplitRule("body", baseMetadata3));
        List<Row> rowList2 = new ArrayList<>();

        Assert.assertEquals(swiftMutableResultSet3.getMetaData().getColumnCount(), 11);
        int addFieldTimes = 0;
        while (swiftMutableResultSet3.hasNext()) {
            Row row = swiftMutableResultSet3.getNextRow();
            if (swiftMutableResultSet3.hasNewSubfields()) {
                addFieldTimes++;
            }
            rowList2.add(row);
        }
        Assert.assertEquals(addFieldTimes, 3);

        //6-10字段全默认全null
        Assert.assertEquals(swiftMutableResultSet3.getMetaData().getColumnCount(), 16);
        for (Row row : rowList2) {
            for (int i = 6; i <= 10; i++) {
                Assert.assertNull(row.getValue(i));
            }
        }

        Assert.assertEquals(rowList2.get(0).getSize(), 11);

        Assert.assertEquals(rowList2.get(1).getSize(), 14);
        Assert.assertEquals(rowList2.get(1).getValue(11), 99.0);
        Assert.assertEquals(rowList2.get(1).getValue(12), 28.0);
        Assert.assertEquals(rowList2.get(1).getValue(13), "12");

        Assert.assertEquals(rowList2.get(2).getSize(), 14);
        Assert.assertNull(rowList2.get(2).getValue(11));
        Assert.assertNull(rowList2.get(2).getValue(12));
        Assert.assertNull(rowList2.get(2).getValue(13));

        Assert.assertEquals(rowList2.get(3).getSize(), 15);
        Assert.assertEquals(rowList2.get(3).getValue(11), 98.0);
        Assert.assertEquals(rowList2.get(3).getValue(12), 20.0);
        Assert.assertEquals(rowList2.get(3).getValue(13), "13");
        Assert.assertEquals(rowList2.get(3).getValue(14), "white");

        Assert.assertEquals(rowList2.get(4).getSize(), 16);
        Assert.assertNull(rowList2.get(4).getValue(11));
        Assert.assertEquals(rowList2.get(4).getValue(12), 25.0);
        Assert.assertEquals(rowList2.get(4).getValue(13), "16");
        Assert.assertNull(rowList2.get(4).getValue(14));
        Assert.assertEquals(rowList2.get(4).getValue(15), "false");
    }


    private static SwiftMetaDataColumn id = new MetaDataColumnBean("id", Types.VARCHAR);
    private static SwiftMetaDataColumn time = new MetaDataColumnBean("time", Types.DATE);
    private static SwiftMetaDataColumn username = new MetaDataColumnBean("username", Types.VARCHAR);
    private static SwiftMetaDataColumn source = new MetaDataColumnBean("source", Types.VARCHAR);
    private static SwiftMetaDataColumn text = new MetaDataColumnBean("text", Types.VARCHAR);
    private static SwiftMetaDataColumn body = new MetaDataColumnBean("body", Types.VARCHAR);

    private static SwiftMetaDataColumn bodyId = new MetaDataColumnBean("body.id", Types.VARCHAR);
    private static SwiftMetaDataColumn bodyScore = new MetaDataColumnBean("body.score", Types.DOUBLE);
    private static SwiftMetaDataColumn bodyAge = new MetaDataColumnBean("body.age", Types.DOUBLE);
    private static SwiftMetaDataColumn bodyType = new MetaDataColumnBean("body.type", Types.VARCHAR);
    private static SwiftMetaDataColumn bodyHead = new MetaDataColumnBean("body.head", Types.VARCHAR);


    private static SwiftMetaDataColumn a = new MetaDataColumnBean("body.a", Types.VARCHAR);
    private static SwiftMetaDataColumn b = new MetaDataColumnBean("body.b", Types.DOUBLE);
    private static SwiftMetaDataColumn c = new MetaDataColumnBean("body.c", Types.DOUBLE);
    private static SwiftMetaDataColumn d = new MetaDataColumnBean("body.d", Types.VARCHAR);
    private static SwiftMetaDataColumn e = new MetaDataColumnBean("body.e", Types.VARCHAR);


    public static SwiftMetaData getFirstMetadata() {
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        columnList.addAll(Arrays.asList(id, time, username, source, text, body));
        return new SwiftMetaDataBean("test", null, "test", null, columnList);
    }

    public static SwiftMetaData getSecondMetadata() {
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        columnList.addAll(Arrays.asList(id, time, username, source, text, body
                , bodyId, bodyAge, bodyScore, bodyType, bodyHead));
        return new SwiftMetaDataBean("test", null, "test", null, columnList);
    }

    public static SwiftMetaData getThirdMetadata() {
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        columnList.addAll(Arrays.asList(id, time, username, source, text, body
                , bodyId, bodyAge, bodyScore, bodyType, bodyHead
                , a, b, c, d, e));
        return new SwiftMetaDataBean("test", null, "test", null, columnList);
    }

    public static class MutableTestResultSet implements SwiftResultSet {
        @Override
        public int getFetchSize() {
            return 0;
        }

        SwiftMetaData swiftMetaData;
        String[] lines;

        int index = 0;

        public MutableTestResultSet(SwiftMetaData swiftMetaData, String[] lines) {
            this.swiftMetaData = swiftMetaData;
            this.lines = lines;
        }

        @Override
        public SwiftMetaData getMetaData() throws SQLException {
            return swiftMetaData;
        }

        @Override
        public boolean hasNext() throws SQLException {
            return lines.length > index;
        }

        @Override
        public Row getNextRow() throws SQLException {
            String line = lines[index++];
            MutableRow row = new ListMutableRow(new ArrayList<Object>(Arrays.asList(line.split(";"))));
            while (row.getSize() < swiftMetaData.getColumnCount()) {
                row.addElement(null);
            }
            return row;
        }

        @Override
        public void close() throws SQLException {

        }
    }
}