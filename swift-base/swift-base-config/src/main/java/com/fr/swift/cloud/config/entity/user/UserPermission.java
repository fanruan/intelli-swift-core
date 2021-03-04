package com.fr.swift.cloud.config.entity.user;

/**
 * @author Hoky
 * @date 2020/10/22
 */
public enum UserPermission {
    ADMIN(true, true),
    NORMAL(true, false);

    private final boolean viewable;
    private final boolean modifiable;

    UserPermission(boolean viewable, boolean modifiable) {
        this.viewable = viewable;
        this.modifiable = modifiable;
    }

}
