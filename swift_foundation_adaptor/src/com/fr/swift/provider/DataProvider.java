package com.fr.swift.provider;

import com.finebi.conf.internalimp.analysis.bean.operator.add.group.custom.number.NumberMaxAndMinValue;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.DataSource;

import java.sql.SQLException;
import java.util.List;

/**
 * This class created on 2018/4/2
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public interface DataProvider {

    List<Segment> getPreviewData(DataSource dataSource) throws Exception;

    List<Segment> getRealData(DataSource dataSource) throws Exception;

    boolean isSwiftAvailable(DataSource dataSource);

    BIDetailTableResult getDetailPreviewByFields(FineBusinessTable table, int rowCount) throws SQLException;

    NumberMaxAndMinValue getNumberMaxAndMinValue(DataSource dataSource, String fieldName);

    List<Object> getGroupPreviewByFields(DataSource dataSource, String fieldName);
}
