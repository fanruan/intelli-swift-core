package com.fr.bi.conf.provider;

import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.conf.base.BISystemDataManager;
import com.fr.bi.conf.base.pack.BIUserPackAndAuthConfigurationManager;
import com.fr.bi.conf.base.pack.data.BIBusinessPackage;
import com.fr.json.JSONObject;

import java.util.Set;

/**
 * Created by wuk on 16/4/25.
 */
public class BISystemPackAndAuthConfigurationManager extends BISystemDataManager<BIUserPackAndAuthConfigurationManager> implements BISystemPackAndAuthConfigurationProvider {

    @Override
    public JSONObject createPackageJSON(long userId) throws Exception {
        JSONObject jsonObject1=getUserGroupConfigManager(userId).getPackAndAuthConfigManager().createPackageJSON();
        return jsonObject1;
    }

    @Override
    public Set<BIBusinessPackage> getAllPackages(long userId) {
        return getUserGroupConfigManager(userId).getCurrentPackage4Generating();
    }


    @Override
    public BIUserPackAndAuthConfigurationManager constructUserManagerValue(Long userId) {
        return BIFactoryHelper.getObject(BIUserPackAndAuthConfigurationManager.class, userId);
    }

    @Override
    public String managerTag() {
        return "packageAndAuthority";
//        return "BusinessPackage";
    }


}
