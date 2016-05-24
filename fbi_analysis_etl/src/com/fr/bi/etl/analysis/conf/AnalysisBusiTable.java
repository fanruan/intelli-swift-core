package com.fr.bi.etl.analysis.conf;

import com.finebi.cube.conf.table.BIBusinessTable;
import com.fr.bi.base.BIUser;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.code.BILogger;

/**
 * Created by 小灰灰 on 2015/12/11.
 */
public class AnalysisBusiTable extends BIBusinessTable {

    public long userID;

    public AnalysisBusiTable(String id, long userId) {
        super(id, "");
        userId = userId;
    }

    public void setSource(CubeTableSource source) {
        this.source = source;
    }

    public CubeTableSource getSource() {
        if (source == null) {
            try {
                source = BIAnalysisETLManagerCenter.getDataSourceManager().getTableSourceByID(getID(), new BIUser(-999));
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }
        if (source == null) {
            BILogger.getLogger().info("UserEtl source missed");
        }
        return source;
    }

    protected int getTableType() {
        return Constants.BUSINESS_TABLE_TYPE.ANALYSIS_TYPE;
    }

}