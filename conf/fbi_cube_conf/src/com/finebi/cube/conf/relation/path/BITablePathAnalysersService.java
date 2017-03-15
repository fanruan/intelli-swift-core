package com.finebi.cube.conf.relation.path;

import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.bi.stable.exception.BITablePathConfusionException;
import com.fr.bi.stable.exception.BITableRelationConfusionException;
import com.finebi.cube.relation.BITablePair;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableRelationPath;

import java.util.Set;

/**
 * 对于两表间的路径的分析器
 * Created by Connery on 2016/1/13.
 */
public interface BITablePathAnalysersService {
    /**
     * 获得Tabled对中间的路径，从First表到Second表的
     * 全部路径
     *
     * @param biTablePair
     * @return
     * @throws BITableAbsentException
     * @throws BITableRelationConfusionException
     * @throws BITablePathConfusionException
     */
    Set<BITableRelationPath> analysisAllPath(BITablePair biTablePair)
            throws BITableAbsentException, BITableRelationConfusionException, BITablePathConfusionException;

    /**
     * 添加一个管理到分析器中
     *
     * @param biTableRelation
     */
    void registerBITableRelation(BITableRelation biTableRelation);

    void removeBITableRelation(BITableRelation biTableRelation);

    boolean contain(BITableRelation biTableRelation);

    void clear();
}