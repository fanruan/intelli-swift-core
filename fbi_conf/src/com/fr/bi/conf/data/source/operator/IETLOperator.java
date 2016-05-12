package com.fr.bi.conf.data.source.operator;

import com.fr.bi.common.BICoreService;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.DBTable;
import com.fr.bi.stable.data.source.ITableSource;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.json.JSONTransform;
import com.fr.stable.xml.XMLable;

import java.util.List;

/**
 * Created by GUY on 2015/3/5.
 */
public interface IETLOperator extends XMLable, JSONTransform, BICoreService {

    String xmlTag();

//    String createMD5Value();

    /**
     * 对原始表处理
     *
     * @param tables 表数组
     * @return 处理过的表
     */
    DBTable getBITable(DBTable[] tables);

    boolean isAddColumnOprator();

    int writeSimpleIndex(Traversal<BIDataValue> travel, List<? extends ITableSource> parents, ICubeDataLoader loader);

    int writePartIndex(Traversal<BIDataValue> travel, List<? extends ITableSource> parents, ICubeDataLoader loader, int startCol, int start, int end);
}