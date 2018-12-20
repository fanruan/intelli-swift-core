package com.fr.swift.server;

import com.fr.swift.api.info.ApiInvocation;
import com.fr.swift.api.rpc.DataMaintenanceService;
import com.fr.swift.api.rpc.SelectService;
import com.fr.swift.api.rpc.TableService;
import com.fr.swift.api.server.SwiftRequestParserVisitor;
import com.fr.swift.jdbc.info.ColumnsRequestInfo;
import com.fr.swift.jdbc.info.SqlRequestInfo;
import com.fr.swift.jdbc.info.TablesRequestInfo;
import junit.framework.TestCase;

import java.lang.reflect.Method;

/**
 * This class created on 2018/12/10
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftRequestParserVisitorTest extends TestCase {

    public void testVisitSelectSqlRequestInfo() throws Exception {
        SqlRequestInfo select = new SqlRequestInfo("select * from cube.a", true);
        ApiInvocation invocation = select.accept(new SwiftRequestParserVisitor());
        Class<?> aClass = invocation.getTarget();
        Class<?>[] parameterTypes = invocation.getParameterTypes();
        Object[] arguments = invocation.getArguments();
        String methodName = invocation.getMethodName();
        Method method = aClass.getMethod(methodName, parameterTypes);

        assertEquals(aClass, SelectService.class);
        assertEquals(parameterTypes.length, 2);
        assertEquals(arguments.length, 2);
        assertEquals(methodName, "query");
        assertNotNull(method);
    }

    public void testVisitCreateSqlRequestInfo() throws Exception {
        SqlRequestInfo create = new SqlRequestInfo("create table cube.tbl_name (a int, b bigint, c double, d timestamp, e date, f varchar)", false);
        ApiInvocation invocation = create.accept(new SwiftRequestParserVisitor());
        Class<?> aClass = invocation.getTarget();
        Class<?>[] parameterTypes = invocation.getParameterTypes();
        Object[] arguments = invocation.getArguments();
        String methodName = invocation.getMethodName();
        Method method = aClass.getMethod(methodName, parameterTypes);

        assertEquals(aClass, TableService.class);
        assertEquals(parameterTypes.length, 3);
        assertEquals(arguments.length, 3);
        assertEquals(methodName, "createTable");
        assertNotNull(method);
    }

    public void testVisitInsertSqlRequestInfo() throws Exception {
        SqlRequestInfo insert = new SqlRequestInfo("insert into cube.tbl_name (a, b, c) values ('a', 'b', 233), ('a1', 'b1', 234)", false);
        ApiInvocation invocation = insert.accept(new SwiftRequestParserVisitor());
        Class<?> aClass = invocation.getTarget();
        Class<?>[] parameterTypes = invocation.getParameterTypes();
        Object[] arguments = invocation.getArguments();
        String methodName = invocation.getMethodName();
        Method method = aClass.getMethod(methodName, parameterTypes);

        assertEquals(aClass, DataMaintenanceService.class);
        assertEquals(parameterTypes.length, 4);
        assertEquals(arguments.length, 4);
        assertEquals(methodName, "insert");
        assertNotNull(method);
    }

    public void testVisitDeleteSqlRequestInfo() throws Exception {
        SqlRequestInfo delete = new SqlRequestInfo("delete from cube.tbl_name where a = 233", false);
        ApiInvocation invocation = delete.accept(new SwiftRequestParserVisitor());
        Class<?> aClass = invocation.getTarget();
        Class<?>[] parameterTypes = invocation.getParameterTypes();
        Object[] arguments = invocation.getArguments();
        String methodName = invocation.getMethodName();
        Method method = aClass.getMethod(methodName, parameterTypes);

        assertEquals(aClass, DataMaintenanceService.class);
        assertEquals(parameterTypes.length, 3);
        assertEquals(arguments.length, 3);
        assertEquals(methodName, "delete");
        assertNotNull(method);
    }

    public void testVisitDropSqlRequestInfo() throws Exception {
        SqlRequestInfo drop = new SqlRequestInfo("drop table cube.tbl_name", false);
        ApiInvocation invocation = drop.accept(new SwiftRequestParserVisitor());
        Class<?> aClass = invocation.getTarget();
        Class<?>[] parameterTypes = invocation.getParameterTypes();
        Object[] arguments = invocation.getArguments();
        String methodName = invocation.getMethodName();
        Method method = aClass.getMethod(methodName, parameterTypes);

        assertEquals(aClass, TableService.class);
        assertEquals(parameterTypes.length, 2);
        assertEquals(arguments.length, 2);
        assertEquals(methodName, "dropTable");
        assertNotNull(method);
    }

    public void testVisitColumnsRequestInfo() throws Exception {
        ColumnsRequestInfo columnsRequestInfo = new ColumnsRequestInfo("cube", "a", "authCode");
        ApiInvocation invocation = columnsRequestInfo.accept(new SwiftRequestParserVisitor());

        Class<?> aClass = invocation.getTarget();
        Class<?>[] parameterTypes = invocation.getParameterTypes();
        Object[] arguments = invocation.getArguments();
        String methodName = invocation.getMethodName();
        Method method = aClass.getMethod(methodName, parameterTypes);

        assertEquals(aClass, TableService.class);
        assertEquals(parameterTypes.length, 2);
        assertEquals(arguments.length, 2);
        assertEquals(methodName, "detectiveMetaData");
        assertNotNull(method);
    }

    public void testVisitTablesRequestInfo() throws Exception {
        TablesRequestInfo tablesRequestInfo = new TablesRequestInfo("cube", "authCode");
        ApiInvocation invocation = tablesRequestInfo.accept(new SwiftRequestParserVisitor());

        Class<?> aClass = invocation.getTarget();
        Class<?>[] parameterTypes = invocation.getParameterTypes();
        Object[] arguments = invocation.getArguments();
        String methodName = invocation.getMethodName();
        Method method = aClass.getMethod(methodName, parameterTypes);

        assertEquals(aClass, TableService.class);
        assertEquals(parameterTypes.length, 1);
        assertEquals(arguments.length, 1);
        assertEquals(methodName, "detectiveAllTable");
        assertNotNull(method);

    }

}
