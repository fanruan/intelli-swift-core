package com.fr.swift.api;

import com.fr.swift.api.rpc.SimpleDetailQueryBean;
import com.fr.swift.api.rpc.invoke.ApiProxyFactory;
import com.fr.swift.api.rpc.session.impl.SwiftApiSessionFactoryImpl;
import com.fr.swift.db.Schema;
import com.fr.swift.netty.rpc.exception.ServiceInvalidException;
import com.fr.swift.service.cluster.ClusterAnalyseService;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftResultSet;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.Serializable;
import java.lang.reflect.UndeclaredThrowableException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author yee
 * @date 2018/8/24
 */
public class ApiTest implements Serializable {

    private static List<Row> datas;
    private static SwiftApiSessionFactoryImpl factory;

    @BeforeClass
    public static void setUp() {
        factory = new SwiftApiSessionFactoryImpl("192.168.0.7:7000");
        datas = new ArrayList<Row>();
        for (int i = 0; i < 100; i++) {
            datas.add(new ListBasedRow(Arrays.<Object>asList(i + 90L, "zorgname_t" + i)));
        }
    }

    @Test
    @Ignore
    public void insert() throws SQLException {
        assertEquals(factory.openSession().insert(Schema.CUBE, "test_table", Arrays.asList("id", "name"), datas), datas.size());
    }

    @Test
    public void insert1() throws SQLException {
        assertEquals(factory.openSession().insert(Schema.CUBE, "test_table", datas), datas.size());
    }

    @Test
    @Ignore
    public void query() throws SQLException {
//        aa4f69b2
        SimpleDetailQueryBean bean = new SimpleDetailQueryBean();
        bean.setTable("e88238c2");
        bean.setColumns(Arrays.asList("id", "name"));
        SwiftResultSet resultSet = factory.openSession().query(bean.getQueryString());
        while (resultSet.hasNext()) {
            System.out.println(resultSet.getNextRow().getValue(0));
        }
        resultSet.close();
    }

    @Test
    @Ignore
    public void testException() {
        ClusterAnalyseService service = ApiProxyFactory.getProxy(ClusterAnalyseService.class, "192.168.0.7:7000");
        try {
            SimpleDetailQueryBean bean = new SimpleDetailQueryBean();
            bean.setTable("e88238c2");
            bean.setColumns(Arrays.asList("id", "name"));
            service.getRemoteQueryResult(bean.getQueryString(), null);
        } catch (UndeclaredThrowableException e) {
            assertTrue(e.getCause() instanceof ServiceInvalidException);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

}