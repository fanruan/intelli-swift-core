package com.fr.bi.etl.analysis.data;

import java.util.Set;

import com.fr.bi.stable.data.source.ITableSource;

/**
 * Created by 小灰灰 on 2015/12/14.
 */
public interface UserTableSource extends ITableSource {

	/**
	 * @return
	 */
	long getUserId();

	/**
	 * @return
	 */
	Set<String> getSourceUsedMD5();

    boolean containsIDParentsWithMD5(String md5);
}