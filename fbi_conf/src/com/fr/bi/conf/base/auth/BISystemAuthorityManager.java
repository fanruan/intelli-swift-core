package com.fr.bi.conf.base.auth;

import com.finebi.cube.conf.BISystemDataManager;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.fr.bi.conf.base.auth.data.BIPackageAuthority;
import com.fr.bi.conf.provider.BIAuthorityManageProvider;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.fs.control.UserControl;
import com.fr.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by Young's on 2016/5/18.
 */
public class BISystemAuthorityManager extends BISystemDataManager<BIAuthorityManager> implements BIAuthorityManageProvider {

    @Override
    public BIAuthorityManager constructUserManagerValue(Long userId) {
        return new BIAuthorityManager();
    }

    @Override
    public String managerTag() {
        return "BISystemAuthorityManager";
    }

    @Override
    public String persistUserDataName(long key) {
        return managerTag();
    }

    public List<BIPackageAuthority> getPackageAuthByID(BIPackageID packageID, long userId) {
        try {
            return getValue(UserControl.getInstance().getSuperManagerID()).getPackageAuthByID(packageID, userId);
        } catch (BIKeyAbsentException e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public JSONObject createJSON(long userId) throws Exception {
        try {
            return getValue(UserControl.getInstance().getSuperManagerID()).createJSON(userId);
        } catch (BIKeyAbsentException e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void clear(long userId) {
        try {
            getValue(UserControl.getInstance().getSuperManagerID()).clear(userId);
        } catch (BIKeyAbsentException e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }

    @Override
    public void persistData(long userId) {
        super.persistUserData(userId);
    }

    public Map<BIPackageID, List<BIPackageAuthority>> getPackagesAuth(long userId){
        try {
            return getValue(UserControl.getInstance().getSuperManagerID()).getPackagesAuth(userId);
        } catch (BIKeyAbsentException e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

    public void savePackageAuth(BIPackageID packageID, List<BIPackageAuthority> authorities, long userId) {
        try {
            getValue(UserControl.getInstance().getSuperManagerID()).savePackageAuth(packageID, authorities, userId);
        } catch (BIKeyAbsentException e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }
}
