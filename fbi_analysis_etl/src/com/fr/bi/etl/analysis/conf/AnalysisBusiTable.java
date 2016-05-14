package com.fr.bi.etl.analysis.conf;

import com.fr.bi.base.BICore;
import com.fr.bi.conf.base.pack.data.BIBusinessTable;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.etl.analysis.manager.BIAnalysisDataSourceManagerProvider;
import com.fr.bi.stable.data.source.ITableSource;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.stable.bridge.StableFactory;

import java.util.Map;

/**
 * Created by 小灰灰 on 2015/12/11.
 */
public class AnalysisBusiTable extends BIBusinessTable {

    private Map<BICore, String> sheets;

    public AnalysisBusiTable(String id, long userId) {
        super(id, userId);
    }

    public void setSource(ITableSource source){
        this.source = source;
    }

    @Override
    public ITableSource getSource() {
        if (source == null) {
            try {
                source = StableFactory.getMarkedObject(BIAnalysisDataSourceManagerProvider.XML_TAG, BIAnalysisDataSourceManagerProvider.class).getTableSourceByID(getID(), getUser());
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }
        if (source == null) {
            BILogger.getLogger().info("UserEtl source missed");
        }
        return source;
    }

    protected int getTableType(){
        return Constants.BUSINESS_TABLE_TYPE.ANALYSIS_TYPE;
    }

}