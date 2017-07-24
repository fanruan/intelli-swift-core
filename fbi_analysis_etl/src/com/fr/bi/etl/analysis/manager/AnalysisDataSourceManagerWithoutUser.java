package com.fr.bi.etl.analysis.manager;

import com.fr.fs.control.UserControl;

/**
 * Created by Lucifer on 2017-4-6.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class AnalysisDataSourceManagerWithoutUser extends AnalysisDataSourceManager implements BIAnalysisDataSourceManagerProvider{
    private static final long serialVersionUID = 4132309804166605518L;
    private final long usedUserId = UserControl.getInstance().getSuperManagerID();

    @Override
    public void persistData(long userId) {
        super.persistData(usedUserId);
    }
}
