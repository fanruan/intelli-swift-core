package com.finebi.cube.location.manager;

import com.finebi.common.resource.ResourceName;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.location.meta.BILocationPool;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * Created by wang on 2017/6/13.
 */
public interface BILocationProvider extends ILocationConverter{
    String XML_TAG = "BILocationProvider";
    void persistData(String xmlPath);

    BILocationPool getAccessLocationPool();
    BILocationPool getAccessLocationPool(Collection<ResourceName> nameCollection);

    BILocationPool getAccessLocationPool(ArrayList<String> parentKeys, ArrayList<String> fileNames);
    Set<String> updateLocationPool(BILocationPool input);
    boolean isEmpty();
}
