package com.fr.swift.api.server;

import com.fr.swift.api.info.ApiInvocation;
import com.fr.swift.api.info.ApiRequestParserVisitor;
import com.fr.swift.api.info.ApiRequestType;
import com.fr.swift.api.info.AuthRequestInfo;
import com.fr.swift.api.info.CreateTableRequestInfo;
import com.fr.swift.api.info.DeleteTableRequestInfo;
import com.fr.swift.api.info.InsertRequestInfo;
import com.fr.swift.api.info.QueryRequestInfo;
import com.fr.swift.api.info.TableRequestInfo;
import com.fr.swift.api.rpc.DataMaintenanceService;
import com.fr.swift.api.rpc.DetectService;
import com.fr.swift.api.rpc.SelectService;
import com.fr.swift.api.rpc.TableService;
import com.fr.swift.api.server.exception.ApiCrasher;
import com.fr.swift.api.server.response.error.ParamErrorCode;
import com.fr.swift.base.json.JsonBuilder;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.db.impl.SwiftWhere;
import com.fr.swift.jdbc.info.ColumnsRequestInfo;
import com.fr.swift.jdbc.info.JdbcRequestParserVisitor;
import com.fr.swift.jdbc.info.SqlRequestInfo;
import com.fr.swift.jdbc.info.TablesRequestInfo;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;

/**
 * This class created on 2018/12/10
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftRequestParserVisitor implements JdbcRequestParserVisitor, ApiRequestParserVisitor {

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

    @Override
    public ApiInvocation visit(AuthRequestInfo authRequestInfo) {
        return createApiInvocation("detectiveAnalyseAndRealTime", DetectService.class, authRequestInfo.getFrom());
    }

    @Override
    public ApiInvocation visit(QueryRequestInfo queryRequestInfo) {
        return createApiInvocation("query", SelectService.class, queryRequestInfo.getDatabase(), queryRequestInfo.getQueryJson());
    }

    @Override
    public ApiInvocation visit(CreateTableRequestInfo createTableRequestInfo) {
        return createApiInvocation("createTable", TableService.class, createTableRequestInfo.getDatabase(),
                createTableRequestInfo.getTable(), createTableRequestInfo.getColumns());
    }

    @Override
    public ApiInvocation visit(DeleteTableRequestInfo deleteTableRequestInfo) {
        try {
            String filter = deleteTableRequestInfo.getWhere();
            SwiftWhere where = new SwiftWhere(JsonBuilder.readValue(filter, FilterInfoBean.class));
            return createApiInvocation("delete", DataMaintenanceService.class, deleteTableRequestInfo.getDatabase(), deleteTableRequestInfo.getTable(), where);
        } catch (Exception e) {
            return ApiCrasher.crash(ParamErrorCode.PARAMS_PARSER_ERROR);
        }
    }

    @Override
    public ApiInvocation visit(InsertRequestInfo insertRequestInfo) {
        try {
            return createApiInvocation("insert", DataMaintenanceService.class,
                    insertRequestInfo.getDatabase(),
                    insertRequestInfo.getTable(),
                    insertRequestInfo.getSelectFields(),
                    insertRequestInfo.getData());
        } catch (Exception e) {
            return ApiCrasher.crash(ParamErrorCode.PARAMS_PARSER_ERROR);
        }
    }

    @Override
    public ApiInvocation visit(TableRequestInfo tableRequestInfo) {
        ApiRequestType type = tableRequestInfo.getRequest();
        switch (type) {
            case DROP_TABLE:
                return createApiInvocation("dropTable", TableService.class, tableRequestInfo.getDatabase(), tableRequestInfo.getTable());
            case TRUNCATE_TABLE:
                return createApiInvocation("truncateTable", TableService.class, tableRequestInfo.getDatabase(), tableRequestInfo.getTable());
            case DELETE:
                return visit((DeleteTableRequestInfo) tableRequestInfo);
            case INSERT:
                return visit((InsertRequestInfo) tableRequestInfo);
            case CREATE_TABLE:
                return visit((CreateTableRequestInfo) tableRequestInfo);
            default:

        }
        return null;
    }
}
