package com.fr.bi.conf.provider;

import com.fr.bi.conf.base.auth.data.BIPackageAuthority;
import com.fr.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by wuk on 16/4/25.
 */
public interface BIAuthorityManageProvider {
    String XML_TAG = "BIPackageAuthManageProvider";

    void savePackageAuth(BIPackageID packageID, List<BIPackageAuthority> auth, long userId);

    Map<BIPackageID, List<BIPackageAuthority>> getPackagesAuth(long userId);

    List<BIPackageAuthority> getPackageAuthByID(BIPackageID packageID, long userId);

    JSONObject createJSON(long userId) throws Exception;

    void clear(long userId);

    @Deprecated
    void persistData(long userId);
}
