/**
 *
 */
package com.fr.bi.web.conf.services.cubeconf;


import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableRelationPath;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class BIUpdateAccessMultiPathAction extends AbstractBIConfigureAction {

    /* (non-Javadoc)
     * @see com.fr.web.core.AcceptCMD#getCMD()
     */
    @Override
    public String getCMD() {
        return "update_access_multi_path";
    }

    /**
     * 方法
     *
     * @param req 参数
     * @param res 参数
     * @throws Exception
     */
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
        String relationsString = WebUtils.getHTTPRequestParameter(req, "relations");
        String typeString = WebUtils.getHTTPRequestParameter(req, "type");
        long userId = ServiceUtils.getCurrentUserID(req);
        int type = Integer.parseInt(typeString);
        JSONArray relationsJSON = new JSONArray(relationsString);
        BITableRelation[] relations = new BITableRelation[relationsJSON.length()];
        for (int i = 0; i < relations.length; i++) {
            BITableRelation rel = new BITableRelation();
            rel.parseJSON(relationsJSON.optJSONObject(i));
            relations[i] = rel;
        }
        BITableRelationPath path = new BITableRelationPath(relations);
        switch (type) {
            case 0://取消选中，既删除该路径

                BICubeConfigureCenter.getTableRelationManager().addDisableRelations(userId, path);
                break;
            case 1://选中，使用该路径
                BICubeConfigureCenter.getTableRelationManager().removeDisableRelations(userId, path);
                break;
        }
        try {
            BICubeConfigureCenter.getTableRelationManager().persistData(userId);
        } catch (Exception e) {

        }
    }
}