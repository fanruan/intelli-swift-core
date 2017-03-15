package com.fr.bi.web.conf.services.cubetask;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.field.BIBusinessField;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.pack.data.BIBasicBusinessPackage;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.finebi.cube.conf.pack.data.BIPackageName;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.fr.bi.base.BIUser;
import com.fr.bi.conf.data.source.DBTableSource;
import com.fr.bi.exception.BIKeyDuplicateException;
import com.fr.bi.stable.data.BIFieldID;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.utils.algorithem.BIRandomUitils;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Connery on 2016/11/29.
 */
public class BISimpleAPIDemoAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        BIBasicBusinessPackage biBasicBusinessPackage = new BIBasicBusinessPackage(
                new BIPackageID(BIRandomUitils.getRandomLetterString(10)),
                new BIPackageName(BIRandomUitils.getRandomLetterString(10)),
                new BIUser(-999), 0l);
        BICubeConfigureCenter.getPackageManager().addPackage(-999, biBasicBusinessPackage);
        BIBusinessTable table_fee = demo(biBasicBusinessPackage, "DEMO_ACTIVITY_FEE");
        BIBusinessTable table_con = demo(biBasicBusinessPackage, "DEMO_CONTRACT");
        BITableRelation relation = new BITableRelation(table_fee.getFields().get(0), table_con.getFields().get(0));
        BICubeConfigureCenter.getTableRelationManager().registerTableRelation(-999, relation);
        WebUtils.printAsJSON(res, new JSONObject().put("result", "success"));
    }

    @Override
    public String getCMD() {
        return "add_demo";
    }

    private BIBusinessTable demo(BIBasicBusinessPackage biBasicBusinessPackage, String tableName) {

        try {
            /**
             * 创建一個业务表
             */
            BIBusinessTable table = new BIBusinessTable(new BITableID(BIRandomUitils.getRandomLetterString(10)), tableName);
            /**
             * 创建一个表数据源，“BIdemo”是数据的名稱，"DEMO_ACTIVITY_FEE"是表名
             */
            DBTableSource tableSource = new DBTableSource("BIdemo", tableName);
            /**
             * 刷新，确保字段的存在
             */
            tableSource.refresh();
            extractFields(table, tableSource);
            /**
             * 注册数据库表
             */
            BICubeConfigureCenter.getDataSourceManager().addTableSource(table, tableSource);
            table.setSource(tableSource);
            biBasicBusinessPackage.addBusinessTable(table);
            BICubeConfigureCenter.getAliasManager().setAliasName(table.getID().getIdentityValue(), "Demo_" + tableName, -999);
            BICubeConfigureCenter.getAliasManager().setAliasName(table.getFields().get(0).getFieldID().getIdentityValue(), "Demo_" + tableName + "_Field", -999);
            return table;
        }  catch (BIKeyDuplicateException e) {
            e.printStackTrace();
        }
        throw BINonValueUtils.beyondControl();
    }

    private void extractFields(BIBusinessTable table, DBTableSource tableSource) {
        List<BusinessField> fieldList = new ArrayList<BusinessField>();
        for (ICubeFieldSource fieldSource : tableSource.getFields().values()) {
            BIBusinessField field = new BIBusinessField(
                    table,
                    new BIFieldID(table.getID().getIdentityValue() + fieldSource.getFieldName()),
                    fieldSource.getFieldName(),
                    fieldSource.getClassType(),
                    fieldSource.getFieldSize());
            fieldList.add(field);
        }
        table.setFields(fieldList);
    }
}
