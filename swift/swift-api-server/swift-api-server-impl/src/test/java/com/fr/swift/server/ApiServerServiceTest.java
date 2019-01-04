package com.fr.swift.server;

import com.fr.swift.SwiftContext;
import com.fr.swift.api.result.SwiftApiResultSet;
import com.fr.swift.api.rpc.SelectService;
import com.fr.swift.api.rpc.TableService;
import com.fr.swift.api.server.ApiServerServiceImpl;
import com.fr.swift.api.server.response.ApiResponse;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.exception.meta.SwiftMetaDataAbsentException;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018/12/10
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SwiftContext.class})
public class ApiServerServiceTest extends TestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        PowerMock.mockStatic(SwiftContext.class);
        BeanFactory beanFactory = PowerMock.createMock(BeanFactory.class);
        EasyMock.expect(SwiftContext.get()).andReturn(beanFactory).anyTimes();
        PowerMock.replay(SwiftContext.class);
    }

    public void testUser() throws Exception {
        String request = " {\n" +
                "    \"swiftUser\": \"username\",\n" +
                "    \"swiftPassword\": \"password\",\n" +
                "    \"requestType\": \"AUTH\",\n" +
                "    \"requestId\": \"fdfd91e4-e7d5-4473-8d13-79e3e250f553\"\n" +
                "}";
        return;
    }


    public void testSQL() {
        String request = "{\n" +
                "    \"sql\": \"select * from table\",\n" +
                "    \"auth\": \"authCode\",\n" +
                "    \"database\": \"cube\",\n" +
                "    \"requestType\": \"SQL\",\n" +
                "    \"requestId\": \"71859bfa-6534-45b0-a3a8-bd8bc3719554\"\n" +
                "}";
        SelectService selectService = EasyMock.createMock(SelectService.class);
        EasyMock.expect(SwiftContext.get().getBean(SelectService.class)).andReturn(selectService).anyTimes();
        SwiftApiResultSet result = EasyMock.createMock(SwiftApiResultSet.class);
        try {
            EasyMock.expect(selectService.query(SwiftDatabase.CUBE, null)).andReturn(result).anyTimes();
        } catch (Exception e) {
            fail();
        }
        EasyMock.replay(SwiftContext.get(), selectService);
        ApiResponse response = new ApiServerServiceImpl().dispatchRequest(request);
        // TODO: 2018/12/12 不同类型sql对应返回值类型
        assertNotNull(response.result());
    }

    public void testTable() throws SwiftMetaDataException {
        String request = "{\n" +
                "    \"database\": \"cube\",\n" +
                "    \"auth\": \"authCode\",\n" +
                "    \"requestType\": \"TABLES\",\n" +
                "    \"requestId\": \"9756991f-ecc9-4aac-afd6-96b9453763fc\"\n" +
                "}";
        TableService tableService = EasyMock.createMock(TableService.class);
        EasyMock.expect(SwiftContext.get().getBean(TableService.class)).andReturn(tableService).anyTimes();
        List<SwiftMetaData> tables = new ArrayList<SwiftMetaData>();
        // Generate by Mock Plugin
        SwiftMetaData tableA = PowerMock.createMock(SwiftMetaData.class);
        EasyMock.expect(tableA.getTableName()).andReturn("tableA").anyTimes();
        SwiftMetaData tableB = PowerMock.createMock(SwiftMetaData.class);
        EasyMock.expect(tableB.getTableName()).andReturn("tableB").anyTimes();

        tables.add(tableA);
        tables.add(tableB);
        EasyMock.expect(tableService.detectiveAllTable(SwiftDatabase.CUBE)).andReturn(tables).anyTimes();
        EasyMock.replay(SwiftContext.get(), tableService, tableA, tableB);

        ApiResponse response = new ApiServerServiceImpl().dispatchRequest(request);
        assertTrue(response.result() instanceof List);
        assertEquals(((List<SwiftMetaData>) response.result()).get(0).getTableName(), "tableA");
        assertEquals(((List<SwiftMetaData>) response.result()).get(1).getTableName(), "tableB");
        assertTrue(true);
    }

    public void testFields() throws SwiftMetaDataAbsentException {
        String request = "{\n" +
                "    \"database\": \"cube\",\n" +
                "    \"table\": \"tableA\",\n" +
                "    \"auth\": \"authCode\",\n" +
                "    \"requestType\": \"COLUMNS\",\n" +
                "    \"requestId\": \"c028db37-72d4-4ac6-bc22-625770418791\"\n" +
                "}";

        TableService tableService = EasyMock.createMock(TableService.class);
        EasyMock.expect(SwiftContext.get().getBean(TableService.class)).andReturn(tableService).anyTimes();

        SwiftMetaDataBean swiftMetaDataBean = new SwiftMetaDataBean();
        List<SwiftMetaDataColumn> fields = new ArrayList<SwiftMetaDataColumn>();
        fields.add(new MetaDataColumnBean("a", 1));
        fields.add(new MetaDataColumnBean("b", 2));
        fields.add(new MetaDataColumnBean("c", 3));
        swiftMetaDataBean.setFields(fields);

        EasyMock.expect(tableService.detectiveMetaData(SwiftDatabase.CUBE, "tableA")).andReturn(swiftMetaDataBean).anyTimes();
        EasyMock.replay(SwiftContext.get(), tableService);

        ApiResponse response = new ApiServerServiceImpl().dispatchRequest(request);
        List<SwiftMetaDataColumn> resultFields = ((SwiftMetaDataBean) (response).result()).getFields();
        assertEquals(resultFields.size(), 3);
        assertEquals(resultFields.get(0).getName(), "a");
        assertEquals(resultFields.get(1).getName(), "b");
        assertEquals(resultFields.get(2).getName(), "c");
        assertEquals(resultFields.get(0).getType(), 1);
        assertEquals(resultFields.get(1).getType(), 2);
        assertEquals(resultFields.get(2).getType(), 3);

        assertTrue(true);
    }
}
