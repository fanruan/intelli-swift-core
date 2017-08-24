package com.finebi.cube.location.provider;

import com.finebi.common.resource.ResourceName;
import com.finebi.cube.location.meta.BILocationPool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by wang on 2017/6/13.
 */
public interface BILocationProvider extends ILocationConverter {
    String XML_TAG = "BILocationProvider";

    void persistData(String xmlPath);

    BILocationPool getAccessLocationPool();

    BILocationPool getAccessLocationPool(Collection<ResourceName> nameCollection);

    /**
     * @param parentKeys 各级别的父亲目录 user table column等 一次只能是一张表
     *                   examples
     *                   [-999,TableA,FiledA]
     *                   [-999,TableA,relation,FiledB]
     * @param fileNames  文件类型  detail ,index count 等
     * @return
     */
    BILocationPool getAccessLocationPool(List<String> parentKeys, List<String> fileNames);

    Set<String> updateLocationPool(BILocationPool input);

    boolean isEmpty();

}
