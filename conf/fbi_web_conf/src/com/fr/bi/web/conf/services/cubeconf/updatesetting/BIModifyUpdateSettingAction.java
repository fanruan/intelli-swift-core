package com.fr.bi.web.conf.services.cubeconf.updatesetting;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeManagerProvider;
import com.fr.bi.cal.BICubeManager;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.stable.bridge.StableFactory;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Young's on 2016/12/29.
 */
public class BIModifyUpdateSettingAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String sourceId = WebUtils.getHTTPRequestParameter(req, "id");
        String updateSettingStr = WebUtils.getHTTPRequestParameter(req, "updateSetting");
        UpdateSettingSource source = new UpdateSettingSource();
        if (updateSettingStr != null && sourceId != null) {
            source.parseJSON(new JSONObject(updateSettingStr));
            try {
                BIConfigureManagerCenter.getUpdateFrequencyManager().saveUpdateSetting(sourceId, source, userId);
                BIConfigureManagerCenter.getUpdateFrequencyManager().persistData(userId);
                StableFactory.getMarkedObject(BICubeManagerProvider.XML_TAG, BICubeManager.class).resetCubeGenerationHour(userId);
            } catch (Exception e) {
                BILoggerFactory.getLogger().error(e.getMessage());
            }
        }
    }

    @Override
    public String getCMD() {
        return "modify_update_setting";
    }
}
