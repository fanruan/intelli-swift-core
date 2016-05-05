package com.fr.bi.cal.stable.index;

import com.fr.bi.base.ValueConverter;
import com.fr.bi.cal.stable.index.file.VersionFile;
import com.fr.bi.conf.log.BIRecord;
import com.fr.bi.stable.constant.CubeConstant;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.file.ColumnFile;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.index.CubeGenerator;
import com.fr.bi.stable.io.newio.NIOReader;
import com.fr.bi.stable.io.newio.NIOWriter;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.bi.stable.io.sortlist.ISortNIOReadList;
import com.fr.bi.stable.structure.collection.list.IntList;
import com.fr.bi.stable.utils.code.BIPrintUtils;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Created by GUY on 2015/3/11.
 */
public class GroupIndexCreator<F, T> implements CubeGenerator {

    private NIOReader<F> sml;

    private SingleUserNIOReadManager manager;

    private NIOWriter<Integer> indexGroupWriter;

    private VersionFile indexGroupVersionFile;

    private long rowCount;

    private Comparator<T> comparator;

    private String fieldName;

    private int type;

    private ColumnFile<T> cf;

    private int version;

    private ValueConverter<F, T> converter;


    private BIRecord log;

    private Table tableKey;

    private boolean needRelease;

    public GroupIndexCreator(SingleUserNIOReadManager manager,
                             NIOReader sml,
                             Comparator<T> comparator,
                             String fieldName,
                             ColumnFile cf,
                             ValueConverter<F, T> creator,
                             long rowCount,
                             int version, boolean needRelease
    ) {
        this.manager = manager;
        this.sml = sml;
        this.comparator = comparator;
        this.fieldName = fieldName;
        this.cf = cf;
        this.converter = creator;
        this.rowCount = rowCount;
        this.version = version;
        this.needRelease = needRelease;
    }


    public void setStringGroupWriter(NIOWriter<Integer> indexGroupWriter) {
        this.indexGroupWriter = indexGroupWriter;
    }

    public void setStringGroupVersion(VersionFile stringIndexVersionFile) {
        this.indexGroupVersionFile = stringIndexVersionFile;
    }


    private Map<T, IntList> createTreeMap(IntList nullList) {
        Map<T, IntList> treeMap = new TreeMap<T, IntList>(comparator);
        long start = System.currentTimeMillis();
        for (int i = 0; i < rowCount; i++) {
            F fValue = sml.get(i);
            if (fValue != null) {
                T value = converter.result2Value(fValue);
                IntList list = treeMap.get(value);
                if (list == null) {
                    list = new IntList();
                    treeMap.put(value, list);
                }
                list.add(i);
            } else {
                nullList.add(i);
            }
            if (((i + 1) & CubeConstant.LOG_ROW) == 0) {
                BIPrintUtils.writeIndexLog("[" + fieldName + "] read and make index ", (int) i, rowCount, start);
                if (log != null) {
                    log.recordColumnToInfoTable(tableKey, fieldName, System.currentTimeMillis() - start,
                            //前一段是70%后一段30%
                            Math.round((float) (i + 1) / rowCount * CubeConstant.PERCENT_ROT_SEVENTY));
                }
            }
        }
        return treeMap;
    }

    @Override
    public void generateCube() {
        if (log != null) {
            log.recordColumnToInfoTable(tableKey,
                    fieldName, 0, 0);
        }
        IntList nullList = new IntList();
        Map<T, IntList> treeMap = createTreeMap(nullList);
        long groupCount = treeMap.size();
        Iterator<Entry<T, IntList>> it = treeMap.entrySet().iterator();
        int i = 0;
        long start = System.currentTimeMillis();
        NIOWriter<T> wml = cf.createGroupWriter();
        NIOWriter<byte[]> indexWriter = cf.createIndexWriter();
        NIOWriter<byte[]> nullWriter = cf.createNullWriter();
        while (it.hasNext()) {
            Entry<T, IntList> entry = it.next();
            wml.add(i, entry.getKey());
            IntList row = entry.getValue();
            GroupValueIndex gvi = GVIFactory.createGroupVauleIndexBySimpleIndex(row);
            indexWriter.add(i, gvi.getBytes());
            i++;
            if ((i & CubeConstant.LOG_ROW) == 0) {// 每执行262144行print一下
                BIPrintUtils.writeIndexLog("[" + fieldName + "] write index ", (int) i, groupCount, start);
                if (log != null) {
                    log.recordColumnToInfoTable(tableKey, fieldName, System.currentTimeMillis() - start,
                            //前一段是70%后一段30%
                            Math.round((float) i / groupCount * CubeConstant.PERCENT_ROT_THIRTY) + CubeConstant.PERCENT_ROT_SEVENTY);
                }
            }
            it.remove();
        }
        nullWriter.add(0, GVIFactory.createGroupVauleIndexBySimpleIndex(nullList).getBytes());
        cf.releaseGroupValueIndexCreator();
        cf.writeGroupCount(groupCount);
        cf.writeVersion(version);
        if (needCreateIndexFile(groupCount)) {
            writePrimaryIndexFile();
        } else {
            releaseManager();
        }
    }

    private void releaseManager(){
        if (needRelease){
            manager.releaseResource();
        }
    }

    private boolean needCreateIndexFile(long groupCount) {
        return indexGroupWriter != null
                && indexGroupVersionFile != null;
    }

    public void setLog(BIRecord log, Table tableKey) {
        this.log = log;
        this.tableKey = tableKey;
    }


    public void writePrimaryIndexFile() {
    	ISortNIOReadList<T> valueIndexMap = cf.createSortGroupReader(new IndexKey(fieldName), manager);
        for (long row = 0; row < rowCount; row++) {
            T v = converter.result2Value(sml.get(row));
            if (v != null) {
            	T[] array = valueIndexMap.createKey(1);
            	array[0] = v;
                int valueIndex = valueIndexMap.indexOf(array)[0];
                indexGroupWriter.add(row, valueIndex);
            } else {
                indexGroupWriter.add(row, CubeConstant.NULLINDEX);
            }
        }
        valueIndexMap.releaseResource();
        releaseManager();
        indexGroupWriter.releaseResource();
        cf.deteleDetailFile();
        indexGroupVersionFile.write(version);
    }


}