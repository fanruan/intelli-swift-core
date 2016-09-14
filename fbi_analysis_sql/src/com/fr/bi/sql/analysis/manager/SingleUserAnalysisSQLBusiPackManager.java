package com.fr.bi.sql.analysis.manager;

import com.finebi.cube.conf.pack.data.BIBusinessPackage;
import com.fr.bi.common.factory.IFactoryService;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.sql.analysis.conf.AnalysisSQLBusiTable;
import com.fr.bi.sql.analysis.conf.AnalysisSQLPackManager;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.json.JSONObject;

import java.util.Locale;
import java.util.Set;

/**
 * Created by 小灰灰 on 2015/12/11.
 */
@BIMandatedObject(factory = IFactoryService.CONF_XML, implement = SingleUserAnalysisSQLBusiPackManager.class)
public class SingleUserAnalysisSQLBusiPackManager {


    private long userId;

    private AnalysisSQLPackManager pack;

    public SingleUserAnalysisSQLBusiPackManager(long userId) {
        this.userId = userId;
        this.pack = new AnalysisSQLPackManager(userId);
    }

    public void addTable(AnalysisSQLBusiTable table) {
        pack.addTable(table);
    }

    public void removeTable(String tableId) {
        pack.removeTable(tableId);
    }

    public AnalysisSQLBusiTable getTable(String tableId) throws BITableAbsentException {
        return pack.getTable(tableId);
    }

    public JSONObject createJSON(Locale locale) throws Exception {
        return pack.createJSON(locale);
    }

    public Set<BIBusinessPackage> getAllPacks(){
        return pack.getAnylysis().getAllPackages();
    }

}