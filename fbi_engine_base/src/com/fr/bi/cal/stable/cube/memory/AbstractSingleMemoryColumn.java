package com.fr.bi.cal.stable.cube.memory;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.base.ValueConverter;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.file.ColumnFile;
import com.fr.bi.stable.file.IndexFile;
import com.fr.bi.stable.file.MemoryColumnFile;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.array.ICubeTableIndexReader;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;
import com.fr.bi.stable.index.CubeGenerator;
import com.fr.bi.stable.io.newio.NIOConstant;
import com.fr.bi.stable.io.newio.NIOReader;
import com.fr.bi.stable.io.newio.NIOWriter;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.bi.stable.io.sortlist.ISortNIOReadList;
import com.fr.bi.stable.structure.collection.CubeIndexGetterWithNullValue;
import com.fr.bi.stable.structure.collection.map.CubeTreeMap;
import com.fr.bi.stable.utils.BICollectionUtils;
import com.fr.stable.StringUtils;
import com.fr.stable.collections.array.IntArray;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by 小灰灰 on 2016/1/14.
 */
public abstract class AbstractSingleMemoryColumn<T> implements MemoryColumnFile<T> {
    private static final String UNSUPPORT = "MemoryColumn Not Support Group Key";
    private String fieldName;
    protected ICubeColumnIndexReader getter;
    protected Object getterLock = new Object();
    protected AnyIndexArray<T> detail;
    protected AnyIndexArray<Integer> groupPosition;


    public AbstractSingleMemoryColumn() {
        initDetail();
    }

    protected abstract void initDetail();

    protected void initGroupPosition(CubeTreeMap map) {
        groupPosition = new AnyIndexArrayCreator<Integer>().create();
        Iterator<GroupValueIndex> it = map.values().iterator();
        int position = 0;
        while (it.hasNext()) {
            final int p = position;
            it.next().Traversal(new SingleRowTraversalAction() {
                @Override
                public void actionPerformed(int row) {
                    groupPosition.add(row, p);
                }
            });
            position++;
        }
    }

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
        return this.initGetter(new IndexKey(StringUtils.EMPTY)).getGroupIndex(new Object[]{detail.get(row)})[0];
    }

    @Override
    public ICubeColumnIndexReader createGroupByType(BIKey key, List<BITableSourceRelation> relationList, SingleUserNIOReadManager manager) {
        return this.initGetter(key);
    }

    private ICubeColumnIndexReader initGetter(BIKey key) {
        if (getter == null) {
            synchronized (getterLock) {
                if (getter == null) {
                    getter = createGroupByType(key, ValueConverter.DEFAULT, BIBaseConstant.COMPARATOR.COMPARABLE.ASC);
                }
            }
        }
        return getter;
    }

    @Override
    public NIOReader createDetailNIOReader(SingleUserNIOReadManager manager) {
        throw new UnsupportedOperationException(UNSUPPORT);
    }

    @Override
    public int getPositionOfGroup(int row, SingleUserNIOReadManager manager) {
        if (groupPosition != null) {
            return processPosition(groupPosition.get(row));
        }
        T value = detail.get(row);
        for (int i = 0; i < getter.sizeOfGroup(); i++) {
            GroupValueIndex groupValueIndex = getter.getGroupValueIndex(i);
            if (groupValueIndex.hasSameValue(getter.getIndex(value))) {
                return i;
            }
        }
        return 0;
    }

    protected int processPosition(Integer value) {
        /**
         * Connery: BI-5488螺旋分析中null值没有GroupPosition
         * 现在统一按照NIOConstant.INTEGER.NULL_VALUE处理。
         */
        if (value == null) {
            return NIOConstant.INTEGER.NULL_VALUE;
        } else {
            return value;
        }
    }

    public ICubeColumnIndexReader createGroupByType(BIKey key, ValueConverter converter, Comparator comparator) {
        CubeTreeMap getter = new CubeTreeMap(comparator);
        Map<Object, IntArray> treeMap = new TreeMap<Object, IntArray>();
        IntArray nullList = new IntArray();
        for (int i = 0; i < detail.size(); i++) {
            T t = detail.get(i);
            Object value = t;
            if (BICollectionUtils.isNotCubeNullKey(value)) {
                value = converter.result2Value(t);
                /**
                 * 不明白这个地方为什么要把value转成Long，普通表的年份分组是int，
                 * 但是螺旋分析的却是long，可能会带来一些列类型转换的问题
                 */
                if (value instanceof Integer) {
                    value = ((Integer) value).longValue();
                }
            }
            if (value != null) {
                IntArray list = treeMap.get(value);
                if (list == null) {
                    list = new IntArray();
                    treeMap.put(value, list);
                }
                list.add(i);
            } else {
                nullList.add(i);
            }
        }
        for (Map.Entry<Object, IntArray> entry : treeMap.entrySet()) {
            getter.put(entry.getKey(), GVIFactory.createGroupValueIndexBySimpleIndex(entry.getValue()));
        }
        initGroupPosition(getter);
        return nullList.size == 0 ? getter : new CubeIndexGetterWithNullValue(getter, null, GVIFactory.createGroupValueIndexBySimpleIndex(nullList));
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
        return initGetter(key).sizeOfGroup();
    }

    @Override
    public ICubeTableIndexReader getGroupValueIndexArrayReader(SingleUserNIOReadManager manager) {
        throw new UnsupportedOperationException(UNSUPPORT);
    }
}