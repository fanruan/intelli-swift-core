package com.fr.swift.server;

import com.fr.swift.api.info.ApiInvocation;
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

    public void testVisitSqlRequestInfo() throws Exception {
        SqlRequestInfo sqlRequestInfo = new SqlRequestInfo("select * from a");
        sqlRequestInfo.setDatabase("cube");
        // TODO: 2018/12/10 @Lyon
        ApiInvocation invocation = sqlRequestInfo.accept(new SwiftRequestParserVisitor());
        assertTrue(false);
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
        assertEquals(methodName, "detectiveAllTableNames");
        assertNotNull(method);

    }

}
