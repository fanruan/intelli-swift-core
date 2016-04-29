package com.fr.bi.conf.manager.userInfo.manager;


import com.fr.bi.common.factory.IFactoryService;
import com.fr.bi.common.factory.annotation.BIMandatedObject;

/**
 * Created by GUY on 2015/3/27.
 */
@BIMandatedObject(factory = IFactoryService.CONF_XML, implement = LoginUserInfoManager.class)
public class LoginUserInfoManager extends AbstractLoginUserInfoManager {


    /**
     *
     */
    private static final long serialVersionUID = -7068198672030674880L;

    public LoginUserInfoManager(long userId) {
        super(userId);
    }
}