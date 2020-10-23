package com.fr.swift.config.entity;

import com.fr.swift.config.entity.user.UserPermission;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.source.core.MD5Utils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Hoky
 * @date 2020/10/22
 */
@Entity
@Table(name = "fine_swift_userinfo",uniqueConstraints = {@UniqueConstraint(columnNames="username")})
public class SwiftUserInfo implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "swiftSchema")
    @Enumerated(EnumType.STRING)
    private SwiftDatabase swiftSchema;

    @Column(name = "createTime")
    private Date createTime;

    @Column(name = "authority")
    @Enumerated(EnumType.STRING)
    private UserPermission authority;

    public SwiftUserInfo() {

    }

    public SwiftUserInfo(String username, String password, UserPermission authority) {
        this.username = username;
        this.password = MD5Utils.getMD5String(new String[]{password});
        this.swiftSchema = SwiftDatabase.CUBE;
        this.authority = authority;
        this.createTime = new Date(System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return "SwiftUserInfo{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", swiftSchema='" + swiftSchema + '\'' +
                ", createTime=" + createTime +
                ", authority=" + authority +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public SwiftDatabase getSwiftSchema() {
        return swiftSchema;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public UserPermission getAuthority() {
        return authority;
    }
}
