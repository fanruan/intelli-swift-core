package com.fr.bi.web.conf.services.cubetask;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.conf.BICubeConfiguration;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.conf.data.source.DBTableSource;
import com.fr.bi.conf.data.source.SQLTableSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by kary on 2016/8/22.
 */
public class BIGetDBTableInfoMapAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        WebUtils.printAsJSON(res, new JSONObject().put("tableInfo", saveTableInfos(userId)));
    }

    @Override
    public String getCMD() {
        return "table_info_get";
    }

    private static Map saveTableInfos(long userId) {
        Set<CubeTableSource> sources = new HashSet<CubeTableSource>();
        Set<BusinessTable> allTables = BICubeConfigureCenter.getPackageManager().getAllTables(userId);
        for (BusinessTable table : allTables) {
            sources.add(table.getTableSource());
        }
        Map<String, String> sourceInfo = new HashMap<String, String>();
        for (CubeTableSource source : sources) {
            if (source instanceof SQLTableSource) {
                String queryName = ((SQLTableSource) source).getSqlConnection() + "@" + ((SQLTableSource) source).getQuery() + ((SQLTableSource) source).getSqlConnection();
                sourceInfo.put(queryName, source.getSourceID());
            }
            if (source instanceof DBTableSource) {
                sourceInfo.put(source.toString(), source.getSourceID());
            }
        }
        ICubeConfiguration advancedConf = BICubeConfiguration.getConf(Long.toString(userId));
        String cubePath = new File(advancedConf.getRootURI().getPath()).getParent() + File.separatorChar + "tableInfo";
        BIFileUtils.writeFile(cubePath, sourceInfo.toString());
        return sourceInfo;
    }
}
