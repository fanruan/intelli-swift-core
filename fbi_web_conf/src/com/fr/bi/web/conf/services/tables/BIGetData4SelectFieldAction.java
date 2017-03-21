package com.fr.bi.web.conf.services.tables;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.pack.data.IBusinessPackageGetterService;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.bi.web.conf.utils.BIWebConfUtils;
import com.fr.fs.control.UserControl;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Set;

/**
 * 配置部分选字段
 * Created by Young's on 2017/3/16.
 */
public class BIGetData4SelectFieldAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        JSONObject jo = BIWebConfUtils.getAvailableGroupsPackages(userId);
        jo.put("fields", getAllFields(userId));
        jo.put("translations", BICubeConfigureCenter.getAliasManager().getTransManager(UserControl.getInstance().getSuperManagerID()).createJSON());
        WebUtils.printAsJSON(res, jo);
    }

    private JSONObject getAllFields(long userId) {
        JSONObject fields = new JSONObject();
        Set<IBusinessPackageGetterService> packs = BICubeConfigureCenter.getPackageManager().getAllPackages(userId);
        try {
            for (IBusinessPackageGetterService p : packs) {
                for (BIBusinessTable t : (Set<BIBusinessTable>) p.getBusinessTables()) {
                    Iterator<BusinessField> iterator = t.getFields().iterator();
                    while (iterator.hasNext()) {
                        BusinessField field = iterator.next();
                        fields.put(BIModuleUtils.getBusinessFieldById(field.getFieldID()).getFieldID().getIdentity(), field.createJSON());
                    }
                }
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return fields;
    }

    @Override
    public String getCMD() {
        return "get_data_4_select_field";
    }
}
