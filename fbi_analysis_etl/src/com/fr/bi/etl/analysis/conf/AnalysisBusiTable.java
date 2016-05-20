package com.fr.bi.etl.analysis.conf;

import com.fr.bi.conf.base.pack.data.BIBusinessTable;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
import com.fr.bi.stable.data.source.ICubeTableSource;
import com.fr.bi.stable.utils.code.BILogger;

/**
 * Created by 小灰灰 on 2015/12/11.
 */
public class AnalysisBusiTable extends BIBusinessTable {

    public AnalysisBusiTable(String id, long userId) {
        super(id, userId);
    }

    public void setSource(ICubeTableSource source){
        this.source = source;
    }

    @Override
    public ICubeTableSource getSource() {
        if (source == null) {
            try {
                source = BIAnalysisETLManagerCenter.getDataSourceManager().getTableSourceByID(getID(), getUser());
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