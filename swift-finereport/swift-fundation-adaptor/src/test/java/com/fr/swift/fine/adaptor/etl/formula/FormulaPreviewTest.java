package com.fr.swift.fine.adaptor.etl.formula;

import com.finebi.conf.structure.result.BIDetailCell;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.fr.swift.config.TestConfDb;
import com.fr.swift.provider.DataProvider;
import com.fr.swift.provider.impl.SwiftDataProvider;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.source.db.TestConnectionProvider;
import com.fr.swift.source.etl.ETLOperator;
import com.fr.swift.source.etl.EtlSource;
import com.fr.swift.source.etl.formula.ColumnFormulaOperator;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class FormulaPreviewTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TestConfDb.setConfDb();
//        LocalSwiftServerService.getInstance().start();
        TestConnectionProvider.createConnection();
    }

    public void testOneFormulaPreview() throws Exception {

        DataProvider dataProvider = new SwiftDataProvider();
        DataSource dataSource = new QueryDBSource("select * from DEMO_CAPITAL_RETURN", "allTest");
        ETLOperator formulaOperator = new ColumnFormulaOperator("addField", ColumnTypeConstants.ColumnType.NUMBER, "${付款金额} + ${付款金额}");
        List<DataSource> baseDataSources = new ArrayList<DataSource>();
        baseDataSources.add(dataSource);
        EtlSource etlSource = new EtlSource(baseDataSources, formulaOperator);

        BIDetailTableResult detailTableResult = dataProvider.getDetailPreviewByFields(null, 0);

        assertEquals(etlSource.getMetadata().getColumnCount(), 5);
        int count = 0;
        while (detailTableResult.hasNext()) {
            List<BIDetailCell> datas = detailTableResult.next();
            assertEquals(datas.size(), 5);
            int index = etlSource.getMetadata().getColumnIndex("付款金额");
            assertEquals((Double.valueOf(datas.get(index).getData().toString())) * 2, Double.valueOf(datas.get(0).getData().toString()));
            count++;
        }
        assertEquals(count, 100);
        assertTrue(true);
    }
}
