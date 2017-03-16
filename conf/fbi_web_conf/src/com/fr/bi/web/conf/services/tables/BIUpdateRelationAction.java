package com.fr.bi.web.conf.services.tables;

import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.bi.web.conf.utils.BIReadRelationTranslationUtils;
import com.fr.bi.web.conf.utils.BIWriteConfigResourcesUtils;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 更新关联
 * Created by Young's on 2016/12/21.
 */
public class BIUpdateRelationAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String fieldId = WebUtils.getHTTPRequestParameter(req, "id");
        String relationsStr = WebUtils.getHTTPRequestParameter(req, "relations");

        JSONObject relationsJO = new JSONObject(relationsStr);
        BIReadRelationTranslationUtils.saveRelation(userId, relationsJO.getJSONArray("connectionSet"), fieldId);
        BIWriteConfigResourcesUtils.writeResourceAsync(userId);
    }

    @Override
    public String getCMD() {
        return "update_relation";
    }
}
