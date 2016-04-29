package com.fr.bi.conf.base;

import com.fr.base.FRContext;
import com.fr.bi.common.container.BIStableMapContainer;
import com.fr.bi.conf.base.pack.XMLConfigureGenerator;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.stable.utils.code.BILogger;

/**
 * Created by Connery on 2016/1/20.
 */
public abstract class BISystemDataManager<MANAGER> extends BIStableMapContainer<Long, MANAGER> {

    @Override
    public MANAGER constructValue(Long key) {
        MANAGER manager = constructUserManagerValue(key);
        initialUserManager(manager);
        return manager;
    }

    public abstract MANAGER constructUserManagerValue(Long userId);

    public abstract String managerTag();

    protected void initialUserManager(MANAGER manager) {
        try {
            XMLConfigureGenerator generator = new XMLConfigureGenerator(managerTag() + ".xml", manager, managerTag());
            generator.readXMLFile();
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }


    public MANAGER getUserGroupConfigManager(long userId) {
        try {
            return getValue(userId);
        } catch (BIKeyAbsentException e) {

            throw new NullPointerException("Please check the userID:" + userId + ",which getIndex a empty manager");
        }
    }

    public void persistUserData(long key) {
        try {
            XMLConfigureGenerator generator = new XMLConfigureGenerator(managerTag() + ".xml", getValue(key), managerTag());
            FRContext.getCurrentEnv().writeResource(generator);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }
}