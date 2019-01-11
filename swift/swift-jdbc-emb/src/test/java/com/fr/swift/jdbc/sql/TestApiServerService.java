package com.fr.swift.jdbc.sql;

import com.fr.swift.annotation.SwiftApi;
import com.fr.swift.api.result.OnePageApiResultSet;
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
        OnePageApiResultSet mockOnePageApiResultSet = PowerMock.createMock(OnePageApiResultSet.class);
        EasyMock.expect(mockOnePageApiResultSet.isOriginHasNextPage()).andReturn(false).anyTimes();
        EasyMock.expect(mockOnePageApiResultSet.getRows()).andReturn(Collections.emptyList()).anyTimes();
        EasyMock.expect(mockOnePageApiResultSet.getRowCount()).andReturn(0).anyTimes();
        try {
            EasyMock.expect(mockOnePageApiResultSet.getMetaData()).andReturn(null).anyTimes();
            EasyMock.expect(mockOnePageApiResultSet.getLabel2Index()).andReturn(Collections.emptyMap()).anyTimes();
        } catch (SQLException ignore) {
        }
        PowerMock.replay(mockOnePageApiResultSet);
        return new ApiResponseImpl(mockOnePageApiResultSet);
    }
}
