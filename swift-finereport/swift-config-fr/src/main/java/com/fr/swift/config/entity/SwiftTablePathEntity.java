package com.fr.swift.config.entity;

import com.fr.swift.config.bean.SwiftTablePathBean;
import com.fr.swift.config.entity.key.SwiftTablePathKey;
import com.fr.swift.converter.ObjectConverter;
import com.fr.third.javax.persistence.Column;
import com.fr.third.javax.persistence.Entity;
import com.fr.third.javax.persistence.Id;
import com.fr.third.javax.persistence.Table;


/**
 * @author yee
 * @date 2018/7/18
 */
@Entity
@Table(name = "fine_swift_table_path")
public class SwiftTablePathEntity implements ObjectConverter<SwiftTablePathBean> {
    @Id
    private SwiftTablePathKey id;
    @Column(name = "tablePath")
    private Integer tablePath;
    @Column(name = "lastPath")
    private Integer lastPath;
    @Column(name = "tmpDir")
    private Integer tmpDir;

    public SwiftTablePathEntity(SwiftTablePathKey id, Integer tablePath) {
        this.id = id;
        this.tablePath = tablePath;
        this.tmpDir = tablePath;
    }

    public SwiftTablePathEntity(String table, Integer tmpDir) {
        this.tmpDir = tmpDir;
        this.id = new SwiftTablePathKey(table);
    }

    public SwiftTablePathEntity() {
    }

    public SwiftTablePathEntity(SwiftTablePathBean swiftTablePathBean) {
        this.id = new SwiftTablePathKey(swiftTablePathBean.getTableKey(), swiftTablePathBean.getClusterId());
        this.lastPath = swiftTablePathBean.getLastPath();
        this.tablePath = swiftTablePathBean.getTablePath();
        this.tmpDir = swiftTablePathBean.getTmpDir();
    }


    public SwiftTablePathKey getId() {
        return id;
    }

    public void setId(SwiftTablePathKey id) {
        this.id = id;
    }

    public Integer getTablePath() {
        return tablePath;
    }

    public void setTablePath(Integer tablePath) {
        this.tablePath = tablePath;
    }

    public Integer getLastPath() {
        return lastPath;
    }

    public void setLastPath(Integer lastPath) {
        this.lastPath = lastPath;
    }

    public Integer getTmpDir() {
        return tmpDir;
    }

    public void setTmpDir(Integer tmpDir) {
        this.tmpDir = tmpDir;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SwiftTablePathEntity entity = (SwiftTablePathEntity) o;

        if (id != null ? !id.equals(entity.id) : entity.id != null) {
            return false;
        }
        if (tablePath != null ? !tablePath.equals(entity.tablePath) : entity.tablePath != null) {
            return false;
        }
        if (lastPath != null ? !lastPath.equals(entity.lastPath) : entity.lastPath != null) {
            return false;
        }
        return tmpDir != null ? tmpDir.equals(entity.tmpDir) : entity.tmpDir == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (tablePath != null ? tablePath.hashCode() : 0);
        result = 31 * result + (lastPath != null ? lastPath.hashCode() : 0);
        result = 31 * result + (tmpDir != null ? tmpDir.hashCode() : 0);
        return result;
    }


    @Override
    public SwiftTablePathBean convert() {
        return new SwiftTablePathBean(this.getId().getClusterId(),
                this.getId().getTableKey().getId(),
                this.getTablePath(), this.getLastPath(), this.getTmpDir());
    }
}
