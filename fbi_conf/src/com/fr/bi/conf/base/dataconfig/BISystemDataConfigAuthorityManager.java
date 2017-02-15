package com.fr.bi.conf.base.dataconfig;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BISystemDataManager;
import com.fr.bi.conf.base.dataconfig.source.BIDataConfigAuthority;
import com.fr.bi.conf.provider.BIDataConfigAuthorityProvider;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.fs.control.UserControl;

import java.util.Set;

/**
 * Created by Young's on 2017/1/16.
 */
public class BISystemDataConfigAuthorityManager extends BISystemDataManager<BIDataConfigAuthorityManager> implements BIDataConfigAuthorityProvider {
    @Override
    public Set<BIDataConfigAuthority> getAllDataConfigAuthorities() {
        try {
            return getValue(UserControl.getInstance().getSuperManagerID()).getDataConfigAuthorities();
        } catch (Exception e) {
            BILoggerFactory.getLogger(BISystemDataConfigAuthorityManager.class).error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Set<BIDataConfigAuthority> getDataConfigAuthoritiesByRole(String roleName, int roleType) {
        try {
            return getValue(UserControl.getInstance().getSuperManagerID()).getDataConfigAuthoritiesByRole(roleName, roleType);
        } catch (Exception e) {
            BILoggerFactory.getLogger(BISystemDataConfigAuthorityManager.class).error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Set<BIDataConfigAuthority> getDataConfigAuthoritiesByUserId(long userId) {
        try {
            return getValue(UserControl.getInstance().getSuperManagerID()).getDataConfigAuthoritiesByUserId(userId);
        } catch (Exception e) {
            BILoggerFactory.getLogger(BISystemDataConfigAuthorityManager.class).error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void saveDataConfigAuthorities(Set<BIDataConfigAuthority> authorities) {
        try {
            getValue(UserControl.getInstance().getSuperManagerID()).setDataConfigAuthorities(authorities);
        } catch (Exception e) {
            BILoggerFactory.getLogger(BISystemDataConfigAuthorityManager.class).error(e.getMessage(), e);
        }
    }

    @Override
    public void clear() {
        try {
            getValue(UserControl.getInstance().getSuperManagerID()).clear();
        } catch (BIKeyAbsentException e) {
            BILoggerFactory.getLogger(BISystemDataConfigAuthorityManager.class).error(e.getMessage(), e);
        }
    }

    @Override
    public void persistData(long userId) {
        super.persistUserData(userId);
    }

    @Override
    public BIDataConfigAuthorityManager constructUserManagerValue(Long userId) {
        return new BIDataConfigAuthorityManager();
    }

    @Override
    public String managerTag() {
        return "BISystemDataConfigAuthorityManager";
    }

    @Override
    public String persistUserDataName(long key) {
        return managerTag();
    }
}
