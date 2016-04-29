package com.fr.bi.conf.base.login;

import com.fr.bi.common.container.BIStableMapContainer;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.conf.base.pack.XMLConfigureGenerator;
import com.fr.bi.conf.manager.userInfo.manager.LoginUserInfoManager;
import com.fr.bi.conf.provider.BIUserLoginInformationProvider;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.stable.utils.code.BILogger;

/**
 * Created by Connery on 2016/1/13.
 */
public class BISystemUserLoginInformationManager extends BIStableMapContainer<Long, LoginUserInfoManager> implements BIUserLoginInformationProvider {
    @Override
    public LoginUserInfoManager constructValue(Long key) {
        LoginUserInfoManager manager = BIFactoryHelper.getObject(LoginUserInfoManager.class, key);
        initialUserManager(manager);
        return manager;
    }


    protected void initialUserManager(LoginUserInfoManager manager) {
        try {
            XMLConfigureGenerator writer = new XMLConfigureGenerator("BILoginUserInfo.xml", manager, "LoginUserInfo");
            writer.readXMLFile();
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }


    public LoginUserInfoManager getUserGroupConfigManager(long userId) {
        try {
            return getValue(userId);
        } catch (BIKeyAbsentException e) {

            throw new NullPointerException("Please check the userID:" + userId + ",which getIndex a empty manager");
        }
    }

    @Override
    public LoginUserInfoManager getUserInfoManager(long userId) {
        return getUserGroupConfigManager(userId);
    }
}