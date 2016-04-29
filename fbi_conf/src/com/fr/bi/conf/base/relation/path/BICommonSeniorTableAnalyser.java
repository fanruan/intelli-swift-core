package com.fr.bi.conf.base.relation.path;

import com.fr.bi.common.container.BIHashMapContainer;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.bi.stable.exception.BITableDuplicateException;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.exception.BIKeyDuplicateException;
import com.fr.bi.stable.relation.BITablePair;
import com.fr.bi.stable.relation.BITableRelation;

import java.util.Iterator;

/**
 * 公共年长表。
 * Created by Connery on 2016/1/13.
 */
public class BICommonSeniorTableAnalyser extends BIHashMapContainer<BITablePair, BITableContainer> implements BICommonSeniorTableAnalysisService {
    private BISeniorTablesManager seniorTablesManager;

    public BICommonSeniorTableAnalyser() {
        this.seniorTablesManager = new BISeniorTablesManager();
    }

    private void addOneCommonSeniorTable(BITablePair pair, Table seniorTable) {
        if (containsKey(pair)) {
            try {
                BITableContainer tableContainer = getValue(pair);
                if (!tableContainer.contain(seniorTable)) {
                    tableContainer.addBITable(seniorTable);
                }
            } catch (BIKeyAbsentException ignore) {

            } catch (BITableDuplicateException ignore) {

            }
        } else {
            try {
                BITableContainer tableContainer = new BITableContainer();
                tableContainer.addBITable(seniorTable);
                putKeyValue(pair, tableContainer);
            } catch (BITableDuplicateException ignore) {

            } catch (BIKeyDuplicateException ignore) {

            }
        }
    }

    @Override
    public void registerOneTableRelation(BITableRelation biTableRelation) {
        seniorTablesManager.addBITableRelation(biTableRelation);
    }

    @Override
    public BITableContainer analysisCommonRelation(BITablePair tablePair) throws BITableAbsentException {
        BITableContainer firstContainer = seniorTablesManager.getSpecificTableIndirectContainer(tablePair.getFrom());
        BITableContainer secondContainer = seniorTablesManager.getSpecificTableIndirectContainer(tablePair.getTo());
        BITableContainer result = new BITableContainer();
        Iterator<Table> it = firstContainer.getContainer().iterator();
        while (it.hasNext()) {
            Table table = it.next();
            if (secondContainer.contain(table)) {
                try {
                    result.addBITable(table);
                } catch (BITableDuplicateException ignore) {
                    continue;
                }
            }
        }
        return result;
    }

    @Override
    public void removeTableRelation(BITableRelation biTableRelation) {
        seniorTablesManager.removeBITableRelation(biTableRelation);
    }
    @Override
    public boolean containTableRelation(BITableRelation biTableRelation) {
      return   seniorTablesManager.containBITableRelation(biTableRelation);
    }
    @Override
    public void clear() {
        super.clear();
    }


}