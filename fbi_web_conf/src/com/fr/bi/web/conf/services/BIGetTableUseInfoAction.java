package com.fr.bi.web.conf.services;

import com.fr.bi.web.conf.AbstractBIConfigureAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 移除业务包里面表的servelet
 */
public class BIGetTableUseInfoAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "get_table_use_info";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
//		String connectionName = WebUtils.getHTTPRequestParameter(req, "connection_name");
//		String md5tableName = WebUtils.getHTTPRequestParameter(req, "md5_table_name");
//		String tableName = WebUtils.getHTTPRequestParameter(req, "table_name");
//		String packageName = WebUtils.getHTTPRequestParameter(req, "package_name");
//		String schemaName = WebUtils.getHTTPRequestParameter(req, "schema_name");
//
//        BITableKey key = new BITableKey(connectionName, schemaName, md5tableName,tableName, null);
//        BIBusiPackManagerInterface busiManager = BIInterfaceAdapter.getBIBusiPackAdapter();
//        long userId = ServiceUtils.getCurrentUserID(req);
//        Iterator<String> packageIterator = busiManager.getPackageNameIterator(key, userId);
//        int count = 0;
//        boolean isTableHasTemplateUsed = false;
//        while(packageIterator.hasNext()) {
//            packageIterator.next();
//            count++;
//        }
//        if(count > 1) {
//            isTableHasTemplateUsed = false;
//        } else {
//            BIAbstractBusiTable table = BIInterfaceAdapter.getDataSourceAdapter().getMD5TableByID(md5tableName,userId);
//            Set<BITableKey> set = new HashSet<BITableKey>();
//            if(table != null){
//                set = table.createRelationRelativeTableKeys();
//            } else{
//                set.add(key);
//            }
//            Iterator<BITableKey> it = set.iterator();
//            while (it.hasNext()){
//                BITableKey k = it.next();
//                isTableHasTemplateUsed = BIReport.isTableInfoHasTemplateUsed(k);
//                if(isTableHasTemplateUsed){
//                    break;
//                }
//            }
//        }
//        WebUtils.printAsJSON(res, new JSONObject().put("usable",isTableHasTemplateUsed));
    }
}