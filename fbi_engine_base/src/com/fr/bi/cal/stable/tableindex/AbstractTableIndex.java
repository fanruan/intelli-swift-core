package com.fr.bi.cal.stable.tableindex;

import com.finebi.cube.api.ICubeTableService;
import com.fr.base.FRContext;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.cal.stable.cube.file.TableCubeFile;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.engine.index.BITableCubeFile;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.bi.stable.structure.collection.list.IntList;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.stable.core.UUID;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by GUY on 2015/3/18.
 */
public abstract class AbstractTableIndex implements ICubeTableService {

    protected transient Date lastTime;
    protected transient Map<BIKey, ICubeFieldSource> columns = new ConcurrentHashMap<BIKey, ICubeFieldSource>();
    protected transient int tableVersion = 0;
    protected transient int rowCount;
    protected transient Map<BIKey, Long> groupCount = new ConcurrentHashMap<BIKey, Long>();
    //TODO removedlist 不需要放内存,直接用NIOReader可好
    protected transient IntList removedList;
    protected transient GroupValueIndex allShowIndex;
    protected BITableCubeFile cube;
    protected SingleUserNIOReadManager manager;
    private String id;


    protected AbstractTableIndex( String path, SingleUserNIOReadManager manager) {
        this.id = UUID.randomUUID().toString();
        cube = new TableCubeFile(path);
        this.manager = manager;
        loadValues();
    }

    protected AbstractTableIndex(BITableCubeFile cube) {
        this.id = UUID.randomUUID().toString();
        this.cube = cube;
        loadValues();
    }

    protected void loadValues() {
        loadMain();
        loadRowCount();
        loadTableVersion();
        loadOthers();
    }

    /**
     * 总长度
     */
    protected void loadRowCount() {
        try {
            rowCount = cube.getRowCount();
        } catch (Exception e) {
            FRContext.getLogger().error(e.getMessage(), e);
        }
    }

    /**
     * cube 版本
     */
    protected void loadTableVersion() {
        try {
            tableVersion = cube.getTableVersion();
        } catch (Exception e) {
            FRContext.getLogger().error(e.getMessage(), e);
        }
    }

    /**
     * 主要字段等信息
     */
    protected void loadMain() {
        try {
            ICubeFieldSource[] fields = cube.getBIField();
            for (ICubeFieldSource column : fields) {
                this.columns.put(new IndexKey(column.getFieldName()), column);
            }
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }

    protected void loadOthers() {
        try {
            this.lastTime = cube.getCubeLastTime();
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }

        try {
            removedList = cube.getRemoveList(manager);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        this.allShowIndex = (removedList == null || removedList.size() == 0 )? GVIFactory.createAllShowIndexGVI(rowCount)
                : GVIFactory.createGroupVauleIndexBySimpleIndex(removedList).NOT(rowCount);
    }

    @Override
    public Map<BIKey, ICubeFieldSource> getColumns() {
        return columns;
    }

    @Override
    public int getColumnSize() {
        return columns.size();
    }

    public long getGroupCount(BIKey key) {
        if (groupCount.containsKey(key)) {
            return groupCount.get(key);
        }
        try {
            groupCount.put(key, cube.getGroupCount(key));
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return groupCount.get(key);
    }

    @Override
    public GroupValueIndex getAllShowIndex() {
        return allShowIndex;
    }

    @Override
    public long getTableVersion(BIKey key) {
        return tableVersion;
    }

    @Override
    public Date getLastTime() {
        return lastTime;
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public IntList getRemovedList() {
        return removedList;
    }

    @Override
    public String getId() {
        return id;
    }
}