package com.fr.bi.cal.stable.cube.file;

import com.fr.bi.base.key.BIKey;
import com.fr.bi.cal.stable.index.file.field.AbstractNIOCubeFile;
import com.fr.bi.cal.stable.index.file.field.CubeLongFile;
import com.fr.bi.cal.stable.tableindex.detailgetter.NormalDetailGetter;
import com.fr.bi.stable.engine.index.getter.DetailGetter;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.bi.stable.io.sortlist.ISortNIOReadList;
import com.fr.bi.stable.io.sortlist.SortNIOReadLongList;
import com.fr.bi.stable.operation.sort.comp.ComparatorFacotry;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.bi.stable.utils.file.BIPathUtils;

import java.util.Comparator;


public class LongColumnFile extends AbstractSingleColumnFile<Long> {

    public LongColumnFile(String path) {
        super(path);
    }


    @Override
    protected AbstractNIOCubeFile<Long> createDetailFile() {

        return BIFileUtils.createFile(this, getDetailFieldName(), CubeLongFile.class, BIPathUtils.createSingleFieldDetailPath(path));
    }


    @Override
    protected CubeLongFile createGroupFile() {

        return BIFileUtils.createFile(this, getGroupFieldName(), CubeLongFile.class, BIPathUtils.createSingleFieldGroupPath(path));

    }


    @Override
    protected Comparator<Long> getComparator() {
        return ComparatorFacotry.LONG_ASC;
    }


    @Override
    public ISortNIOReadList<Long> createSortGroupReader(BIKey key,
                                                        SingleUserNIOReadManager manager) {
        return new SortNIOReadLongList(createGroupReader(manager), getGroupCount(key));
    }


    @Override
    public DetailGetter createDetailGetter(SingleUserNIOReadManager manager) {

        return new NormalDetailGetter(createDetailReader(manager));
    }

}