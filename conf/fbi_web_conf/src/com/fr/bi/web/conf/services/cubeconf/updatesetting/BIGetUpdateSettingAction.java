package com.fr.bi.web.conf.services.cubeconf.updatesetting;

import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Young's on 2016/12/29.
 */
public class BIGetUpdateSettingAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String sourceId = WebUtils.getHTTPRequestParameter(req, "id");
        UpdateSettingSource settingSource = BIConfigureManagerCenter.getUpdateFrequencyManager().getUpdateSetting(sourceId, userId);
        if (settingSource != null) {
            WebUtils.printAsJSON(res, settingSource.createJSON());
        }
    }

    @Override
    public String getCMD() {
        return "get_update_setting";
    }
}
