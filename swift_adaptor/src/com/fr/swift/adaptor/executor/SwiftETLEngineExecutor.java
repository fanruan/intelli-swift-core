package com.fr.swift.adaptor.executor;

import com.finebi.base.common.resource.FineResourceItem;
import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.internalimp.service.engine.table.FineTableEngineExecutor;
import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.bean.table.BusinessTableBean;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.structure.conf.base.EngineConfTable;
import com.finebi.conf.structure.result.BIDetailTableResult;

import java.util.List;

/**
 * This class created on 2018-1-26 14:13:26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftETLEngineExecutor implements FineTableEngineExecutor {
    @Override
    public BIDetailTableResult getPreviewData(FineBusinessTable table, int rowCount) throws Exception {
        return null;
    }

    @Override
    public BIDetailTableResult getRealData(FineBusinessTable table) throws Exception {
        return null;
    }

    @Override
    public List<FineBusinessField> getFieldList(FineBusinessTable table) throws Exception {
        return null;
    }

    @Override
    public EngineConfTable createTable(BusinessTableBean bean) throws Exception {
        return null;
    }

    @Override
    public boolean refresh(EngineConfTable table) {
        return false;
    }

    @Override
    public boolean isAvailable(FineResourceItem item) {
        return false;
    }

    @Override
    public String getName(FineResourceItem item) {
        return null;
    }

    @Override
    public FineEngineType getEngineType() {
        return null;
    }
}
