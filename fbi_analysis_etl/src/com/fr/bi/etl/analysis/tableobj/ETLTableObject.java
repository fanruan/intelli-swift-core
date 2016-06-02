/**
 * 
 */
package com.fr.bi.etl.analysis.tableobj;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.adapter.BICubeTableAdapter;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.location.BICubeLocation;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.structure.BICube;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.common.inter.Delete;
import com.fr.bi.common.inter.Release;
import com.fr.bi.etl.analysis.data.UserCubeTableSource;
import com.fr.bi.stable.engine.index.NullTableIndexException;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.bi.stable.utils.file.BIPathUtils;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Daniel
 *
 */
public class ETLTableObject implements Release, Delete {

	private String path;
	
	private ICubeTableService ti;
	
	private SingleUserNIOReadManager manager = new SingleUserNIOReadManager(-1);
	
	private volatile boolean isClear = false;
	
	public ETLTableObject(final UserCubeTableSource source, final String id){
		this.path = BIPathUtils.createUserETLCubePath(source.fetchObjectCore().getIDValue(), id);
		ti = new BICubeTableAdapter(new BICube(new BICubeResourceRetrieval(new ICubeConfiguration() {
            @Override
            public URI getRootURI() {
                try {
                    return URI.create(new BICubeLocation(BIPathUtils.createUserETLTableBasePath(source.fetchObjectCore().getID().getIdentityValue()), id).getAbsolutePath());
                } catch (URISyntaxException e) {
                    throw BINonValueUtils.beyondControl(e);
                }
            }
        }), BIFactoryHelper.getObject(ICubeResourceDiscovery.class)), source);
	}
	
	
	public ICubeTableService getTableIndex(){
		if(isClear){
			throw new NullTableIndexException();
		}
		return ti;
	}
	

	/* (non-Javadoc)
	 * @see com.fr.bi.common.inter.Release#clear()
	 */
	@Override
	public void clear() {
		isClear = true;
		ti.clear();
		manager.clear();
	}

	/**
	 * 
	 */
	@Override
	public void delete() {
		BIFileUtils.delete(new File(this.path));
	}
	
}