package com.fr.bi.stable.engine.index;

import com.fr.bi.base.key.BIKey;
import com.fr.bi.common.inter.Delete;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.BICubeFieldSource;
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
import java.util.TreeSet;

public interface BITableCubeFile extends Delete {

    void mkDir();

    void writeMain(List<String> columnList);

    void writeVersionCheck();

    void writeTableGenerateVersion(int version);

    int getTableVersion();

    void writeRowCount(long rowCount);

    void writeLastTime();

    void writeRemovedLine(TreeSet<Integer> removedLine);

    BICubeFieldSource[] getBIField();

    void createDetailDataWriter();

    void releaseDetailDataWriter();

    void addDataValue(BIDataValue v);

    boolean checkRelationVersion(List<BITableSourceRelation> relations,
                                        int relation_version);

    boolean checkRelationVersion(BIKey key, List<BITableSourceRelation> relations,
                                        int relation_version);

    IndexFile getLinkIndexFile(List<BITableSourceRelation> relations);

    boolean checkCubeVersion();

    IndexFile getLinkIndexFile(BIKey key, List<BITableSourceRelation> relations);

    Date getCubeLastTime();

    IntList getRemoveList(SingleUserNIOReadManager manager);

    Long getGroupCount(BIKey key);

    int getRowCount();

    void copyDetailValue(BITableCubeFile cube, SingleUserNIOReadManager manager, long rowCount);

    /**
     * 获取字符串类型的按行的index
     * @param key
     * @param manager
     * @return
     */
    GroupValueIndex getNullGroupValueIndex(BIKey key, SingleUserNIOReadManager manager);

    ICubeTableIndexReader getGroupValueIndexArrayReader(BIKey key, SingleUserNIOReadManager manager);

    DetailGetter createDetailGetter(BIKey key, SingleUserNIOReadManager manager);

    ICubeColumnIndexReader createGroupByType(BIKey key, List<BITableSourceRelation> relationList, SingleUserNIOReadManager manager);

    ICubeTableIndexReader getBasicGroupValueIndexArrayReader(List<BITableSourceRelation> relationList, SingleUserNIOReadManager manager);

    GroupValueIndex getIndexByRow(BIKey key, int row, SingleUserNIOReadManager manager);

}