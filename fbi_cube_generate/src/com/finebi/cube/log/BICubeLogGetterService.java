package com.finebi.cube.log;

import java.util.List;
import java.util.Map;

/**
 * Created by neil on 2017/4/12.
 */
public interface BICubeLogGetterService {

    List<Map<String,Object>> getTableTransportLogInfo();

    List<Map<String,Object>> getTableFieldIndexLogInfo();

    List<Map<String,Object>> getRelationIndexLogInfo();

    int getAllTableNeedTranslate();

    int getAllFieldNeedIndex();

    int getAllRelationNeedGenerate();

    int getTableAlreadyTranslate();

    int getFieldAlreadyIndex();

    int getRelationAlreadyGenerate();

}
