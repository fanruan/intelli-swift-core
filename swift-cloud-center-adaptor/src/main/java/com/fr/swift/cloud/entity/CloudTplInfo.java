package com.fr.swift.cloud.entity;

import com.fr.third.javax.persistence.Column;
import com.fr.third.javax.persistence.Entity;
import com.fr.third.javax.persistence.Table;

import java.util.Date;

/**
 * @author yee
 * @date 2018-12-21
 */
@Entity
@Table(name = "cloud_tpl_info")
public class CloudTplInfo {
    @Column
    private Date time;
    @Column
    private String id;
    @Column
    private String tid;
    @Column
    private String tname;
    @Column
    private long cnums;
    @Column
    private long formnums;
    @Column
    private long sheetnums;
    @Column
    private long dsnums;
    @Column
    private long compformnums;
    @Column
    private long submitnums;
    @Column
    private boolean isfrozen;
    @Column
    private boolean isfoldtree;
    @Column
    private long widgetnums;
    @Column
    private long tsize;
    @Column
    private long imgsize;
    @Column
    private String execute0;
    @Column
    private String execute1;
    @Column
    private String execute2;
    @Column
    private String execute3;
    @Column
    private String execute4;
    @Column
    private long mem0;
    @Column
    private long mem1;
    @Column
    private long mem2;
    @Column
    private long mem3;
    @Column
    private long mem4;
    @Column
    private String sql0;
    @Column
    private String sql1;
    @Column
    private String sql2;
    @Column
    private String sql3;
    @Column
    private String sql4;

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getTname() {
        return tname;
    }

    public void setTname(String tname) {
        this.tname = tname;
    }

    public long getCnums() {
        return cnums;
    }

    public void setCnums(long cnums) {
        this.cnums = cnums;
    }

    public long getFormnums() {
        return formnums;
    }

    public void setFormnums(long formnums) {
        this.formnums = formnums;
    }

    public long getSheetnums() {
        return sheetnums;
    }

    public void setSheetnums(long sheetnums) {
        this.sheetnums = sheetnums;
    }

    public long getDsnums() {
        return dsnums;
    }

    public void setDsnums(long dsnums) {
        this.dsnums = dsnums;
    }

    public long getCompformnums() {
        return compformnums;
    }

    public void setCompformnums(long compformnums) {
        this.compformnums = compformnums;
    }

    public long getSubmitnums() {
        return submitnums;
    }

    public void setSubmitnums(long submitnums) {
        this.submitnums = submitnums;
    }

    public boolean isIsfrozen() {
        return isfrozen;
    }

    public void setIsfrozen(boolean isfrozen) {
        this.isfrozen = isfrozen;
    }

    public boolean isIsfoldtree() {
        return isfoldtree;
    }

    public void setIsfoldtree(boolean isfoldtree) {
        this.isfoldtree = isfoldtree;
    }

    public long getWidgetnums() {
        return widgetnums;
    }

    public void setWidgetnums(long widgetnums) {
        this.widgetnums = widgetnums;
    }

    public long getTsize() {
        return tsize;
    }

    public void setTsize(long tsize) {
        this.tsize = tsize;
    }

    public long getImgsize() {
        return imgsize;
    }

    public void setImgsize(long imgsize) {
        this.imgsize = imgsize;
    }

    public String getExecute0() {
        return execute0;
    }

    public void setExecute0(String execute0) {
        this.execute0 = execute0;
    }

    public String getExecute1() {
        return execute1;
    }

    public void setExecute1(String execute1) {
        this.execute1 = execute1;
    }

    public String getExecute2() {
        return execute2;
    }

    public void setExecute2(String execute2) {
        this.execute2 = execute2;
    }

    public String getExecute3() {
        return execute3;
    }

    public void setExecute3(String execute3) {
        this.execute3 = execute3;
    }

    public String getExecute4() {
        return execute4;
    }

    public void setExecute4(String execute4) {
        this.execute4 = execute4;
    }

    public long getMem0() {
        return mem0;
    }

    public void setMem0(long mem0) {
        this.mem0 = mem0;
    }

    public long getMem1() {
        return mem1;
    }

    public void setMem1(long mem1) {
        this.mem1 = mem1;
    }

    public long getMem2() {
        return mem2;
    }

    public void setMem2(long mem2) {
        this.mem2 = mem2;
    }

    public long getMem3() {
        return mem3;
    }

    public void setMem3(long mem3) {
        this.mem3 = mem3;
    }

    public long getMem4() {
        return mem4;
    }

    public void setMem4(long mem4) {
        this.mem4 = mem4;
    }

    public String getSql0() {
        return sql0;
    }

    public void setSql0(String sql0) {
        this.sql0 = sql0;
    }

    public String getSql1() {
        return sql1;
    }

    public void setSql1(String sql1) {
        this.sql1 = sql1;
    }

    public String getSql2() {
        return sql2;
    }

    public void setSql2(String sql2) {
        this.sql2 = sql2;
    }

    public String getSql3() {
        return sql3;
    }

    public void setSql3(String sql3) {
        this.sql3 = sql3;
    }

    public String getSql4() {
        return sql4;
    }

    public void setSql4(String sql4) {
        this.sql4 = sql4;
    }
}
