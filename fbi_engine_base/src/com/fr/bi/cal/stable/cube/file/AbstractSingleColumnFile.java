package com.fr.bi.cal.stable.cube.file;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeColumnDetailGetter;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.base.ValueConverter;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.cal.stable.index.GroupIndexCreator;
import com.fr.bi.cal.stable.index.file.GroupLengthFile;
import com.fr.bi.cal.stable.index.file.field.AbstractNIOCubeFile;
import com.fr.bi.cal.stable.io.NIOReadGroupMap;
import com.fr.bi.stable.file.ColumnFile;
import com.fr.bi.stable.file.IndexFile;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.index.CubeGenerator;
import com.fr.bi.stable.io.newio.NIOReader;
import com.fr.bi.stable.io.newio.NIOWriter;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.bi.stable.io.sortlist.ISortNIOReadList;
import com.fr.bi.stable.structure.array.ArrayKey;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.bi.stable.utils.file.BIPathUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractSingleColumnFile<T> extends LinkIndexFile implements ColumnFile<T> {

    private final static String DETAIL_NAME = "detail";
    private final static String GROUP_NAME = "group";
    private final static String LENGTH_NAME = "lengthFile";

    protected AbstractNIOCubeFile<T> detail;
    protected AbstractNIOCubeFile<T> group;

    private GroupLengthFile lengthFile;

    private Map<ArrayKey<BITableSourceRelation>, IndexFile> relationMap = new ConcurrentHashMap<ArrayKey<BITableSourceRelation>, IndexFile>();

    private Map<ArrayKey<BITableSourceRelation>, ICubeColumnIndexReader> getterMap = new ConcurrentHashMap<ArrayKey<BITableSourceRelation>, ICubeColumnIndexReader>();

    private Object getterLock = new Object();

    public AbstractSingleColumnFile(String path) {
        super(path);
    }

    protected String getDetailFieldName() {
        return DETAIL_NAME;
    }

    protected String getGroupFieldName() {
        return GROUP_NAME;
    }

    protected abstract AbstractNIOCubeFile<T> createDetailFile();

    protected abstract AbstractNIOCubeFile<T> createGroupFile();

    protected abstract Comparator<T> getComparator();

    protected GroupLengthFile createLengthFile() {
        return BIFileUtils.createFile(this, LENGTH_NAME, GroupLengthFile.class, BIPathUtils.createGroupLengthPath(path));
    }


    @Override
    public void createDetailDataWriter() {
        createDetailFile().createNIOWriter();
    }

    @Override
    public void releaseDetailDataWriter() {
        if (detail != null) {
            detail.clearWriter();
        }
    }

    @Override
    public void addDataValue(int row, T value) {
        detail.createNIOWriter().add(row, value);
    }


    @Override
    public void writeGroupCount(long groupCount) {
        createLengthFile().write(groupCount);
    }

    @Override
    public void releaseGroupValueIndexCreator() {
        super.releaseGroupValueIndexCreator();
        if (group != null) {
            group.clearWriter();
        }

    }

    @Override
    public boolean checkVersion(int relation_version) {
        return super.checkVersion(relation_version);
    }

    @Override
    public NIOWriter<T> createGroupWriter() {
        return createGroupFile().createNIOWriter();
    }


    @Override
    public CubeGenerator createGroupIndexCreator(SingleUserNIOReadManager manager, NIOReader sml,BIKey key, ValueConverter vc, int version, long rowCount, boolean needRelease) {
        GroupIndexCreator creator = new GroupIndexCreator(
                manager,
                sml,
                getComparator(), key.getKey(), this,
                vc,
                rowCount, version, needRelease);
        dealWithCreator(creator);
        return creator;
    }

    @Override
    public ISortNIOReadList<T> createSortGroupReader(BIKey key, SingleUserNIOReadManager manager) {
        return null;
    }

    @Override
    public ICubeColumnDetailGetter createDetailGetter(SingleUserNIOReadManager manager) {
        return null;
    }

    @Override
    public CubeGenerator createGroupIndexCreator(BIKey key,
                                                       ValueConverter vc,
                                                       int version, long rowCount) {
        AbstractNIOCubeFile<T> detail = createDetailFile();
        SingleUserNIOReadManager manager = new SingleUserNIOReadManager(-1L);
        NIOReader sml = detail.createNIOReader(manager);
        return createGroupIndexCreator(manager, sml, key, vc, version, rowCount, true);
    }

    protected void dealWithCreator(GroupIndexCreator creator) {
    }

    private IndexFile getBasicIndexFile(ArrayKey<BITableSourceRelation> key) {
        IndexFile ifile = relationMap.get(key);
        if (ifile != null) {
            return ifile;
        }
        synchronized (relationMap) {
            ifile = relationMap.get(key);
            if (ifile == null) {
                ifile = new LinkIndexFile(BIPathUtils.createColumnLinkIndexPath(path, key));
                relationMap.put(key, ifile);
            }
            return ifile;
        }
    }

    @Override
    public IndexFile getLinkIndexFile(BIKey columnKey, List<BITableSourceRelation> relations) {
        ArrayKey<BITableSourceRelation> key = new ArrayKey<BITableSourceRelation>(relations.toArray(new BITableSourceRelation[relations.size()]));
        return getBasicIndexFile(key);
    }

    @Override
    public long getGroupCount(BIKey key) {
        return Long.parseLong(createLengthFile().read());
    }

    public NIOReader<T> createGroupReader(SingleUserNIOReadManager manager) {
        return createGroupFile().createNIOReader(manager);
    }

    protected NIOReader<T> createDetailReader(SingleUserNIOReadManager manager) {
        return createDetailFile().createNIOReader(manager);
    }


    @Override
    public void deteleDetailFile() {
        File f = createDetailFile().createFile();
        File temp = f;
        int i = 0;
        while (temp.exists()) {
            temp.delete();
            temp = new File(f.getAbsolutePath() + "_" + (++i));
        }

    }

    @Override
    public void copyDetailValue(String path, ColumnFile columnFile, SingleUserNIOReadManager manager, long rowCount) {
        File newFile = new File(path);
        File oldFile = new File(this.path);
        String field = BIPathUtils.createSingleFieldDetailPath(this.path);
        BIFileUtils.copyFile(BIPathUtils.createDetailPath(field), oldFile, newFile);
    }

    @Override
    public GroupValueIndex getIndexByRow(int row, SingleUserNIOReadManager manager) {
        Object v = createDetailGetter(manager).getValue(row);
        ICubeColumnIndexReader map = createGroupByType(BIKey.DEFAULT, new ArrayList<BITableSourceRelation>(), manager);
        Object [] keys = map.createKey(1);
        keys[0] = v;
        return map.getGroupIndex(keys)[0];
    }


    @Override
    public ICubeColumnIndexReader createGroupByType(BIKey key, List<BITableSourceRelation> relationList, SingleUserNIOReadManager manager) {
        ArrayKey<BITableSourceRelation> relationKey = new ArrayKey<BITableSourceRelation>(relationList.toArray(new BITableSourceRelation[relationList.size()]));
        ICubeColumnIndexReader getter = getterMap.get(relationKey);
        if (getter == null){
            synchronized (getterLock){
                getter = getterMap.get(relationKey);
                if (getter != null){
                    return getter;
                }
                getter = new NIOReadGroupMap(createSortGroupReader(key, manager),
                        relationList == null || relationList.isEmpty() ? getGroupValueIndexArrayReader(manager) : getLinkIndexFile(key, relationList).getGroupValueIndexArrayReader(manager));
                getterMap.put(relationKey, getter);
            }
        }
        return getter;
    }

    @Override
    public NIOReader createDetailNIOReader(SingleUserNIOReadManager manager) {
        return createDetailFile().createNIOReader(manager);
    }
}