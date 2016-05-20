/**
 * 
 */
package com.fr.bi.etl.analysis.tableobj;

import com.fr.bi.cal.generate.index.IndexGenerator;
import com.fr.bi.cal.stable.cube.file.TableCubeFile;
import com.fr.bi.etl.analysis.data.UserTableSource;
import com.fr.bi.stable.utils.file.BIPathUtils;

/**
 * @author Daniel
 *
 */
public class UserETLIndexGenerator extends IndexGenerator {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -3109798392790594046L;

	/**
	 * @param source
	 * @param userId
	 * @param version
	 */
	public UserETLIndexGenerator(UserTableSource source, int version, String path) {
        super(source, path, source.getUserId(), version);
    }
	
    @Override
	protected void createTableCube() {
        cube = new TableCubeFile(BIPathUtils.createUserETLTablePath(source.fetchObjectCore().getID().getIdentityValue(), pathSuffix));
    }

}