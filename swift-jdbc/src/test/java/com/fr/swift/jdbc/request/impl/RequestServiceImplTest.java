package com.fr.swift.jdbc.request.impl;

import com.fr.swift.api.server.response.ApiResponse;
import com.fr.swift.jdbc.info.SqlRequestInfo;
import com.fr.swift.jdbc.rpc.JdbcExecutor;
import com.fr.swift.result.SerializableResultSet;
import com.fr.swift.rpc.bean.RpcResponse;
import com.fr.swift.rpc.bean.impl.RpcRequest;
import org.easymock.EasyMock;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

/**
 * @author yee
 * @date 2018-12-24
 */
public class RequestServiceImplTest {

    @Test
    public void apply() {
        // Generate by Mock Plugin
        JdbcExecutor mockJdbcExecutor = PowerMock.createMock(JdbcExecutor.class);
        ApiResponse mockApiResponse = PowerMock.createMock(ApiResponse.class);
        EasyMock.expect(mockApiResponse.isError()).andReturn(false).anyTimes();
        RpcResponse mockRpcResponse = PowerMock.createMock(RpcResponse.class);
        EasyMock.expect(mockRpcResponse.getResult()).andReturn(mockApiResponse).anyTimes();
        EasyMock.expect(mockJdbcExecutor.send(EasyMock.notNull(RpcRequest.class))).andReturn(mockRpcResponse).anyTimes();
        PowerMock.replayAll();
        ApiResponse response = new RequestServiceImpl().apply(mockJdbcExecutor, "user", "pass");
        assertFalse(response.isError());
        PowerMock.verifyAll();
    }

    @Test
    public void apply1() {
        // Generate by Mock Plugin
        JdbcExecutor mockJdbcExecutor = PowerMock.createMock(JdbcExecutor.class);
        ApiResponse mockApiResponse = PowerMock.createMock(ApiResponse.class);
        SerializableResultSet mockSerializableResultSet = PowerMock.createMock(SerializableResultSet.class);
        EasyMock.expect(mockApiResponse.isError()).andReturn(false).anyTimes();
        EasyMock.expect(mockApiResponse.result()).andReturn(mockSerializableResultSet);
        RpcResponse mockRpcResponse = PowerMock.createMock(RpcResponse.class);
        EasyMock.expect(mockRpcResponse.getResult()).andReturn(mockApiResponse).anyTimes();
        EasyMock.expect(mockJdbcExecutor.send(EasyMock.notNull(RpcRequest.class))).andReturn(mockRpcResponse).anyTimes();
        PowerMock.replayAll();
        ApiResponse response = new RequestServiceImpl().apply(mockJdbcExecutor, new SqlRequestInfo("select * from tableA", true));
        assertFalse(response.isError());
        assertTrue(response.result() instanceof SerializableResultSet);
        PowerMock.verifyAll();
    }

    @Test
    public void apply2() {
        String jsonString = "{\"database\":cube,\"requestType\":\"SQL\",\"auth\":\"authCode\",\"requestId\":\"93eceaf1-4e47-40a1-84e9-814404eb7ad5\",\"sql\":\"select * from tableA\"}";
        JdbcExecutor mockJdbcExecutor = PowerMock.createMock(JdbcExecutor.class);
        ApiResponse mockApiResponse = PowerMock.createMock(ApiResponse.class);
        EasyMock.expect(mockApiResponse.isError()).andReturn(false).anyTimes();
        RpcResponse mockRpcResponse = PowerMock.createMock(RpcResponse.class);
        EasyMock.expect(mockRpcResponse.getResult()).andReturn(mockApiResponse).anyTimes();
        EasyMock.expect(mockJdbcExecutor.send(EasyMock.notNull(RpcRequest.class))).andReturn(mockRpcResponse).anyTimes();
        PowerMock.replayAll();
        ApiResponse response = new RequestServiceImpl().apply(mockJdbcExecutor, jsonString);
        assertFalse(response.isError());
        PowerMock.verifyAll();
    }

    @Test
    public void applyWithRetry() {
        // Generate by Mock Plugin
        JdbcExecutor mockJdbcExecutor = PowerMock.createMock(JdbcExecutor.class);
        ApiResponse mockApiResponse = PowerMock.createMock(ApiResponse.class);
        EasyMock.expect(mockApiResponse.isError()).andReturn(true).times(2);
        EasyMock.expect(mockApiResponse.isError()).andReturn(false).once();
        RpcResponse mockRpcResponse = PowerMock.createMock(RpcResponse.class);
        EasyMock.expect(mockRpcResponse.getResult()).andReturn(mockApiResponse).times(3);
        EasyMock.expect(mockJdbcExecutor.send(EasyMock.notNull(RpcRequest.class))).andReturn(mockRpcResponse).times(3);
        PowerMock.replayAll();
        new RequestServiceImpl().applyWithRetry(mockJdbcExecutor, "user", "pass", 3);
        PowerMock.verifyAll();
    }

    @Test
    public void applyWithRetry1() {
        // Generate by Mock Plugin
        JdbcExecutor mockJdbcExecutor = PowerMock.createMock(JdbcExecutor.class);
        ApiResponse mockApiResponse = PowerMock.createMock(ApiResponse.class);
        SerializableResultSet mockSerializableResultSet = PowerMock.createMock(SerializableResultSet.class);
        EasyMock.expect(mockApiResponse.isError()).andReturn(true).times(2);
        EasyMock.expect(mockApiResponse.isError()).andReturn(false).once();
        EasyMock.expect(mockApiResponse.result()).andReturn(mockSerializableResultSet);
        RpcResponse mockRpcResponse = PowerMock.createMock(RpcResponse.class);
        EasyMock.expect(mockRpcResponse.getResult()).andReturn(mockApiResponse).times(3);
        EasyMock.expect(mockJdbcExecutor.send(EasyMock.notNull(RpcRequest.class))).andReturn(mockRpcResponse).times(3);
        PowerMock.replayAll();
        ApiResponse response = new RequestServiceImpl().applyWithRetry(mockJdbcExecutor, new SqlRequestInfo("select * from tableA", true), 3);
        assertTrue(response.result() instanceof SerializableResultSet);
        PowerMock.verifyAll();
    }

    @Test
    public void applyWithRetry2() {
        String jsonString = "{\"database\":cube,\"requestType\":\"SQL\",\"auth\":\"authCode\",\"requestId\":\"93eceaf1-4e47-40a1-84e9-814404eb7ad5\",\"sql\":\"select * from tableA\"}";
        JdbcExecutor mockJdbcExecutor = PowerMock.createMock(JdbcExecutor.class);
        ApiResponse mockApiResponse = PowerMock.createMock(ApiResponse.class);
        EasyMock.expect(mockApiResponse.isError()).andReturn(true).times(2);
        EasyMock.expect(mockApiResponse.isError()).andReturn(false).once();
        RpcResponse mockRpcResponse = PowerMock.createMock(RpcResponse.class);
        EasyMock.expect(mockRpcResponse.getResult()).andReturn(mockApiResponse).times(3);
        EasyMock.expect(mockJdbcExecutor.send(EasyMock.notNull(RpcRequest.class))).andReturn(mockRpcResponse).times(3);
        PowerMock.replayAll();
        new RequestServiceImpl().applyWithRetry(mockJdbcExecutor, jsonString, 3);
        PowerMock.verifyAll();
    }
}