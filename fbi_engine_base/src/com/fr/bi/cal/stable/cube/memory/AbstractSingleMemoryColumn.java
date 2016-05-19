package com.fr.bi.cal.stable.cube.memory;

import com.fr.bi.base.ValueConverter;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.file.ColumnFile;
import com.fr.bi.stable.file.IndexFile;
import com.fr.bi.stable.file.MemoryColumnFile;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.array.ICubeTableIndexReader;
import com.fr.bi.stable.index.CubeGenerator;
import com.fr.bi.stable.io.newio.NIOReader;
import com.fr.bi.stable.io.newio.NIOWriter;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.bi.stable.io.sortlist.ISortNIOReadList;
import com.fr.bi.stable.operation.sort.comp.ComparatorFacotry;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.finebi.cube.api.ICubeColumnIndexReader;
import com.fr.bi.stable.structure.collection.list.IntList;
import com.fr.bi.stable.structure.collection.map.CubeTreeMap;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by 小灰灰 on 2016/1/14.
 */
public abstract class AbstractSingleMemoryColumn<T> implements MemoryColumnFile<T> {
    private static final String UNSUPPORT = "MemoryColumn Not Support Group Key";
    private String fieldName;
    protected CubeTreeMap getter;
    protected Object getterLock = new Object();
    protected AnyIndexArray<T> detail;

    public AbstractSingleMemoryColumn() {
        initDetail();
    }

    protected abstract void initDetail();

    @Override

    public void createDetailDataWriter() {

    }

    @Override
    public void releaseDetailDataWriter() {

    }

    @Override
    public void addDataValue(int row, T value) {
        detail.add(row, value);
    }


    @Override
    public CubeGenerator createGroupIndexCreator(BIKey key, ValueConverter vc, int version, long rowCount) {
        throw new UnsupportedOperationException(UNSUPPORT);
    }

    @Override
    public CubeGenerator createGroupIndexCreator(SingleUserNIOReadManager manager, NIOReader sml, BIKey key, ValueConverter vc, int version, long rowCount, boolean needRelease) {
        throw new UnsupportedOperationException(UNSUPPORT);
    }

    @Override
    public NIOWriter createGroupWriter() {
        throw new UnsupportedOperationException(UNSUPPORT);
    }

    @Override
    public ISortNIOReadList createSortGroupReader(BIKey key, SingleUserNIOReadManager manager) {
        throw new UnsupportedOperationException(UNSUPPORT);
    }



    @Override
    public void deteleDetailFile() {

    }

    @Override
    public void copyDetailValue(String path, ColumnFile columnFile, SingleUserNIOReadManager manager, long rowCount) {

    }

    @Override
    public GroupValueIndex getIndexByRow(int row, SingleUserNIOReadManager manager) {
        return createGroupByType(null, null, null).getGroupIndex(new Object[]{detail.get(row)})[0];
    }

    @Override
    public ICubeColumnIndexReader createGroupByType(BIKey key, List<BITableSourceRelation> relationList, SingleUserNIOReadManager manager) {
        if (getter == null){
            synchronized (getterLock){
                if (getter == null){
                    getter = createGroupByType(ValueConverter.DEFAULT , ComparatorFacotry.createASCComparator());
                }
            }
        }
        return getter;
    }

    @Override
    public NIOReader createDetailNIOReader(SingleUserNIOReadManager manager) {
        throw new UnsupportedOperationException(UNSUPPORT);
    }

    public CubeTreeMap createGroupByType(ValueConverter converter, Comparator comparator) {
        if (getter == null){
            synchronized (getterLock){
                if (getter == null){
                    Map<Object, IntList> treeMap = new TreeMap<Object, IntList>();
                    for (int i = 0; i < detail.size(); i ++){
                        Object value = converter.result2Value(detail.get(i));
                        if (value != null) {
                            IntList list = treeMap.get(value);
                            if (list == null) {
                                list = new IntList();
                                treeMap.put(value, list);
                            }
                            list.add(i);
                        }
                    }
                    getter = new CubeTreeMap(comparator);
                    for (Map.Entry<Object, IntList> entry : treeMap.entrySet()){
                        getter.put(entry.getKey(), GVIFactory.createGroupVauleIndexBySimpleIndex(entry.getValue()));
                    }
                }
            }
        }
        return getter;
    }

    @Override
    public IndexFile getLinkIndexFile(BIKey key, List list) {
        throw new UnsupportedOperationException(UNSUPPORT);
    }

    @Override
    public void writeVersion(int version) {

    }

    @Override
    public void releaseGroupValueIndexCreator() {

    }

    @Override
    public boolean checkVersion(int relation_version) {
        return true;
    }

    @Override
    public NIOWriter<byte[]> createIndexWriter() {
        return null;
    }

    @Override
    public NIOWriter<byte[]> createNullWriter() {
        return null;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public NIOReader<byte[]> createIndexReader(BIKey key, SingleUserNIOReadManager manager) {
        throw new UnsupportedOperationException(UNSUPPORT);
    }

    @Override
    public NIOReader<byte[]> createNullIndexReader(BIKey key, SingleUserNIOReadManager manager) {
        throw new UnsupportedOperationException(UNSUPPORT);
    }

    @Override
    public void writeGroupCount(long groupCount) {

    }

    @Override
    public long getGroupCount(BIKey key) {
        return 0;
    }

    @Override
    public ICubeTableIndexReader getGroupValueIndexArrayReader(SingleUserNIOReadManager manager) {
        throw new UnsupportedOperationException(UNSUPPORT);
    }
}