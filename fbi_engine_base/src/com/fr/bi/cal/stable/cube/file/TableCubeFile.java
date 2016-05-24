package com.fr.bi.cal.stable.cube.file;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.base.ValueConverter;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.cal.stable.cube.AbstractCubeFile;
import com.fr.bi.cal.stable.cube.ColumnFiles;
import com.fr.bi.cal.stable.index.GroupIndexCreator;
import com.fr.bi.cal.stable.index.file.CubeLastTimeFile;
import com.fr.bi.cal.stable.index.file.CubeMainFile;
import com.fr.bi.cal.stable.index.file.CubeOtherFile;
import com.fr.bi.cal.stable.index.file.VersionFile;
import com.fr.bi.cal.stable.relation.uselinkindex.LinkColumnUseIndexLoader;
import com.fr.bi.stable.constant.CubeConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.BICubeFieldSource;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.engine.index.BITableCubeFile;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.file.ColumnFile;
import com.fr.bi.stable.file.IndexFile;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.GroupValueIndexCreator;
import com.fr.bi.stable.gvi.array.GroupValueIndexArrayReader;
import com.fr.bi.stable.gvi.array.ICubeTableIndexReader;
import com.fr.bi.stable.index.CubeGenerator;
import com.fr.bi.stable.io.newio.NIOReader;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.bi.stable.structure.array.ArrayKey;
import com.fr.bi.stable.structure.collection.list.IntList;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.bi.stable.utils.file.BIPathUtils;
import com.fr.json.JSONObject;
import com.fr.stable.StableUtils;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TableCubeFile extends AbstractCubeFile {

    private String path;

    @SuppressWarnings("unused")
    private VersionFile currentVersion;

    @SuppressWarnings("unused")
    private VersionFile cubeVersion;

    @SuppressWarnings("unused")
    private CubeMainFile mainFile;

    @SuppressWarnings("unused")
    private CubeOtherFile rowCountFile;

    @SuppressWarnings("unused")
    private CubeLastTimeFile lastTimeFile;

    @SuppressWarnings("unused")
    private IntegerColumnFile removeFile;

    private Map<ArrayKey<BITableSourceRelation>, IndexFile> basicRelationMap = new ConcurrentHashMap<ArrayKey<BITableSourceRelation>, IndexFile>();

    public TableCubeFile(String path) {
        this.path = path;
    }

    @Override
    public void mkDir() {
        File f = new File(this.path);
        StableUtils.mkdirs(f);
    }


    @Override
    public void writeMain(List<String> columnList) {
        getCubeMainFile().write(columnList);
    }

    @Override
    public void writeVersionCheck() {
        getCubeVersionFile().write(CubeConstant.CUBEVERSION);
    }

    @Override
    public void writeTableGenerateVersion(int version) {
        getTableVersionFile().write(version);
    }

    @Override
    public int getTableVersion() {
        return Integer.parseInt(getTableVersionFile().read());
    }

    @Override
    public void writeRowCount(long rowCount) {
        getRowCountFile().write(rowCount);
    }

    @Override
    public int getRowCount() {
        return Integer.parseInt(getRowCountFile().read());
    }

    @Override
    public void copyDetailValue(BITableCubeFile oldCube, SingleUserNIOReadManager manager, long rowCount) {
        initColumns();
        TableCubeFile cube = (TableCubeFile) oldCube;
        ICubeFieldSource[] fields = getBIField();
        for (int i = 0; i < fields.length; i++) {
            columns.getColumnFile(i).copyDetailValue(BIPathUtils.createSingleFieldBasePath(cube.path, fields[i].getFieldName()), cube.getColumnFile(fields[i].getFieldName()), manager, rowCount);
        }
    }

    private CubeMainFile getCubeMainFile() {
        return BIFileUtils.createFile(this, "mainFile", CubeMainFile.class, BIPathUtils.createMainPath(path));
    }

    private VersionFile getCubeVersionFile() {
        return BIFileUtils.createFile(this, "cubeVersion", VersionFile.class, BIPathUtils.createVersionCubePath(path));
    }

    private VersionFile getTableVersionFile() {
        return BIFileUtils.createFile(this, "currentVersion", VersionFile.class, BIPathUtils.createVersionTablePath(path));
    }

    private CubeOtherFile getRowCountFile() {
        return BIFileUtils.createFile(this, "rowCountFile", CubeOtherFile.class, BIPathUtils.createOtherPath(path));
    }

    private CubeLastTimeFile getLastTimeFile() {
        return BIFileUtils.createFile(this, "lastTimeFile", CubeLastTimeFile.class, BIPathUtils.createOtherPath(path));
    }

    private IntegerColumnFile getRemoveFile() {
        return BIFileUtils.createFile(this, "removeFile", IntegerColumnFile.class, BIPathUtils.createRemovePath(path));
    }


    @Override
    public void writeLastTime() {
        getLastTimeFile().write();
    }

    @Override
    public void writeRemovedLine(TreeSet<Integer> removedLine) {
        IntegerColumnFile file = getRemoveFile();
        Iterator<Integer> it = removedLine.iterator();
        int row = 0;
        while (it.hasNext()) {
            file.addDataValue(row, it.next());
            row++;
        }
    }

    @Override
    public ICubeFieldSource[] getBIField() {
        List<String> columnString = getCubeMainFile().read();
        ICubeFieldSource[] fields = new ICubeFieldSource[columnString.size()];
        for (int i = 0, ilen = columnString.size(); i < ilen; i++) {
            try {
                JSONObject jo = new JSONObject(columnString.get(i));
                BICubeFieldSource field = new BICubeFieldSource(null, null, 0, 0);
                field.parseJSON(jo);
                fields[i] = field;
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }
        return fields;
    }

    @Override
    public void createDetailDataWriter() {
        ColumnFiles columns = initColumns();
        for (int i = 0, ilen = columns.size(); i < ilen; i++) {
            ColumnFile<?> cf = columns.getColumnFile(i);
            if (cf != null) {
                cf.createDetailDataWriter();
            }
        }
    }

    protected ColumnFiles initColumns() {
        if (columns != null) {
            return columns;
        }
        synchronized (this) {
            if (columns == null) {
                ICubeFieldSource[] fields = getBIField();
                ColumnFile<?>[] columns = new ColumnFile[fields.length];
                Map<String, Integer> colIndexMap = new HashMap<String, Integer>(fields.length);
                for (int i = 0, ilen = fields.length; i < ilen; i++) {
                    ICubeFieldSource field = fields[i];
                    colIndexMap.put(field.getFieldName(), i);
                    String fieldPath = BIPathUtils.createSingleFieldBasePath(path, field.getFieldName());
                    switch (field.getFieldType()) {
                        case DBConstant.COLUMN.DATE:
                            columns[i] = new DateColumnFile(fieldPath);
                            break;
                        case DBConstant.COLUMN.NUMBER:
                            switch (field.getClassType()) {
                                case DBConstant.CLASS.INTEGER:
                                case DBConstant.CLASS.LONG: {
                                    columns[i] = new LongColumnFile(fieldPath);
                                    break;
                                }
                                default: {
                                    columns[i] = new DoubleColumnFile(fieldPath);
                                    break;
                                }
                            }
                            break;
                        case DBConstant.COLUMN.STRING:
                            columns[i] = new StringColumnFile(fieldPath);
                            break;
                    }
                }
                this.columns = new ColumnFiles(columns, colIndexMap);
            }
            return columns;
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void releaseDetailDataWriter() {
        if (columns != null) {
            for (int i = 0, ilen = columns.size(); i < ilen; i++) {
                ColumnFile cf = columns.getColumnFile(i);
                if (cf != null) {
                    cf.releaseDetailDataWriter();
                }
            }
        }
    }

    public GroupIndexCreator[] createGroupValueIndexCreator() {
        int version = getTableVersion();
        long rowCount = getRowCount();
        List<GroupIndexCreator> giclist = new ArrayList<GroupIndexCreator>();
        ColumnFiles columns = initColumns();
        ICubeFieldSource[] fields = getBIField();
        for (int i = 0; i < columns.size(); i++) {
            giclist.add((GroupIndexCreator) columns.getColumnFile(i).createGroupIndexCreator(new IndexKey(fields[i].getFieldName()), ValueConverter.DEFAULT, version, rowCount));
        }
        return giclist.toArray(new GroupIndexCreator[giclist.size()]);
    }

    @Override
    public GroupValueIndex getNullGroupValueIndex(BIKey key, SingleUserNIOReadManager manager) {
        return GroupValueIndexCreator.createGroupValueIndex(getColumnFile(key).createNullIndexReader(key, manager).get(0));
    }

    @Override
    public ICubeColumnIndexReader createGroupByType(BIKey key, List<BITableSourceRelation> relationList, SingleUserNIOReadManager manager) {
        checkIndex(key, relationList, manager);
        return getColumnFile(key).createGroupByType(key, relationList, manager);
    }

    @Override
    public ICubeTableIndexReader getBasicGroupValueIndexArrayReader(List<BITableSourceRelation> relationList, SingleUserNIOReadManager manager) {
        return new GroupValueIndexArrayReader(getLinkIndexFile(relationList).createIndexReader(BIKey.DEFAULT, manager));
    }

    public void releaseGroupValueIndexCreator() {
        if (columns != null) {
            for (int i = 0; i < columns.size(); i++) {
                ColumnFile<?> cf = columns.getColumnFile(i);
                if (cf != null) {
                    cf.releaseGroupValueIndexCreator();
                }
            }
        }
    }

    @Override
    public void delete() {
        BIFileUtils.delete(new File(path));
    }

    @Override
    public boolean checkRelationVersion(List<BITableSourceRelation> relations,
                                        int relation_version) {
        ArrayKey<BITableSourceRelation> key = new ArrayKey<BITableSourceRelation>(relations.toArray(new BITableSourceRelation[relations.size()]));
        IndexFile ifile = getBasicIndexFile(key);
        return ifile.checkVersion(relation_version);
    }

    @Override
    public boolean checkRelationVersion(BIKey key, List<BITableSourceRelation> relations,
                                        int relation_version) {
        ArrayKey<BITableSourceRelation> relationArrayKey = new ArrayKey<BITableSourceRelation>(relations.toArray(new BITableSourceRelation[relations.size()]));
        IndexFile ifile = getLinkIndexFile(key, relations);
        return ifile.checkVersion(relation_version);
    }

    private IndexFile getBasicIndexFile(ArrayKey<BITableSourceRelation> key) {
        IndexFile ifile = basicRelationMap.get(key);
        if (ifile != null) {
            return ifile;
        }
        synchronized (basicRelationMap) {
            ifile = basicRelationMap.get(key);
            if (ifile == null) {
                ifile = new LinkIndexFile(BIPathUtils.createRowIndexPath(path, key));
                basicRelationMap.put(key, ifile);
            }
            return ifile;
        }
    }

    @Override
    public IndexFile getLinkIndexFile(List<BITableSourceRelation> relations) {
        ArrayKey<BITableSourceRelation> key = new ArrayKey<BITableSourceRelation>(relations.toArray(new BITableSourceRelation[relations.size()]));
        return getBasicIndexFile(key);
    }


    @Override
    public boolean checkCubeVersion() {
        return Integer.parseInt(getCubeVersionFile().read()) == CubeConstant.CUBEVERSION && checkColumnVersion();
    }

    private boolean checkColumnVersion() {
//        List<String> columns = getCubeMainFile().read();
//        Iterator<String> it = columns.iterator();
//        while (it.hasNext()){
//            try {
//                JSONObject jo = new JSONObject(it.next());
//                BIField field = new BIField();
//                field.parseJSON(jo);
//                ColumnFile columnFile = getColumnFile(field.getFieldName());
//                columnFile.getVersion()
//            } catch (Exception e) {
//                return false;
//            }
//
//        }
        return true;
    }


    private void checkIndex(BIKey key, List<BITableSourceRelation> relationList, SingleUserNIOReadManager manager) {
        ColumnFile cf = getColumnFile(key);
        if (!cf.checkVersion(getTableVersion())) {
            synchronized (cf) {
                if (!cf.checkVersion(getTableVersion())) {
                    CubeGenerator generator = cf.createGroupIndexCreator(manager, cf.createDetailNIOReader(manager), key, ValueConverter.DEFAULT, getTableVersion(), getRowCount(), false);
                    generator.generateCube();
                }
            }
        }
        if (relationList != null && !relationList.isEmpty()) {
            IndexFile iF = cf.getLinkIndexFile(key, relationList);
            if (!iF.checkVersion(getLinkIndexFile(relationList).getVersion())) {
                synchronized (iF) {
                    if (!iF.checkVersion(getLinkIndexFile(relationList).getVersion())) {
                        new LinkColumnUseIndexLoader(this, key, relationList, manager).generateCube();
                    }
                }
            }
        }

    }


    @Override
    public ICubeTableIndexReader getGroupValueIndexArrayReader(BIKey key, SingleUserNIOReadManager manager) {
        checkIndex(key, new ArrayList<BITableSourceRelation>(), manager);
        return getColumnFile(key).getGroupValueIndexArrayReader(manager);
    }

    @Override
    public IndexFile getLinkIndexFile(BIKey key, List<BITableSourceRelation> relations) {
        ColumnFile<?> cf = getColumnFile(key.getKey());
        if (cf != null) {
            return cf.getLinkIndexFile(key, relations);
        }
        return null;
    }

    @Override
    public Date getCubeLastTime() {
        return new Date(Long.valueOf(getLastTimeFile().read()));
    }

    @Override
    public IntList getRemoveList(SingleUserNIOReadManager manager) {
        Long length = Long.parseLong(getRemoveFile().createLengthFile().read());
        NIOReader<Integer> reader = getRemoveFile().createDetailReader(manager);
        IntList result = new IntList();
        for (long i = 0; i < length; i++) {
            result.add(reader.get(i));
        }
        return result;
    }

    /* (non-Javadoc)
     * @see com.fr.bi.common.inter.Release#clear()
     */
    @Override
    public void clear() {
    }

}