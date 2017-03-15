package com.fr.bi.conf.provider;

import com.finebi.cube.conf.pack.data.BIPackageID;
import com.fr.bi.conf.base.auth.data.BIPackageAuthority;
import com.fr.fs.base.entity.CompanyRole;
import com.fr.fs.base.entity.CustomRole;
import com.fr.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by kary on 16/4/25.
 */
public interface BIAuthorityManageProvider {
    String XML_TAG = "BIPackageAuthManageProvider";

    void savePackageAuth(BIPackageID packageID, List<BIPackageAuthority> auth, long userId);

    Map<BIPackageID, List<BIPackageAuthority>> getPackagesAuth(long userId);

    List<BIPackageAuthority> getPackageAuthByID(BIPackageID packageID, long userId);

    List<BIPackageAuthority> getPackageAuthBySession(BIPackageID packageID,List<CompanyRole> comRoles, List<CustomRole> cusRoles);

    List<BIPackageID> getAuthPackagesByUser(long userId);

    List<BIPackageID> getAuthPackagesBySession(List<CompanyRole> comRoles, List<CustomRole> cusRoles);

    boolean hasAuthPackageByUser(long userId, String sessionId);

    JSONObject createJSON(long userId) throws Exception;

    void clear(long userId);

    void removeAuthPackage(BIPackageID packageID);

    long getAuthVersion();

    @Deprecated
    void persistData(long userId);
}
