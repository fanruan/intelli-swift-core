package com.fr.bi.web.conf.services.datalink;

import com.fr.bi.conf.base.datasource.BIConnectionManager;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.ComparatorUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: Sheldon
 * Date: 13-10-30
 * Time: 下午3:40
 * To change this template use File | Settings | File Templates.
 */
public class BIModifyDataLinkAction extends AbstractBIConfigureAction {

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String actionType = WebUtils.getHTTPRequestParameter(req, "actionType");

        if (ComparatorUtils.equals(actionType, "delete")) {
            BIConnectionManager.getBIConnectionManager().removeConnection(WebUtils.getHTTPRequestParameter(req, "name"));
        } else if (ComparatorUtils.equals(actionType, "update")) {
            String linkData = WebUtils.getHTTPRequestParameter(req, "linkData");
            String oldName = WebUtils.getHTTPRequestParameter(req, "oldName");
            BIConnectionManager.getBIConnectionManager().updateConnection(linkData, oldName);

        }
    }

    @Override
    public String getCMD() {
        return "modify_data_link";
    }
}