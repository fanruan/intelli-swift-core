package com.fr.bi.conf.base.auth;

import com.finebi.cube.conf.pack.data.BIPackageID;
import com.fr.bi.conf.base.auth.data.BIPackageAuthority;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.fs.base.entity.CompanyRole;
import com.fr.fs.base.entity.CustomRole;
import com.fr.fs.control.CompanyRoleControl;
import com.fr.fs.control.CustomRoleControl;
import com.fr.fs.control.DepartmentControl;
import com.fr.fs.control.PostControl;
import com.fr.general.ComparatorUtils;
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

    public List<BIPackageAuthority> getPackageAuthByID(BIPackageID packageID, long userId) throws Exception {
        List<BIPackageAuthority> packAuths = this.packagesAuth.get(packageID);
        List<BIPackageAuthority> result = new ArrayList<BIPackageAuthority>();
        Set<CompanyRole> comRoles = CompanyRoleControl.getInstance().getCompanyRoleSet(userId);
        Set<CustomRole> cusRoles = CustomRoleControl.getInstance().getCustomRoleSet(userId);
        for (int i = 0; i < packAuths.size(); i++) {
            BIPackageAuthority auth = packAuths.get(i);
            String roleName = auth.getRoleName();
            int roleType = auth.getRoleType();
            switch (roleType) {
                case BIBaseConstant.ROLE_TYPE.COMPANY:
                    for (CompanyRole companyRole : comRoles) {
                        String dName = DepartmentControl.getInstance().getDepartmentShowName(companyRole.getDepartmentId());
                        String pName = PostControl.getInstance().getPostName(companyRole.getPostId());
                        if (ComparatorUtils.equals(dName + "," + pName, roleName)) {
                            result.add(auth);
                        }
                    }
                    break;
                case BIBaseConstant.ROLE_TYPE.CUSTOM:
                    for (CustomRole customRole : cusRoles) {
                        if (ComparatorUtils.equals(customRole.getRolename(), roleName)) {
                            result.add(auth);
                        }
                    }
                    break;
            }
        }
        return result;
    }

    public List<BIPackageAuthority> getPackageAuthBySession(BIPackageID packageID, BISessionProvider session) throws Exception {
        List<BIPackageAuthority> packAuths = this.packagesAuth.get(packageID);
        List<BIPackageAuthority> result = new ArrayList<BIPackageAuthority>();
        for (int i = 0; i < packAuths.size(); i++) {
            BIPackageAuthority auth = packAuths.get(i);
            List<CompanyRole> comRoles = session.getCompanyRoles();
            List<CustomRole> cusRoles = session.getCustomRoles();
            String roleName = auth.getRoleName();
            int roleType = auth.getRoleType();
            switch (roleType) {
                case BIBaseConstant.ROLE_TYPE.COMPANY:
                    for (CompanyRole companyRole : comRoles) {
                        String dName = DepartmentControl.getInstance().getDepartmentShowName(companyRole.getDepartmentId());
                        String pName = PostControl.getInstance().getPostName(companyRole.getPostId());
                        if (ComparatorUtils.equals(dName + "," + pName, roleName)) {
                            result.add(auth);
                        }
                    }
                    break;
                case BIBaseConstant.ROLE_TYPE.CUSTOM:
                    for (CustomRole customRole : cusRoles) {
                        if (ComparatorUtils.equals(customRole.getRolename(), roleName)) {
                            result.add(auth);
                        }
                    }
                    break;
            }
        }
        return result;
    }

    public List<BIPackageID> getAuthPackagesByUser(long userId) throws Exception {
        List<BIPackageID> packageIDs = new ArrayList<BIPackageID>();
        Set<CompanyRole> comRoles = CompanyRoleControl.getInstance().getCompanyRoleSet(userId);
        Set<CustomRole> cusRoles = CustomRoleControl.getInstance().getCustomRoleSet(userId);
        Iterator<Map.Entry<BIPackageID, List<BIPackageAuthority>>> iterator = this.packagesAuth.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<BIPackageID, List<BIPackageAuthority>> packAuth = iterator.next();
            List<BIPackageAuthority> authorities = packAuth.getValue();
            BIPackageID pId = packAuth.getKey();
            for (int i = 0; i < authorities.size(); i++) {
                BIPackageAuthority auth = authorities.get(i);
                String roleName = auth.getRoleName();
                int roleType = auth.getRoleType();
                switch (roleType) {
                    case BIBaseConstant.ROLE_TYPE.COMPANY:
                        for (CompanyRole companyRole : comRoles) {
                            String dName = DepartmentControl.getInstance().getDepartmentShowName(companyRole.getDepartmentId());
                            String pName = PostControl.getInstance().getPostName(companyRole.getPostId());
                            if (ComparatorUtils.equals(dName + "," + pName, roleName)) {
                                packageIDs.add(pId);
                            }
                        }
                        break;
                    case BIBaseConstant.ROLE_TYPE.CUSTOM:
                        for (CustomRole customRole : cusRoles) {
                            if (ComparatorUtils.equals(customRole.getRolename(), roleName)) {
                                packageIDs.add(pId);
                            }
                        }
                        break;
                }
            }
        }
        return packageIDs;
    }

    public List<BIPackageID> getAuthPackagesBySession(BISessionProvider session) throws Exception {
        List<BIPackageID> packageIDs = new ArrayList<BIPackageID>();
        Iterator<Map.Entry<BIPackageID, List<BIPackageAuthority>>> iterator = this.packagesAuth.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<BIPackageID, List<BIPackageAuthority>> packAuth = iterator.next();
            List<BIPackageAuthority> authorities = packAuth.getValue();
            BIPackageID pId = packAuth.getKey();
            for (int i = 0; i < authorities.size(); i++) {
                BIPackageAuthority auth = authorities.get(i);
                List<CompanyRole> comRoles = session.getCompanyRoles();
                List<CustomRole> cusRoles = session.getCustomRoles();
                String roleName = auth.getRoleName();
                int roleType = auth.getRoleType();
                switch (roleType) {
                    case BIBaseConstant.ROLE_TYPE.COMPANY:
                        for (CompanyRole companyRole : comRoles) {
                            String dName = DepartmentControl.getInstance().getDepartmentShowName(companyRole.getDepartmentId());
                            String pName = PostControl.getInstance().getPostName(companyRole.getPostId());
                            if (ComparatorUtils.equals(dName + "," + pName, roleName)) {
                                packageIDs.add(pId);
                            }
                        }
                        break;
                    case BIBaseConstant.ROLE_TYPE.CUSTOM:
                        for (CustomRole customRole : cusRoles) {
                            if (ComparatorUtils.equals(customRole.getRolename(), roleName)) {
                                packageIDs.add(pId);
                            }
                        }
                        break;
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

    public void removeAuthPackage(BIPackageID packageID) {
        this.packagesAuth.remove(packageID);
    }
}
