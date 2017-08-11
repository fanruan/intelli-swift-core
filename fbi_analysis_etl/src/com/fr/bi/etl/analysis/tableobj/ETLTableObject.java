/**
 *
 */
package com.fr.bi.etl.analysis.tableobj;

import com.finebi.common.name.Name;
import com.finebi.common.name.NameImp;
import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.adapter.BICubeTableAdapter;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.data.disk.BICubeDiskPrimitiveDiscovery;
import com.finebi.cube.location.BICubeLocation;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.location.manager.BILocationProvider;
import com.finebi.cube.location.manager.ILocationConverter;
import com.finebi.cube.structure.BICube;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.common.inter.Delete;
import com.fr.bi.common.inter.Release;
import com.fr.bi.etl.analysis.data.UserCubeTableSource;
import com.fr.bi.stable.engine.index.NullTableIndexException;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.bi.util.BIConfigurePathUtils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @author Daniel
 */
public class ETLTableObject implements Release, Delete {

    private String path;

    private ICubeTableService ti;

    private SingleUserNIOReadManager manager = new SingleUserNIOReadManager(-1);

    private volatile boolean isClear = false;

    private static BILogger LOGGER = BILoggerFactory.getLogger(ETLTableObject.class);

    public ETLTableObject(final UserCubeTableSource source, final String id) {
        this.path = BIConfigurePathUtils.createUserETLCubePath(source.fetchObjectCore().getIDValue(), id);
        ti = new BICubeTableAdapter(new BICube(new BICubeResourceRetrieval(new ICubeConfiguration() {
            @Override
            public URI getRootURI() {
                try {
                    File file = new File(new BICubeLocation(BIConfigurePathUtils.createUserETLTableBasePath(source.fetchObjectCore().getID().getIdentityValue()), id, new ILocationConverter() {
                        @Override
                        public ICubeResourceLocation getRealLocation(String path, String child) throws URISyntaxException {
                            return new BICubeLocation(path, child, this);
                        }
                    }).getAbsolutePath());
                    return URI.create(file.toURI().getRawPath());
                } catch (URISyntaxException e) {
                    throw BINonValueUtils.beyondControl(e);
                }
            }

            @Override
            public BILocationProvider getLocationProvider() {
                return null;
            }

        }), BIFactoryHelper.getObject(ICubeResourceDiscovery.class)), source);
    }


    public ICubeTableService getTableIndex() {
        if (isClear) {
            throw new NullTableIndexException();
        }
        return ti;
    }


    /* (non-Javadoc)
     * @see com.fr.bi.common.inter.Release#clear()
     */
    @Override
    public void clear() {
        synchronized (this) {
            isClear = true;
            ti.clear();
            manager.clear();
        }
    }

    /**
     *
     */
    @Override
    public void delete() {
        List<String> files2Clear = BIFileUtils.findAllFiles(new File(this.path).getParentFile());
        boolean success = BIFileUtils.delete(new File(this.path).getParentFile());
        if (!success) {
            LOGGER.error("delete failed" + this.path);
            List<String> fileList = BIFileUtils.deleteFiles(new File(this.path).getParentFile());
            for (String s : fileList) {
                new File(s).deleteOnExit();
            }
        }
        for (String fileName : files2Clear) {
            BICubeDiskPrimitiveDiscovery.getInstance().clearFileNotExist(new File(fileName).toURI().getRawPath());
        }
    }

}