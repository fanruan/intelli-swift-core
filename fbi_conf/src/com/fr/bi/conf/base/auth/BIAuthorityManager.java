package com.fr.bi.conf.base.auth;

import com.finebi.cube.conf.pack.data.BIPackageID;
import com.fr.bi.conf.base.auth.data.BIPackageAuthority;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.fs.base.entity.CompanyRole;
import com.fr.fs.base.entity.CustomRole;
import com.fr.fs.control.CompanyRoleControl;
import com.fr.fs.control.CustomRoleControl;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.*;

/**
 * Created by young
 */
public class BIAuthorityManager {

    private Map<BIPackageID, List<BIPackageAuthority>> packagesAuth = new HashMap<BIPackageID, List<BIPackageAuthority>>();

    public void savePackageAuth(BIPackageID packageID, List<BIPackageAuthority> auth, long userId) {
        this.packagesAuth.put(packageID, auth);
    }

    public Map<BIPackageID, List<BIPackageAuthority>> getPackagesAuth(long userId) {
        return packagesAuth;
    }

    public List<BIPackageAuthority> getPackageAuthByID(BIPackageID packageID, long userId) {
        return this.packagesAuth.get(packageID);
    }

    public List<BIPackageID> getAuthPackagesByUser(long userId) throws Exception {
        List<BIPackageID> packageIDs = new ArrayList<BIPackageID>();
        Set<CompanyRole> comRoles = CompanyRoleControl.getInstance().getCompanyRoleSet(userId);
        Set<CustomRole> cusRoles = CustomRoleControl.getInstance().getCustomRoleSet(userId);
        List<Long> comRoleIds = new ArrayList<Long>();
        List<Long> cusRoleIds = new ArrayList<Long>();
        for (CustomRole role : cusRoles) {
            cusRoleIds.add(role.getId());
        }
        for (CompanyRole role : comRoles) {
            comRoleIds.add(role.getId());
        }
        Iterator<Map.Entry<BIPackageID, List<BIPackageAuthority>>> iterator = this.packagesAuth.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<BIPackageID, List<BIPackageAuthority>> packAuth = iterator.next();
            List<BIPackageAuthority> authorities = packAuth.getValue();
            BIPackageID pId = packAuth.getKey();
            for (int i = 0; i < authorities.size(); i++) {
                BIPackageAuthority auth = authorities.get(i);
                long roleId = auth.getRoleId();
                //TODO 过滤
                if ((comRoleIds.contains(roleId) && auth.getRoleType() == BIBaseConstant.ROLE_TYPE.COMPANY) ||
                        (cusRoleIds.contains(roleId) && auth.getRoleType() == BIBaseConstant.ROLE_TYPE.CUSTOM)) {
                    packageIDs.add(pId);
                }
            }
        }
        return packageIDs;
    }

    public JSONObject createJSON(long userId) throws Exception {
        JSONObject jo = new JSONObject();
        Iterator<Map.Entry<BIPackageID, List<BIPackageAuthority>>> iterator = this.packagesAuth.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<BIPackageID, List<BIPackageAuthority>> packAuth = iterator.next();
            List<BIPackageAuthority> authorities = packAuth.getValue();
            JSONArray ja = new JSONArray();
            for (int i = 0; i < authorities.size(); i++) {
                ja.put(authorities.get(i).createJSON());
            }
            jo.put(packAuth.getKey().getIdentity(), ja);
        }
        return jo;
    }

    public void clear(long userId) {
        synchronized (this.packagesAuth) {
            this.packagesAuth.clear();
        }
    }
}
