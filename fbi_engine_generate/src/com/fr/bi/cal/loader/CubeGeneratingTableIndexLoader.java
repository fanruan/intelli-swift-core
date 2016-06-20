package com.fr.bi.cal.loader;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.base.BIBasicCore;
import com.fr.bi.base.BICore;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.cal.stable.cube.file.TableCubeFile;
import com.fr.bi.cal.stable.engine.index.loader.CubeAbstractLoader;
import com.fr.bi.cal.stable.tableindex.index.BIMultiTableIndex;
import com.fr.bi.cal.stable.tableindex.index.BITableIndex;
import com.fr.bi.common.inter.ValueCreator;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.data.source.SourceFile;
import com.fr.bi.stable.io.newio.NIOUtils;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.file.BIPathUtils;
import com.fr.bi.stable.utils.program.BIConstructorUtils;
import com.fr.general.GeneralContext;
import com.fr.stable.EnvChangedListener;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class CubeGeneratingTableIndexLoader extends CubeAbstractLoader {

    /**
     *
     */
    private static final long serialVersionUID = 5409283024826768350L;
    private static Map<Long, ICubeDataLoader> userMap = new ConcurrentHashMap<Long, ICubeDataLoader>();

    public CubeGeneratingTableIndexLoader(long userId) {
        super(userId);
    }

    static {
        GeneralContext.addEnvChangedListener(new EnvChangedListener() {

            @Override
            public void envChanged() {
                CubeGeneratingTableIndexLoader.envChanged();
            }
        });
    }

    public static ICubeDataLoader getInstance(long userId) {
        return BIConstructorUtils.constructObject(userId, CubeGeneratingTableIndexLoader.class, userMap);
    }

    public static void envChanged() {
        synchronized (CubeGeneratingTableIndexLoader.class) {
            for (Entry<Long, ICubeDataLoader> entry : userMap.entrySet()) {
                ICubeDataLoader loader = entry.getValue();
                if (loader != null) {
                    loader.clear();
                }
            }
            userMap.clear();
        }
    }

    @Override
    public long getUserId() {
        return biUser.getUserId();
    }




    public ICubeTableService getTableIndexByPath(final SourceFile file) {
        final BICore key = BIBasicCore.generateValueCore(file.getPath());
        return indexMap.get(key, new ValueCreator<ICubeTableService>() {

            @Override
            public ICubeTableService createNewObject() {
                return createTableIndex(file);
            }
        });
    }

    private ICubeTableService createTableIndex(SourceFile file) {
        if (file.isSource()) {
            return createTableIndex(BIBasicCore.generateValueCore(file.getPath()));
        }
        ICubeTableService[] tis = new ICubeTableService[file.getChildren().size()];
        for (int i = 0; i < tis.length; i++) {
            tis[i] = createTableIndex(file.getChildren().get(i));
        }
        return new BIMultiTableIndex(tis);
    }

    @Override
    public ICubeTableService getTableIndex(CubeTableSource tableSource) {
        return createTableIndex(BIBasicCore.generateValueCore(tableSource.getSourceID()));
    }

    @Override
    public BIKey getFieldIndex(BusinessField column) {
        return null;
    }

    private ICubeTableService createTableIndex(final BICore core) {
        return indexMap.get(core, new ValueCreator<ICubeTableService>() {

            @Override
            public ICubeTableService createNewObject() {
                ICubeTableService ti = null;
                try {
                    String path = BIPathUtils.createTableTempPath(core.getID().getIdentityValue(), biUser.getUserId());
                    TableCubeFile f = new TableCubeFile(path);
                    if (f.checkCubeVersion()) {
                        ti = new BITableIndex(path, getNIOReaderManager());
                    } else {
                        path = BIPathUtils.createTablePath(core.getID().getIdentityValue(), biUser.getUserId());
                        ti = new BITableIndex(path, getNIOReaderManager());
                    }
                } catch (Exception e) {
                    BILogger.getLogger().error("Can`t Find Cube Of Table : " + core.toString() + ", Please Check Your Database Connection");
                }
                return ti;
            }
        });

    }

    @Override
    public boolean needReadTempValue() {
        return true;
    }

    @Override
    public boolean needReadCurrentValue() {
        return true;
    }


    @Override
    public SingleUserNIOReadManager getNIOReaderManager() {
        return NIOUtils.getGeneratingManager(biUser.getUserId());
    }

    @Override
    public long getVersion() {
        return 0;
    }
}