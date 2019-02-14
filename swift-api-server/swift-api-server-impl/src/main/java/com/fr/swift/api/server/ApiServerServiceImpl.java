package com.fr.swift.api.server;

import com.fr.swift.annotation.SwiftApi;
import com.fr.swift.api.info.ApiInvocation;
import com.fr.swift.api.info.ApiRequestType;
import com.fr.swift.api.info.AuthRequestInfo;
import com.fr.swift.api.info.CreateTableRequestInfo;
import com.fr.swift.api.info.DeleteRequestInfo;
import com.fr.swift.api.info.InsertRequestInfo;
import com.fr.swift.api.info.JdbcRequestType;
import com.fr.swift.api.info.QueryRequestInfo;
import com.fr.swift.api.info.RequestInfo;
import com.fr.swift.api.info.TableRequestInfo;
import com.fr.swift.api.server.exception.ApiCrasher;
import com.fr.swift.api.server.exception.ApiRequestRuntimeException;
import com.fr.swift.api.server.response.ApiResponse;
import com.fr.swift.api.server.response.ApiResponseImpl;
import com.fr.swift.api.server.response.error.ParamErrorCode;
import com.fr.swift.api.server.response.error.ServerErrorCode;
import com.fr.swift.base.json.JsonBuilder;
import com.fr.swift.basics.annotation.ProxyService;
import com.fr.swift.basics.base.ProxyServiceRegistry;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.jdbc.info.ColumnsRequestInfo;
import com.fr.swift.jdbc.info.SqlRequestInfo;
import com.fr.swift.jdbc.info.TablesRequestInfo;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * This class created on 2018/12/10
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@SwiftBean
@ProxyService(value = ApiServerService.class, type = ProxyService.ServiceType.EXTERNAL)
public class ApiServerServiceImpl implements ApiServerService {

    private static final String REQUEST_TYPE = "requestType";
    private static final Map<String, Class<? extends RequestInfo>> requestTypeMap = new HashMap<String, Class<? extends RequestInfo>>();

    {
        requestTypeMap.put(AuthRequestInfo.AUTH.name(), AuthRequestInfo.class);
        requestTypeMap.put(JdbcRequestType.TABLES.name(), TablesRequestInfo.class);
        requestTypeMap.put(JdbcRequestType.COLUMNS.name(), ColumnsRequestInfo.class);
        requestTypeMap.put(JdbcRequestType.SQL.name(), SqlRequestInfo.class);
        requestTypeMap.put(ApiRequestType.JSON_QUERY.name(), QueryRequestInfo.class);
        requestTypeMap.put(ApiRequestType.CREATE_TABLE.name(), CreateTableRequestInfo.class);
        requestTypeMap.put(ApiRequestType.DELETE.name(), DeleteRequestInfo.class);
        requestTypeMap.put(ApiRequestType.INSERT.name(), InsertRequestInfo.class);
        requestTypeMap.put(ApiRequestType.DROP_TABLE.name(), TableRequestInfo.class);
        requestTypeMap.put(ApiRequestType.TRUNCATE_TABLE.name(), TableRequestInfo.class);
    }

    @Override
    @SwiftApi
    public ApiResponse dispatchRequest(String request) {
        ApiResponse response = new ApiResponseImpl();
        try {
            Class<? extends RequestInfo> clazz = parseRequestClass(request);
            RequestInfo requestInfo = JsonBuilder.readValue(request, clazz);
            ApiInvocation invocation = requestInfo.accept(new SwiftRequestParserVisitor());
            Object object = invokeRequest(invocation);
            response.setResult((Serializable) object);
        } catch (ApiRequestRuntimeException re) {
            response.setThrowable(re);
            response.setStatusCode(re.getStatusCode());
        } catch (Exception e) {
            response.setThrowable(e);
            response.setStatusCode(ParamErrorCode.PARAMS_PARSER_ERROR);
        }
        return response;
    }

    private Class<? extends RequestInfo> parseRequestClass(String request) throws Exception {
        Map<String, String> map = JsonBuilder.readValue(request, Map.class);
        if (map.containsKey(REQUEST_TYPE)) {
            String requestType = map.get(REQUEST_TYPE);
            return requestTypeMap.get(requestType);
        } else {
            return ApiCrasher.crash(ParamErrorCode.RESULT_TYPE_ABSENT);
        }
    }

    private Object invokeRequest(ApiInvocation invocation) {
        Class<?> aClass = invocation.getTarget();
        Class<?>[] parameterTypes = invocation.getParameterTypes();
        Object[] arguments = invocation.getArguments();
        String methodName = invocation.getMethodName();
        try {
            Method method = aClass.getMethod(methodName, parameterTypes);
            Object object = method.invoke(ProxyServiceRegistry.get().getExternalService(aClass), arguments);
            return object;
        } catch (Exception e) {
            return ApiCrasher.crash(ServerErrorCode.SERVER_INVOKE_ERROR, e);
        }
    }
}
