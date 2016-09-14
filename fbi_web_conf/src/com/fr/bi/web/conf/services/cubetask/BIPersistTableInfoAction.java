package com.fr.bi.web.conf.services.cubetask;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.conf.BICubeConfiguration;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.fr.bi.conf.data.source.DBTableSource;
import com.fr.bi.conf.data.source.ETLTableSource;
import com.fr.bi.conf.data.source.SQLTableSource;
import com.fr.bi.stable.constant.BIBaseConstant;
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
public class BIPersistTableInfoAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        WebUtils.printAsJSON(res, new JSONObject().put("tableInfo", saveTableInfo(userId)));
    }
    @Override
    public String getCMD() {
        return "table_info_get";
    }

    private static Map saveTableInfo(long userId) {
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
            if (source.getType()== BIBaseConstant.TABLETYPE.ETL){
                StringBuffer EtlInfo=new StringBuffer();
                EtlInfo.append("ETL,parents as listed：");
                for (CubeTableSource tableSource : ((ETLTableSource) source).getParents()) {
                    EtlInfo.append(tableSource.getSourceID()+tableSource.getTableName());
                }
                sourceInfo.put(EtlInfo.toString(),source.getSourceID());
            }
        }
        Map<String, String> relationMap = new HashMap<String, String>();
        Set<BITableRelation> tableRelations = BICubeConfigureCenter.getTableRelationManager().getAllTableRelation(userId);
        for (BITableRelation relation : tableRelations) {
            relationMap.put(relation.getPrimaryTable().getTableSource().toString()+"."+relation.getPrimaryField().getFieldName()+">>"+relation.getForeignTable().getTableSource().toString()+"."+relation.getForeignField().getFieldName(),relation.getPrimaryTable().getTableSource().getSourceID()+"||"+relation.getForeignTable().getTableSource().getSourceID());
        }
        ICubeConfiguration advancedConf = BICubeConfiguration.getConf(Long.toString(userId));
        String cubeTablePath = new File(advancedConf.getRootURI().getPath()).getParent() + File.separatorChar + "tableInfo";
        String cubeRelationPath = new File(advancedConf.getRootURI().getPath()).getParent() + File.separatorChar + "relationInfo";
        BIFileUtils.writeFile(cubeTablePath, sourceInfo.toString());
        BIFileUtils.writeFile(cubeRelationPath, relationMap.toString());
        Map<String, String> info=new HashMap();
        info.put("tableInfo",sourceInfo.toString());
        info.put("relationInfo",relationMap.toString());
        return info;
    }
}
