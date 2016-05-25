package com.finebi.cube.conf;

import com.finebi.cube.conf.datasource.FieldDataSourceService;
import com.finebi.cube.conf.datasource.TableDataSourceService;
import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.exception.BIFieldAbsentException;
import com.fr.bi.stable.data.db.ICubeFieldSource;

/**
 * This class created on 2016/5/23.
 *
 * @author Connery
 * @since 4.0
 */
public interface BIDataSourceManagerProvider extends FieldDataSourceService, TableDataSourceService {

    String XML_TAG = "BIDataSourceManager";


    /**
     * @param biField
     * @return
     * @throws BIFieldAbsentException
     */
    ICubeFieldSource findDBField(BusinessField biField) throws BIFieldAbsentException;

    @Deprecated
    void persistData(long userId);

}