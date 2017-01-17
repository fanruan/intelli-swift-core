package com.fr.bi.conf.provider;

import com.fr.bi.conf.base.dataconfig.source.BIDataConfigAuthority;

import java.util.Set;

/**
 * Created by Young's on 2017/1/17.
 */
public interface BIDataConfigAuthorityProvider {
    String XML_TAG = "BIDataConfigAuthorityProvider";

    Set<BIDataConfigAuthority> getAllDataConfigAuthorities();

    Set<BIDataConfigAuthority> getDataConfigAuthoritiesByRole(String roleName, int roleType);

    void clear();

    @Deprecated
    void persistData(long userId);
}
