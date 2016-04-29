package com.fr.bi.web.conf.services;

import com.fr.bi.web.conf.AbstractBIConfigureAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by sheldon on 14-10-16.
 */
public class BIGetConfPrimaryTableAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {

//        String tableInforString = WebUtils.getHTTPRequestParameter(req, "table_infor");
//        String packageName = WebUtils.getHTTPRequestParameter(req, "package_name");
//        long userId = ServiceUtils.getCurrentUserID(req);
//        List<BITableKey> targetList = new ArrayList<BITableKey>();
//        JSONArray targetsJa = new JSONArray();
//
//        JSONObject tableJo = new JSONObject( tableInforString );
//        if( packageName == null && tableJo.has("package_name") ){
//            packageName = tableJo.getString("package_name");
//        }
//
//        String id = tableJo.getString("id");
//        BITableKey tableKey = new BITableKey(id);
//
//        Set<BIAbstractFieldDefine> tableFieldSet = new HashSet<BIAbstractFieldDefine>();
//        BIConfUtils.analysisUseGetKeyTablesByEnd( tableFieldSet, tableKey , userId);
//
//        targetList.add( tableKey );
//
//        Iterator iterator = tableFieldSet.iterator();
//        while ( iterator.hasNext() ) {
//
//            BIAbstractFieldDefine fieldDefine = ( BIAbstractFieldDefine )iterator.next();
//
//            BIAbstractBusiTable keyTable = BIConfUtils.getBusiTable4Config(fieldDefine.createKey(), userId);
//            if( keyTable == null ) {
//                continue;
//            }
//            BITableKey priTablekey = keyTable.createTableKey();
//
//            String connectionName = keyTable.getConnectionName();
//
//            if (keyTable != null && !targetList.contains( priTablekey ) ) {
//
//                targetList.add( priTablekey );
////                targetsJa.put( keyTable.asJsonWithFieldDetailInfor4BIConfigure(packageName, connectionName, false) );
//
//                JSONObject priTableJo = keyTable.asJsonWithFieldDetailInfor4BIConfigure(packageName, connectionName, false, userId);
//                BITableRelation[][] relations = BIConfUtils.createNewRelationList( tableKey, priTablekey, userId);
//                JSONArray targetRelations = new JSONArray();
//
//                if(relations != null) {
//                    for(int ji = 0; ji < relations.length; ji++) {
//                        JSONArray relationJa = new JSONArray();
//                        BITableRelation[] aPath = relations[ji];
//                        for(int jt = 0; jt < aPath.length; jt++) {
//                            relationJa.put(aPath[jt].createJSON(userId));
//                        }
//
//                        targetRelations.put( relationJa );
//                    }
//                }
//
//                priTableJo.put("target_relation", targetRelations );
//
//                targetsJa.put( priTableJo );
//            }
//        }
//
//
//        tableJo.put("key_tables", targetsJa);
//        WebUtils.printAsJSON(res, tableJo);
    }

    /**
     * cmd参数值，例如op=write&cmd=sort
     *
     * @return 返回该请求的cmd参数的值
     */
    @Override
    public String getCMD() {
        return "get_conf_primary_table";
    }
}