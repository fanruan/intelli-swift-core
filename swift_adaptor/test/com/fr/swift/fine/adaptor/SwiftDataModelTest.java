package com.fr.swift.fine.adaptor;

import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.result.BIDetailCell;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.fr.swift.adaptor.model.SwiftDataModel;
import com.fr.swift.source.db.ConnectionInfo;
import com.fr.swift.source.db.ConnectionManager;
import com.fr.swift.source.db.IConnectionProvider;
import com.fr.swift.source.db.TableDBSource;
import com.fr.swift.source.db.TestConnectionProvider;
import junit.framework.TestCase;

import java.util.List;

/**
 * This class created on 2018-1-2 15:35:37
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftDataModelTest extends TestCase {

    private TableDBSource source;
    private ConnectionInfo connectionInfo;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        IConnectionProvider connectionProvider = new com.fr.swift.provider.ConnectionProvider();
        ConnectionManager.getInstance().registerProvider(connectionProvider);
        connectionInfo = TestConnectionProvider.createConnection();
        ConnectionManager.getInstance().registerConnectionInfo("local", connectionInfo);
    }

    public void testSwiftDataModelGetFields() throws Exception {
        SwiftDataModel dataModel = new SwiftDataModel();
        List<FineBusinessField> list = dataModel.getFieldList("local", "DEMO_CONTRACT", connectionInfo.getSchema(), connectionInfo.getFrConnection());
        assertEquals(list.size(), 10);
        assertTrue(true);
    }

    public void testSwiftDataModelPreviewDBTable() throws Exception {
        SwiftDataModel dataModel = new SwiftDataModel();
        BIDetailTableResult detailTableResult = dataModel.getPreviewData("local", "DEMO_CONTRACT", 150, connectionInfo.getSchema(), connectionInfo.getFrConnection());
        assertEquals(detailTableResult.columnSize(), 10);
        int count = 0;
        while (detailTableResult.hasNext()) {
            List<BIDetailCell> cellList = detailTableResult.next();
            assertEquals(cellList.size(), 10);
            count++;
        }
        assertEquals(count, 150);
        assertTrue(true);
    }
}
