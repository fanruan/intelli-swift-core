package com.fr.bi.conf.provider;

import com.fr.bi.conf.manager.userInfo.manager.LoginUserInfoManager;

/**
 * Created by Connery on 2016/1/12.
 */
public interface BIUserLoginInformationProvider {

    String XML_TAG = "BIUserLoginInformationProvider";

    LoginUserInfoManager getUserInfoManager(long userId);


}