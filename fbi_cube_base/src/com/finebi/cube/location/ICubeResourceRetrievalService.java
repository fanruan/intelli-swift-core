package com.finebi.cube.location;

import com.finebi.cube.exception.BICubeResourceAbsentException;
import com.finebi.cube.exception.IllegalCubeResourceLocationException;
import com.finebi.cube.structure.BICubeTablePath;
import com.finebi.cube.structure.ITableKey;
import com.finebi.cube.structure.column.BIColumnKey;
import com.fr.bi.stable.exception.BITablePathEmptyException;

import java.net.URISyntaxException;

/**
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
