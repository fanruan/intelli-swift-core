package com.finebi.cube.conf;

import com.finebi.cube.conf.pack.XMLConfigureGenerator;
import com.fr.base.FRContext;
import com.fr.bi.common.container.BIStableMapContainer;
import com.fr.bi.exception.BIKeyAbsentException;
import com.finebi.cube.common.log.BILoggerFactory;

/**
 * This class created on 2016/5/23.
 *
 * @author Connery
 * @since 4.0
 */
public abstract class BISystemDataManager<MANAGER> extends BIStableMapContainer<Long, MANAGER> {

    private static final long serialVersionUID = -7890676855475278578L;

    @Override
    public MANAGER constructValue(Long key) {
        MANAGER manager = constructUserManagerValue(key);
        initialUserManager(key, manager);
        return manager;
    }

    public abstract MANAGER constructUserManagerValue(Long userId);

    public abstract String managerTag();

    public abstract String persistUserDataName(long key);

    protected void initialUserManager(long key, MANAGER manager) {
        try {
            XMLConfigureGenerator generator = new XMLConfigureGenerator(persistUserDataName(key) + ".xml", manager, managerTag());
            generator.readXMLFile();
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
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
            XMLConfigureGenerator generator = new XMLConfigureGenerator(persistUserDataName(key) + ".xml", getValue(key), managerTag());
            FRContext.getCurrentEnv().writeResource(generator);
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
    }
}