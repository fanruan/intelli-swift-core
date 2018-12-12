package com.fr.swift.api;

import com.fr.swift.api.rpc.SimpleDetailQueryBean;
import com.fr.swift.api.rpc.session.impl.SwiftApiSessionFactoryImpl;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author yee
 * @date 2018/8/24
 */
public class ApiTest {

    private static List<Row> datas;
    private static SwiftApiSessionFactoryImpl factory;

    @BeforeClass
    public static void setUp() {
        factory = new SwiftApiSessionFactoryImpl("127.0.0.1:7000");
        datas = new ArrayList<Row>();
        for (int i = 0; i < 100; i++) {
            datas.add(new ListBasedRow(Arrays.<Object>asList(i + 90L, "zorgname_t" + i)));
        }
    }

    @Test
    @Ignore
    public void insert() throws Exception {
        assertEquals(factory.openSession().insert(SwiftDatabase.CUBE, "test_table", Arrays.asList("id", "name"), datas), datas.size());
    }

    @Test
    public void insert1() throws Exception {
        assertEquals(factory.openSession().insert(SwiftDatabase.CUBE, "test_table", datas), datas.size());
    }

    @Test
    @Ignore
    public void query() throws Exception {
//        aa4f69b2
        SimpleDetailQueryBean bean = new SimpleDetailQueryBean();
        bean.setTable("36e09331");
        bean.setColumns(Arrays.asList("0calday", "zsqty"));
        SwiftResultSet resultSet = factory.openSession().query(SwiftDatabase.CUBE, bean.getQueryString());
        while (resultSet.hasNext()) {
            System.out.println(resultSet.getNextRow().getValue(0));
        }
        resultSet.close();
    }

}