package com.fr.bi.stable.file;

import com.fr.bi.base.ValueConverter;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.engine.index.getter.DetailGetter;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.index.CubeGenerator;
import com.fr.bi.stable.io.newio.NIOReader;
import com.fr.bi.stable.io.newio.NIOWriter;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.bi.stable.io.sortlist.ISortNIOReadList;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.finebi.cube.api.ICubeColumnIndexReader;

import java.util.List;

public interface ColumnFile<T> extends IndexFile {

    void createDetailDataWriter();

    void releaseDetailDataWriter();

    void addDataValue(int row, T value);


    CubeGenerator createGroupIndexCreator(BIKey key,
                                                ValueConverter vc,
                                                int version, long rowCount);


    CubeGenerator createGroupIndexCreator(SingleUserNIOReadManager manager, NIOReader sml ,BIKey key,
                                                ValueConverter vc,
                                                int version, long rowCount, boolean needRelease);


    NIOWriter<T> createGroupWriter();

    IndexFile getLinkIndexFile(BIKey key, List<BITableSourceRelation> relations);

    ISortNIOReadList<T> createSortGroupReader(BIKey key, SingleUserNIOReadManager manager);

    DetailGetter<T> createDetailGetter(SingleUserNIOReadManager manager);

	void deteleDetailFile();

    void copyDetailValue(String path, ColumnFile columnFile, SingleUserNIOReadManager manager, long rowCount);

    GroupValueIndex getIndexByRow(int row, SingleUserNIOReadManager manager);

    ICubeColumnIndexReader createGroupByType(BIKey key, List<BITableSourceRelation> relationList, SingleUserNIOReadManager manager);

    NIOReader createDetailNIOReader(SingleUserNIOReadManager manager);
}