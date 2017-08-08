package com.finebi.cube.location.meta;

import com.finebi.common.resource.DefaultResourcePool;
import com.finebi.common.resource.ResourceName;
import com.finebi.cube.common.log.BILoggerFactory;

import java.util.Collection;

/**
 * Created by wang on 2017/6/20.
 */
public class BILocationPoolImp extends DefaultResourcePool<ResourceName, BILocationInfo> implements BILocationPool {

    @Override
    public void mergePool(BILocationPool input) {
        for (BILocationInfo item : input.getAllItems()){
            updateResourceItem(item.getResourceName(),item);
        }
    }

    @Override
    public BILocationPool getBILocationPool() {
        BILocationPool copyPool = new BILocationPoolImp();
        for (BILocationInfo item : getAllItems()){
            try {
                copyPool.addResourceItem(item.getResourceName(), (BILocationInfo) item.clone());
            } catch (CloneNotSupportedException e) {
                BILoggerFactory.getLogger(BILocationPoolImp.class).error(e.getMessage(),e);
            }
        }
        return copyPool;
    }

    @Override
    public BILocationPool getBILocationPool(Collection<ResourceName> nameCollection) {
        BILocationPool copyPool = new BILocationPoolImp();
        for (ResourceName name : nameCollection){
            try {
                copyPool.addResourceItem(name, (BILocationInfo) getResourceItem(name.value()).clone());
            } catch (Exception e) {
                BILoggerFactory.getLogger(BILocationPoolImp.class).error(e.getMessage(),e);
            }
        }
        return copyPool;
    }
}
