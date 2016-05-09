package com.finebi.cube.location;

import com.finebi.cube.exception.BICubeResourceAbsentException;
import com.finebi.cube.exception.IllegalCubeResourceLocationException;
import com.finebi.cube.structure.BICubeTablePath;
import com.finebi.cube.structure.ITableKey;
import com.finebi.cube.structure.column.BIColumnKey;
import com.fr.bi.stable.exception.BITablePathEmptyException;

import java.net.URISyntaxException;

/**
 * Cube对象与Cube数据的桥梁。
 * Cube对象代表的是Cube的结构和相应的操作。
 * 而具体数据存在硬盘或者内存中。
 * <p/>
 * 当需要读写Cube时候，通过cube对象计算出唯一的
 * Location。Cube数据通过解析Location获得相应的Read和
 * Write接口。
 * <p/>
 * 这里负责的就是如何通过cube对象计算出Location。
 * <p/>
 * Cube对象详见structure包
 * Cube数据详见data包，主要接口是ICubeResourceDiscovery
 * <p/>
 * This class created on 2016/3/8.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeResourceRetrievalService {
    String RELATION_NAME = "relation";

    ICubeResourceLocation retrieveResource(String sourceTag) throws BICubeResourceAbsentException;

    ICubeResourceLocation retrieveResource(ITableKey table) throws BICubeResourceAbsentException;

    /**
     * 获得关联的资源位置。
     *
     * @param tableRelationPath 关联路径
     * @return 资源位置
     * @throws IllegalCubeResourceLocationException cube资源Location异常
     * @throws BITablePathEmptyException            关联路径为空
     */
    ICubeResourceLocation retrieveResource(ITableKey tableSource, BICubeTablePath tableRelationPath) throws BICubeResourceAbsentException, BITablePathEmptyException;

    ICubeResourceLocation retrieveResource(ITableKey table, BIColumnKey field) throws BICubeResourceAbsentException, URISyntaxException;

    ICubeResourceLocation retrieveResource(ITableKey table, BIColumnKey field, BICubeTablePath tableRelationPath) throws BICubeResourceAbsentException, URISyntaxException;
}
