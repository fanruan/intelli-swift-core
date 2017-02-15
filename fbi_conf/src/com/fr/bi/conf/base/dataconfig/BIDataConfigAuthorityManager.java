package com.fr.bi.conf.base.dataconfig;

import com.fr.bi.conf.base.dataconfig.source.BIDataConfigAuthority;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.fs.base.entity.CompanyRole;
import com.fr.fs.base.entity.CustomRole;
import com.fr.fs.control.CompanyRoleControl;
import com.fr.fs.control.CustomRoleControl;
import com.fr.fs.control.DepartmentControl;
import com.fr.fs.control.PostControl;
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
        //按角色先清掉
        for (BIDataConfigAuthority authority : dataConfigAuthorities) {
            clearByRole(authority.getRoleName(), authority.getRoleType());
        }
        for (BIDataConfigAuthority authority : dataConfigAuthorities) {
            if (authority.getId() != null) {
                this.dataConfigAuthorities.add(authority);
            }
        }
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

    public Set<BIDataConfigAuthority> getDataConfigAuthoritiesByUserId(long userId) throws Exception {
        Set<CompanyRole> comRoles = CompanyRoleControl.getInstance().getCompanyRoleSet(userId);
        Set<CustomRole> cusRoles = CustomRoleControl.getInstance().getCustomRoleSet(userId);
        Set<BIDataConfigAuthority> authorities = new HashSet<BIDataConfigAuthority>();
        for (BIDataConfigAuthority authority : dataConfigAuthorities) {
            String roleName = authority.getRoleName();
            switch (authority.getRoleType()) {
                case BIBaseConstant.ROLE_TYPE.COMPANY:
                    for (CompanyRole role : comRoles) {
                        String dName = DepartmentControl.getInstance().getDepartmentShowName(role.getDepartmentId(), ",");
                        if (role.getPostId() != 0) {
                            String pName = PostControl.getInstance().getPostName(role.getPostId());
                            dName = dName + "," + pName;
                        }
                        if (ComparatorUtils.equals(dName, roleName)) {
                            authorities.add(authority);
                        }
                    }
                    break;
                case BIBaseConstant.ROLE_TYPE.CUSTOM:
                    for (CustomRole customRole : cusRoles) {
                        if (ComparatorUtils.equals(customRole.getRolename(), roleName)) {
                            authorities.add(authority);
                        }
                    }
                    break;
            }
        }
        return authorities;
    }

    public void saveSingleRoleDataConfigAuthorities(String roleName, int roleType, Set<BIDataConfigAuthority> authorities) {
        Set<BIDataConfigAuthority> oldAuthorities = getDataConfigAuthoritiesByRole(roleName, roleType);
        dataConfigAuthorities.removeAll(oldAuthorities);
        dataConfigAuthorities.addAll(authorities);
    }

    private void clearByRole(String roleName, int roleType) {
        Set<BIDataConfigAuthority> need2Remove = new HashSet<BIDataConfigAuthority>();
        for (BIDataConfigAuthority authority : this.dataConfigAuthorities) {
            if (ComparatorUtils.equals(roleName, authority.getRoleName()) &&
                    ComparatorUtils.equals(roleType, authority.getRoleType())) {
                need2Remove.add(authority);
            }
        }
        this.dataConfigAuthorities.removeAll(need2Remove);
    }

    public void clear() {
        dataConfigAuthorities.clear();
    }
}
