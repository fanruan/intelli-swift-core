package com.fr.swift.jdbc.sql;

import com.fr.swift.annotation.SwiftApi;
import com.fr.swift.api.result.BaseApiResultSet;
import com.fr.swift.api.server.ApiServerService;
import com.fr.swift.api.server.response.ApiResponse;
import com.fr.swift.api.server.response.ApiResponseImpl;
import org.easymock.EasyMock;
import org.powermock.api.easymock.PowerMock;

import java.sql.SQLException;
import java.util.Collections;

/**
 * @author yee
 * @date 2019-01-09
 */
@SwiftApi
public class TestApiServerService implements ApiServerService {
    @Override
    @SwiftApi
    public ApiResponse dispatchRequest(String request) {
        // Generate by Mock Plugin
        BaseApiResultSet mockBaseApiResultSet = PowerMock.createMock(BaseApiResultSet.class);
        EasyMock.expect(mockBaseApiResultSet.isOriginHasNextPage()).andReturn(false).anyTimes();
        EasyMock.expect(mockBaseApiResultSet.getRows()).andReturn(Collections.emptyList()).anyTimes();
        EasyMock.expect(mockBaseApiResultSet.getRowCount()).andReturn(0).anyTimes();
        try {
            EasyMock.expect(mockBaseApiResultSet.getMetaData()).andReturn(null).anyTimes();
            EasyMock.expect(mockBaseApiResultSet.getLabel2Index()).andReturn(Collections.emptyMap()).anyTimes();
        } catch (SQLException ignore) {
        }
        PowerMock.replay(mockBaseApiResultSet);
        return new ApiResponseImpl(mockBaseApiResultSet);
    }
}
