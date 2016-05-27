package com.fr.bi.conf.report.filter;

import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.base.provider.ParseJSONWithUID;
import com.fr.bi.common.BICoreService;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.json.JSONCreator;

/**
 * 过滤的父接口
 * Created by GUY on 2015/4/20.
 */
public interface FieldFilter extends ParseJSONWithUID, JSONCreator,BICoreService {

    /**
     * 创建维度到指标的过滤条件
     *
     *
     * @param target
     * @param loader loader 对象
     * @return 分组索引
     */
     GroupValueIndex createFilterIndex(DimensionCalculator dimension, CubeTableSource target, ICubeDataLoader loader, long userId);
}