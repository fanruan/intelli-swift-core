package com.fr.bi.web.conf.services;

import com.fr.base.FRContext;
import com.fr.bi.conf.base.pack.data.BIPackageID;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONException;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;

/**
 * 移除业务包里面表的servelet
 */
public class BIRemoveTableFromBusinessPackageAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "remove_table_from_business_package";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
        String packageName = WebUtils.getHTTPRequestParameter(req, "package_name");
        String id = WebUtils.getHTTPRequestParameter(req, "id");

        String hasUsageChecked = WebUtils.getHTTPRequestParameter(req, "hasUsageChecked");
        if (ComparatorUtils.equals(hasUsageChecked, "yes")) {
            doRemoveTableFromBusinessPackage(req, packageName, id);
        } else {
//			BITableKey key = new BITableKey(id);
//			BIBusiPackManagerInterface busiManager = BIInterfaceAdapter.getBIBusiPackAdapter();
//			long userId = ServiceUtils.getCurrentUserID(req);
//			Iterator<String> packageIterator = busiManager.getPackageNameIterator(key, userId);
//			int count = 0;
//			while(packageIterator.hasNext()) {
//				packageIterator.next();
//				count++;
//			}
//			if(count > 1) {
//				doRemoveTableFromBusinessPackage(req, packageName, connectionName, md5tableName, schemaName);
//			} else {
//				boolean isTableHasTemplateUsed = false;
//                BIAbstractBusiTable table = BIInterfaceAdapter.getDataSourceAdapter().getMD5TableByID(md5tableName,userId);
//                Set<BITableKey> set = new HashSet<BITableKey>();
//                if(table != null){
//                    set = table.createRelationRelativeTableKeys();
//                } else{
//                    set.add(key);
//                }
//                Iterator<BITableKey> it = set.iterator();
//                while (it.hasNext()){
//                    BITableKey k = it.next();
//                    isTableHasTemplateUsed = BIReport.isTableInfoHasTemplateUsed(k);
//                    if(isTableHasTemplateUsed){
//                        break;
//                    }
//                }
//				if(isTableHasTemplateUsed) {
//					WebUtils.printAsString(res, "do_have_usage");
//				} else {
//					doRemoveTableFromBusinessPackage(req, packageName, id);
//				}
//			}
        }
    }

    private void doRemoveTableFromBusinessPackage(HttpServletRequest req, String packageName, String id) throws JSONException {
        long userId = ServiceUtils.getCurrentUserID(req);
        try {
            /**
             * Todo 又是packageName了
             */
            BIConfigureManagerCenter.getPackageManager().removeTable(userId, new BIPackageID(packageName), new BITableID(id));
            /**
             * Todo 保存资源
             */
//            FRContext.getCurrentEnv().writeResource(BIConfigureDataManager.getBusiPackManager().getInstance(userId));
        } catch (Exception e) {
            FRContext.getLogger().log(Level.WARNING, e.getMessage(), e);
        }
    }

}