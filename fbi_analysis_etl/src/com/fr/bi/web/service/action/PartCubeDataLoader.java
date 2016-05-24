package com.fr.bi.web.service.action;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.BIBasicCore;
import com.fr.bi.base.BICore;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.cal.stable.cube.memory.MemoryCubeFile;
import com.fr.bi.cal.stable.tableindex.index.BITableIndex;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.etl.analysis.data.UserCubeTableSource;
import com.fr.bi.etl.analysis.data.UserETLTableSource;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.source.ICubeTableSource;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.bi.stable.utils.program.BIConstructorUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 小灰灰 on 2016/5/16.
 */
public class PartCubeDataLoader implements ICubeDataLoader {
    private static final String EXCEPTION = "ONLY PARAMETER TYPE ITableSource SUPPORT HERE";

    private long userId;

    private static Map<Long, PartCubeDataLoader> userMap = new ConcurrentHashMap<Long, PartCubeDataLoader>();


    private transient Map<BICore, ICubeTableSource> sourceMap = new ConcurrentHashMap<BICore, ICubeTableSource>();

    public PartCubeDataLoader(long userId) {
        this.userId = userId;
    }

    public static PartCubeDataLoader getInstance(long userId, UserCubeTableSource source) {
        PartCubeDataLoader loader = BIConstructorUtils.constructObject(userId, PartCubeDataLoader.class, userMap, false);
        loader.registSource(source);
        return loader;
    }

    private void registSource(ICubeTableSource source){
        BICore core = source.fetchObjectCore();
        if (!sourceMap.containsKey(core)){
            sourceMap.put(source.fetchObjectCore(), source);
            if ( source.getType() == Constants.TABLE_TYPE.USER_ETL ){
                for (ICubeTableSource s :((UserETLTableSource)source).getParents()){
                    registSource(s);
                }
            }
        }
    }

    @Override
    public ICubeTableService getTableIndex(Table td) {
        return getTableIndex(BIBasicCore.generateValueCore(td.getID().getIdentityValue()));
    }

    @Override
    public ICubeTableService getTableIndex(BICore core) {
        ICubeTableSource source = sourceMap.get(core);
        if (isParentTableIndex(source)){
            return getTableIndex(((UserETLTableSource) source).getParents().get(0).fetchObjectCore());
        }
        final MemoryCubeFile cube = new MemoryCubeFile(source.getFieldsArray(new HashSet<ICubeTableSource>()));
        cube.writeRowCount(source.read(new Traversal<BIDataValue>() {
            @Override
            public void actionPerformed(BIDataValue data) {
                cube.addDataValue(data);
            }
        }, source.getFieldsArray(new HashSet<ICubeTableSource>()), this));
        return new BITableIndex(cube);
    }

    @Override
    public ICubeTableService getTableIndex(BIField td) {
        return getTableIndex(td.getTableBelongTo());
    }

    @Override
    public BIKey getFieldIndex(BIField column) {
        return new IndexKey(column.getFieldName());
    }

    @Override
    public ICubeTableService getTableIndex(BITableID id) {
        throw new RuntimeException(EXCEPTION);
    }

    @Override
    public long getUserId() {
        return userId;
    }

    @Override
    public boolean needReadTempValue() {
        return false;
    }

    @Override
    public boolean needReadCurrentValue() {
        return false;
    }

    @Override
    public SingleUserNIOReadManager getNIOReaderManager() {
        throw new RuntimeException(EXCEPTION);
    }

    @Override
    public void releaseCurrentThread() {

    }

    @Override
    public ICubeTableService getTableIndex(ICubeTableSource tableSource, int start, int end) {
        ICubeTableSource source = sourceMap.get(tableSource);
        if (isParentTableIndex(source)){
            return getTableIndex(((UserETLTableSource) source).getParents().get(0).fetchObjectCore(), start, end);
        }
        final MemoryCubeFile cube = new MemoryCubeFile(source.getFieldsArray(new HashSet<ICubeTableSource>()));
        cube.writeRowCount(source.read4Part(new Traversal<BIDataValue>() {
            @Override
            public void actionPerformed(BIDataValue data) {
                cube.addDataValue(data);
            }
        }, source.getFieldsArray(new HashSet<ICubeTableSource>()), this, start, end));
        return new BITableIndex(cube);
    }

    private boolean isParentTableIndex(ICubeTableSource source) {
        return source.getType() == Constants.TABLE_TYPE.USER_ETL && (((UserETLTableSource)source).hasTableFilterOperator()|| ((UserETLTableSource)source).getETLOperators().isEmpty());
    }

    @Override
    public void clear() {
        synchronized (this) {
            if(userMap != null){
                userMap.clear();
            }
        }
    }
}
