package com.finebi.base.data;


import com.finebi.base.data.xml.item.XmlItem;
import com.finebi.base.data.xml.item.XmlListItem;

import java.util.List;

/**
 * Created by andrew_asa on 2017/10/11.
 */
public class Group {

    @XmlItem
    private String id;

    @XmlItem
    private String name;

    @XmlItem
    private long initTime;

    @XmlItem
    private long updateTime;

    @XmlItem
    private long creator;

    @XmlListItem
    private List<String> packages;

    public Group() {

    }

    public String getId() {

        return id;
    }

    public String getName() {

        return name;
    }

    public long getInitTime() {

        return initTime;
    }

    public long getUpdateTime() {

        return updateTime;
    }

    public long getCreator() {

        return creator;
    }

    public List<String> getPackages() {

        return packages;
    }

    public void setId(String id) {

        this.id = id;
    }

    public void setName(String name) {

        this.name = name;
    }

    public void setInitTime(long initTime) {

        this.initTime = initTime;
    }

    public void setUpdateTime(long updateTime) {

        this.updateTime = updateTime;
    }

    public void setCreator(long creator) {

        this.creator = creator;
    }

    public void setPackages(List<String> packages) {

        this.packages = packages;
    }


}
