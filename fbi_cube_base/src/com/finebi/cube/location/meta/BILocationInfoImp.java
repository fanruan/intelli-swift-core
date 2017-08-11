package com.finebi.cube.location.meta;

import com.finebi.common.name.Name;
import com.finebi.common.resource.ResourceName;
import com.finebi.common.resource.ResourceNameImpl;

/**
 * Created by wang on 2017/6/20.
 */
public class BILocationInfoImp implements BILocationInfo {
    private ResourceName name;
    private String basePath;
    private String child;
    private long version;
    public BILocationInfoImp(String name,String locationPath,String child){
        this.name = new ResourceNameImpl(name);
        this.basePath = locationPath;
        this.child = child;
    }


    public String getBasePath() {
        return basePath;
    }

    @Override
    public String getChild() {
        return child;
    }

    @Override
    public Name getName() {
        return name;
    }

    @Override
    public ResourceName getResourceName() {
        return name;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public long version() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BILocationInfoImp)) {
            return false;
        }

        BILocationInfoImp that = (BILocationInfoImp) o;

        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
