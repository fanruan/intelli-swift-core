package com.fr.swift.api.server;

import com.fr.swift.api.info.ApiInvocation;
import com.fr.swift.api.rpc.SelectService;
import com.fr.swift.api.rpc.TableService;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.jdbc.info.ColumnsRequestInfo;
import com.fr.swift.jdbc.info.RequestParserVisitor;
import com.fr.swift.jdbc.info.SqlRequestInfo;
import com.fr.swift.jdbc.info.TablesRequestInfo;

/**
 * This class created on 2018/12/10
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftRequestParserVisitor implements RequestParserVisitor {

    @Override
    public ApiInvocation visit(SqlRequestInfo sqlRequestInfo) {
        String database = sqlRequestInfo.getDatabase();
        String sql = sqlRequestInfo.getSql();
        //todo sql解析 @Lyon
        SwiftDatabase swiftDatabase = SwiftDatabase.fromKey(database);
        String queryJson = null;
        return createApiInvocation("query", SelectService.class, swiftDatabase, queryJson);
    }

    @Override
    public ApiInvocation visit(ColumnsRequestInfo columnsRequestInfo) {
        String database = columnsRequestInfo.getDatabase();
        String table = columnsRequestInfo.getTable();
        SwiftDatabase swiftDatabase = SwiftDatabase.fromKey(database);
        return createApiInvocation("detectiveMetaData", TableService.class, swiftDatabase, table);
    }

    @Override
    public ApiInvocation visit(TablesRequestInfo tablesRequestInfo) {
        String database = tablesRequestInfo.getDatabase();
        SwiftDatabase swiftDatabase = SwiftDatabase.fromKey(database);
        return createApiInvocation("detectiveAllTableNames", TableService.class, swiftDatabase);
    }

    private ApiInvocation createApiInvocation(String method, Class<?> clazz, Object... arguments) {
        Class<?>[] parameterTypes = new Class[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            parameterTypes[i] = arguments[i].getClass();
        }
        ApiInvocation invocation = new ApiInvocation(method, clazz, parameterTypes, arguments);
        return invocation;
    }
}
