package com.fr.bi.web.conf.services.packs;

import com.fr.bi.conf.base.pack.data.BIBusinessPackage;
import com.fr.bi.conf.base.pack.data.BIBusinessTable;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.data.BIBasicField;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Set;

public class BIGetAllBusinessPackagesAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "get_all_business_packages";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        JSONObject jo = new JSONObject();
        Set<BIBusinessPackage> packs = BIConfigureManagerCenter.getPackageManager().getAllPackages(userId);
        for(BIBusinessPackage pack : packs) {
            Set<BIBusinessTable> tables = pack.getBusinessTables();
            JSONObject tableJO = new JSONObject();
            for(BIBusinessTable table : tables) {
                Iterator<BIBasicField> fields = table.getFieldsIterator();
                JSONArray fieldsJA = new JSONArray();
                while (fields.hasNext()) {
                    fieldsJA.put(fields.next().createJSON());
                }
                tableJO.put(table.getID().getIdentity(), fieldsJA);
            }
            jo.put(pack.getID().getIdentity(), tableJO);
        }
        WebUtils.printAsJSON(res, jo);
    }
}