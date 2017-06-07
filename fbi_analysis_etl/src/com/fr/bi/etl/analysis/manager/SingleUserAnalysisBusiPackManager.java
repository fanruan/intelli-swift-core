package com.fr.bi.etl.analysis.manager;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.pack.data.BIBusinessPackage;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.common.factory.IFactoryService;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.etl.analysis.conf.AnalysisBusiTable;
import com.fr.bi.etl.analysis.conf.AnalysisPackManager;
import com.fr.bi.etl.analysis.data.AnalysisCubeTableSource;
import com.fr.bi.exception.BIKeyDuplicateException;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.fs.control.UserControl;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Created by 小灰灰 on 2015/12/11.
 */
@BIMandatedObject(factory = IFactoryService.CONF_XML, implement = SingleUserAnalysisBusiPackManager.class)
public class SingleUserAnalysisBusiPackManager{


    private long userId;

    private String version;

    private AnalysisPackManager pack;

    public SingleUserAnalysisBusiPackManager(long userId) {
        this.userId = userId;
        this.pack = new AnalysisPackManager(userId);
    }

    public boolean checkVersion() {
        //ResourceHelper那边取了所有用户的业务包，没做过螺旋分析的用户也会new一个空的manager对象，这边判断下如果业务包是空的，就不兼容了，要不几万个用户卡死了
        if (!pack.getAllTables().isEmpty() && !ComparatorUtils.equals(this.version, "4.0.2.20170607")){
            synchronized (this){
                if (!ComparatorUtils.equals(this.version, "4.0.2.20170607")){
                    try {
                        checkWidget();
                    } catch (Exception e){
                        BILoggerFactory.getLogger().error(e.getMessage());
                        return true;
                    }
                    this.version = "4.0.2.20170607";
                    return false;
                }
            }
        }
        return true;
    }

    private void checkWidget() throws BIKeyDuplicateException {
        BILoggerFactory.getLogger().info("start check spa busipack");
        for (BusinessTable table : getAllTables()){
            ((AnalysisCubeTableSource)table.getTableSource()).resetTargetsMap();
            BIAnalysisETLManagerCenter.getDataSourceManager().addTableSource(table, table.getTableSource());
        }
        BIAnalysisETLManagerCenter.getDataSourceManager().persistData(UserControl.getInstance().getSuperManagerID());
        BILoggerFactory.getLogger().info("finish check spa busipack");
    }


    public void addTable(AnalysisBusiTable table) {
        pack.addTable(table);
    }

    public void removeTable(String tableId) {
        pack.removeTable(tableId);
    }

    public AnalysisBusiTable getTable(String tableId) throws BITableAbsentException {
        return pack.getTable(tableId);
    }

    public JSONObject createJSON(Locale locale) throws Exception {
        return pack.createJSON(locale);
    }

    public Set<BIBusinessPackage> getAllPacks(){
        HashSet<BIBusinessPackage> packSet = new HashSet<BIBusinessPackage>();
        packSet.add(pack.getAnylysis().getPack());
        return packSet;
    }

    public Set<BusinessTable> getAllTables(){
        return pack.getAllTables();
    }
}