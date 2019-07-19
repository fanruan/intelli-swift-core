package com.fr.swift.api.server;

import com.fr.swift.annotation.SwiftApi;
import com.fr.swift.api.info.ApiInvocation;
import com.fr.swift.api.info.RequestInfo;
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

import java.io.Serializable;
import java.lang.reflect.Method;

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

    @Override
    @SwiftApi
    public ApiResponse dispatchRequest(String request) {
        ApiResponse response = new ApiResponseImpl();
        try {
            RequestInfo requestInfo = JsonBuilder.readValue(request, RequestInfo.class);
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

    private Object invokeRequest(ApiInvocation invocation) {
        Class<?> aClass = invocation.getTarget();
        Class<?>[] parameterTypes = invocation.getParameterTypes();
        Object[] arguments = invocation.getArguments();
        String methodName = invocation.getMethodName();
        try {
            Method method = aClass.getMethod(methodName, parameterTypes);
            return method.invoke(ProxyServiceRegistry.get().getExternalService(aClass), arguments);
        } catch (Exception e) {
            return ApiCrasher.crash(ServerErrorCode.SERVER_INVOKE_ERROR, e);
        }
    }
}
