package com.fr.bi.stable.engine.index;

import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.engine.index.getter.DetailGetter;
import com.fr.bi.stable.file.IndexFile;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.array.ICubeTableIndexReader;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.api.ICubeColumnIndexReader;
import com.fr.bi.stable.structure.collection.list.IntList;

import java.util.Date;
import java.util.List;

/**
 * Created by Connery on 2016/3/1.
 */
public interface ITableCubeGetter {
    int getTableVersion();

    ICubeFieldSource[] getBIField();

    int getRowCount();

    IndexFile getLinkIndexFile(BIKey key, List<BITableSourceRelation> relations);

    Date getCubeLastTime();

    IntList getRemoveList(SingleUserNIOReadManager manager);

    Long getGroupCount(BIKey key);

    /**
     * 获取字符串类型的按行的index
     *
     * @param key
     * @return
     */
    GroupValueIndex getNullGroupValueIndex(BIKey key);

    ICubeTableIndexReader getGroupValueIndexArrayReader(BIKey key);

    DetailGetter getDetailGetter(BIKey key);

    ICubeColumnIndexReader getGroupByType(BIKey key, List<BITableSourceRelation> relationList);

    ICubeTableIndexReader getBasicGroupValueIndexArrayReader(List<BITableSourceRelation> relationList);

    GroupValueIndex getIndexByRow(BIKey key, int row);
}
