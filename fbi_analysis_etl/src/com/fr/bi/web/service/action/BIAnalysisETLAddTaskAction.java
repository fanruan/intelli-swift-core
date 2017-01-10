package com.fr.bi.web.service.action;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.BIUser;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.etl.analysis.data.AnalysisCubeTableSource;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by kary on 17-1-6.
 */
public class BIAnalysisETLAddTaskAction extends AbstractAnalysisETLAction {
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        final long userId = ServiceUtils.getCurrentUserID(req);
        String tableId = WebUtils.getHTTPRequestParameter(req, "id");
        try {
            BusinessTable table = BIAnalysisETLManagerCenter.getBusiPackManager().getTable(tableId, userId);
            Set<AnalysisCubeTableSource> sources = new HashSet<AnalysisCubeTableSource>();
            // 判断Version只需判断自身,如果是AnalysisETLTableSource，则需要同时check自己的parents即AnalysisBaseTableSource
            ((AnalysisCubeTableSource) table.getTableSource()).getSourceNeedCheckSource(sources);
            for (AnalysisCubeTableSource s : sources) {
                BILoggerFactory.getLogger(BIAnalysisETLAddTaskAction.class).info(" check Version Of " + s.createUserTableSource(userId).fetchObjectCore().getIDValue());
                if (!BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider().checkVersion(s, new BIUser(userId))) {
                    BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider().addTask(s, new BIUser(userId));
                }
            }
        } catch (BITableAbsentException e) {
            BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
        }
        WebUtils.printAsJSON(res, new JSONObject());
    }

    @Override
    public String getCMD() {
        return "add_analysis_task";
    }

}
