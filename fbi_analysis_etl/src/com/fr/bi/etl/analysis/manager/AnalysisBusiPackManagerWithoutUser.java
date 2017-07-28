package com.fr.bi.etl.analysis.manager;

import com.finebi.cube.conf.pack.data.IBusinessPackageGetterService;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.etl.analysis.conf.AnalysisBusiTable;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.fs.control.UserControl;
import com.fr.json.JSONObject;

import java.util.Locale;
import java.util.Set;

/**
 * Created by Lucifer on 2017-4-6.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class AnalysisBusiPackManagerWithoutUser extends AnalysisBusiPackManager implements BIAnalysisBusiPackManagerProvider{
    private static final String TAG = "AnalysisBusiPackManager";
    private static final long serialVersionUID = -2729584433712015294L;
    private final long usedUserID = UserControl.getInstance().getSuperManagerID();

    public SingleUserAnalysisBusiPackManager getUserAnalysisBusiPackManager(long userId) {
        try {
            return getValue(usedUserID);
        } catch (BIKeyAbsentException e) {
            throw new NullPointerException("Please check the userID:" + userId + ",which getIndex a empty manager");
        }
    }

    @Override
    public SingleUserAnalysisBusiPackManager constructUserManagerValue(Long userId) {
        return super.constructUserManagerValue(usedUserID);
    }

    @Override
    public Set<IBusinessPackageGetterService> getAllPackages(long userId) {
        return super.getAllPackages(usedUserID);
    }

    @Override
    public void persistData(long userId) {
        super.persistData(usedUserID);
    }

    @Override
    public JSONObject createPackageJSON(long userId) throws Exception {
        return super.createPackageJSON(usedUserID);
    }

    @Override
    public JSONObject createPackageJSON(long userId, Locale locale) throws Exception {
        return super.createPackageJSON(usedUserID, locale);
    }

    @Override
    public JSONObject createAnalysisPackageJSON(long userId, Locale locale) throws Exception {
        return super.createAnalysisPackageJSON(usedUserID, locale);
    }

    @Override
    public void addTable(AnalysisBusiTable table) {
        getUserAnalysisBusiPackManager(usedUserID).addTable(table);
    }

    @Override
    public void removeTable(String tableId, long userId) {
        super.removeTable(tableId, usedUserID);
    }

    @Override
    public AnalysisBusiTable getTable(String tableId, long userId) throws BITableAbsentException {
        return super.getTable(tableId, usedUserID);
    }

    @Override
    public Set<BusinessTable> getAllTables(long userId) {
        return super.getAllTables(usedUserID);
    }

    @Override
    public JSONObject saveAnalysisETLTable(final long userId, String tableId, String newId, String tableName, String describe, String tableJSON) throws Exception {
        return super.saveAnalysisETLTable(usedUserID, tableId, newId, tableName, describe, tableJSON);
    }
}
