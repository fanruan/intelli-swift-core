package com.fr.bi.cal.stable.cube.file;

import com.finebi.cube.api.ICubeColumnDetailGetter;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.cal.stable.index.file.field.CubeDoubleFile;
import com.fr.bi.cal.stable.tableindex.detailgetter.NormalDetailGetter;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.bi.stable.io.sortlist.ISortNIOReadList;
import com.fr.bi.stable.io.sortlist.SortNIOReadDoubleList;
import com.fr.bi.stable.operation.sort.comp.ComparatorFacotry;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.bi.stable.utils.file.BIPathUtils;

import java.util.Comparator;

public class DoubleColumnFile extends AbstractSingleColumnFile<Double> {

    public DoubleColumnFile(String path) {
        super(path);
    }

    @Override
    protected CubeDoubleFile createDetailFile() {
        return BIFileUtils.createFile(this, getDetailFieldName(), CubeDoubleFile.class, BIPathUtils.createSingleFieldDetailPath(path));
    }

    @Override
    protected CubeDoubleFile createGroupFile() {
        return BIFileUtils.createFile(this, getGroupFieldName(), CubeDoubleFile.class, BIPathUtils.createSingleFieldGroupPath(path));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Comparator<Double> getComparator() {
        return ComparatorFacotry.createASCComparator();
    }

    @Override
    public ISortNIOReadList<Double> createSortGroupReader(BIKey key,
                                                          SingleUserNIOReadManager manager) {
        return new SortNIOReadDoubleList(createGroupReader(manager), getGroupCount(key));
    }

    @Override
    public ICubeColumnDetailGetter createDetailGetter(SingleUserNIOReadManager manager) {
        return new NormalDetailGetter(createDetailReader(manager));
    }


}