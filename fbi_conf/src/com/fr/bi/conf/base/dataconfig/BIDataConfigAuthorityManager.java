package com.fr.bi.conf.base.dataconfig;

import com.fr.bi.conf.base.dataconfig.source.BIDataConfigAuthority;
import com.fr.general.ComparatorUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Young's on 2017/1/16.
 */
public class BIDataConfigAuthorityManager {
    private Set<BIDataConfigAuthority> dataConfigAuthorities = new HashSet<BIDataConfigAuthority>();

    public Set<BIDataConfigAuthority> getDataConfigAuthorities() {
        return dataConfigAuthorities;
    }

    public void setDataConfigAuthorities(Set<BIDataConfigAuthority> dataConfigAuthorities) {
        this.dataConfigAuthorities = dataConfigAuthorities;
    }

    public Set<BIDataConfigAuthority> getDataConfigAuthoritiesByRole(String roleName, int roleType) {
        Set<BIDataConfigAuthority> authorities = new HashSet<BIDataConfigAuthority>();
        Iterator<BIDataConfigAuthority> dataConfigAuthorityIterator = dataConfigAuthorities.iterator();
        while (dataConfigAuthorityIterator.hasNext()) {
            BIDataConfigAuthority authority = dataConfigAuthorityIterator.next();
            if (ComparatorUtils.equals(roleName, authority.getRoleName()) &&
                    ComparatorUtils.equals(roleType, authority.getRoleType())) {
                authorities.add(authority);
            }
        }
        return authorities;
    }

    public void saveSingleRoleDataConfigAuthorities(String roleName, int roleType, Set<BIDataConfigAuthority> authorities) {
        Set<BIDataConfigAuthority> oldAuthorities = getDataConfigAuthoritiesByRole(roleName, roleType);
        dataConfigAuthorities.removeAll(oldAuthorities);
        dataConfigAuthorities.addAll(authorities);
    }

    public void clear() {
        dataConfigAuthorities.clear();
    }
}
