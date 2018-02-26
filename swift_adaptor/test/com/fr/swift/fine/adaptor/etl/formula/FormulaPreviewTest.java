package com.fr.swift.fine.adaptor.etl.formula;

import com.finebi.conf.structure.result.BIDetailCell;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.fr.swift.adaptor.executor.SwiftFieldsDataPreview;
import com.fr.swift.generate.BaseTest;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.source.etl.ETLOperator;
import com.fr.swift.source.etl.ETLSource;
import com.fr.swift.source.etl.formula.ColumnFormulaOperator;

import java.util.ArrayList;
import java.util.List;

public class FormulaPreviewTest extends BaseTest {

    public void testOneFormulaPreview() throws Exception {
        DataSource dataSource = new QueryDBSource("select * from DEMO_CAPITAL_RETURN", "allTest");
        ETLOperator formulaOperator = new ColumnFormulaOperator("addField", ColumnTypeConstants.ColumnType.NUMBER, "${付款金额} + ${付款金额}");
        List<DataSource> baseDataSources = new ArrayList<DataSource>();
        baseDataSources.add(dataSource);
        ETLSource etlSource = new ETLSource(baseDataSources, formulaOperator);

        SwiftFieldsDataPreview swiftFieldsDataPreview = new SwiftFieldsDataPreview();
        BIDetailTableResult detailTableResult = swiftFieldsDataPreview.getDetailPreviewByFields(etlSource, 200);

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
