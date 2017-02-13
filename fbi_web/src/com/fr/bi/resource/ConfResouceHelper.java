package com.fr.bi.resource;

import com.fr.stable.ArrayUtils;

/**
 * Created by Wang on 2017/1/18.
 */
public class ConfResouceHelper {
    private static String[] getConfCssModule() {
        return new String[]{
                //数据连接
                "com/fr/bi/web/css/fragments/datalink/widget.testlink.loading.css",

                //union
                "com/fr/bi/web/css/modules/onepackage/etl/union/widget.addunion.table.css",
                "com/fr/bi/web/css/modules/onepackage/etl/union/widget.unionresult.header.css",

                //etl relation
                "com/fr/bi/web/css/modules/relation/button.relationtable.field.css",
                "com/fr/bi/web/css/modules/relation/widget.relationset.group.css",

                //建关联面板
                "com/fr/bi/web/css/modules/selectdatamask/widget.selectdata.mask.css",

                //etl预览
                "com/fr/bi/web/css/modules/onepackage/etl/preview/widget.etltable.preview.css",

                //etl增加公式列
                "com/fr/bi/web/css/modules/onepackage/etl/addfield/widget.button.formulafield.css",
                "com/fr/bi/web/css/modules/onepackage/etl/addfield/widget.formulalist.css",

                "com/fr/bi/web/css/modules/onepackage/etl/flowchart/widget.etltable.operator.css",
                "com/fr/bi/web/css/modules/onepackage/etl/flowchart/widget.etltable.combo.css",

                //etl行列转化
                "com/fr/bi/web/css/modules/onepackage/etl/convert/convert.listItem.css",
                "com/fr/bi/web/css/modules/onepackage/etl/convert/convert.displaylabel.css",
                "com/fr/bi/web/css/modules/onepackage/etl/convert/convert.genFields.css",
                "com/fr/bi/web/css/modules/onepackage/etl/convert/convert.selectFieldsDataPane.css",

                //etl新增分组列
                "com/fr/bi/web/css/modules/onepackage/etl/addgroupfield/widget.addgroupfield.css",

                //etl新增公式列
                "com/fr/bi/web/css/modules/onepackage/etl/addfield/widget.addformulafield.css",
                "com/fr/bi/web/css/modules/onepackage/etl/addfield/widget.addformulafield.popover.css",

                //etl自循环列
                "com/fr/bi/web/css/modules/onepackage/etl/circle/circle.operator.css",
                "com/fr/bi/web/css/modules/onepackage/etl/circle/operatorPane/tab/circle.operator.pane.css",
                "com/fr/bi/web/css/modules/onepackage/etl/circle/operatorPane/tab/circle.select.field.button.css",
                "com/fr/bi/web/css/modules/onepackage/etl/circle/operatorPane/tab/circle.tab.css",
                "com/fr/bi/web/css/modules/onepackage/etl/circle/operatorPane/tab/circle.two.condition.switch.css",
                "com/fr/bi/web/css/modules/onepackage/etl/circle/operatorPane/tab/circle.tab.region.css",
                "com/fr/bi/web/css/modules/onepackage/etl/circle/levelPane/circle.display.editor.css",
                "com/fr/bi/web/css/modules/onepackage/etl/circle/circle.result.pane.css",

                //etl分组统计
                "com/fr/bi/web/css/modules/onepackage/etl/group/group.select.fields.css",
                "com/fr/bi/web/css/modules/onepackage/etl/group/group.select.fields.item.css",
                "com/fr/bi/web/css/modules/onepackage/etl/group/group.dimension.css",
                "com/fr/bi/web/css/modules/onepackage/etl/group/region.string.css",

                //etl选择部分字段
                "com/fr/bi/web/css/modules/onepackage/etl/partfield/widget.selectpartfieldlist.css",

                //etl过滤
                "com/fr/bi/web/css/modules/onepackage/etl/filter/filteritem/item.stringfield.css",
                "com/fr/bi/web/css/modules/onepackage/etl/filter/filteritem/item.numberfield.css",
                "com/fr/bi/web/css/modules/onepackage/etl/filter/filteritem/item.datefield.css",
                "com/fr/bi/web/css/modules/onepackage/etl/filter/filteritem/item.notypefield.css",
                "com/fr/bi/web/css/modules/onepackage/etl/filter/formula/item.formula.css",
                "com/fr/bi/web/css/modules/onepackage/etl/filter/formula/item.emptyformula.css",

                //FineBI Service
                "com/fr/bi/web/css/modules/finebiservice/expander.finebiservice.css",
                "com/fr/bi/web/css/modules/finebiservice/finebiservice.css",

                //业务包分组
                "com/fr/bi/web/css/modules/businesspackagegroup/pane.ungroup.and.group.businesspackage.css",
                "com/fr/bi/web/css/modules/businesspackagegroup/buttons/button.businesspackage.css",

                //多路径设置
                "com/fr/bi/web/css/fragments/multirelation/item.tablefield.multirelation.css",
                "com/fr/bi/web/css/fragments/multirelation/item.multirelation.css",
                "com/fr/bi/web/css/fragments/multirelation/expander.multirelation.css",
                "com/fr/bi/web/css/fragments/multirelation/item.tablefield.multirelation.css",
                "com/fr/bi/web/css/fragments/multirelation/view.searcher.multirelation.css",

                //去数据库选表
                "com/fr/bi/web/css/modules/selecttable/widget.selecttable.pane.css",
                "com/fr/bi/web/css/modules/selecttable/widget.datalinktab.css",
                "com/fr/bi/web/css/modules/selecttable/widget.databasetables.pane.css",
                "com/fr/bi/web/css/modules/selecttable/widget.packagetables.pane.css",
                "com/fr/bi/web/css/modules/selecttable/widget.etltables.pane.css",

                //cube日志
                "com/fr/bi/web/css/modules/cubelog/items/title.item.wronginfo.cubelog.css",
                "com/fr/bi/web/css/modules/cubelog/items/item.wronginfo.cubelog.css",
                "com/fr/bi/web/css/modules/cubelog/items/item.cubelog.css",
                "com/fr/bi/web/css/modules/cubelog/nodes/node.wronginfo.cubelog.css",
                "com/fr/bi/web/css/modules/cubelog/nodes/node.cubelog.css",
                "com/fr/bi/web/css/modules/cubelog/tree.cubelog.css",
                "com/fr/bi/web/css/modules/cubelog/cubelog.css",

                //excel
                "com/fr/bi/web/css/extend/excel/excel.upload.css",
                "com/fr/bi/web/css/extend/excel/fieldset/excel.fieldset.css",
                "com/fr/bi/web/css/extend/excel/fieldset/excel.fieldset.table.css",
                "com/fr/bi/web/css/extend/excel/fieldset/combo/combo.fieldset.css",
                "com/fr/bi/web/css/extend/excel/fieldset/combo/item.excelfieldtype.css",
                "com/fr/bi/web/css/extend/excel/tipcombo/excel.tipcombo.css",

                //sql
                "com/fr/bi/web/css/extend/sql/sql.edit.css",

                "com/fr/bi/web/css/modules/datalink/widget.datalink.add.css",
                "com/fr/bi/web/css/modules/datalink/widget.datalink.schema.add.css",

                "com/fr/bi/web/css/modules/cubepath/widget.cubepath.css",
                "com/fr/bi/web/css/modules/cubepath/widget.cubepath.confirm.css",

                "com/fr/bi/web/css/modules/globalupdate/widget.globalupdate.setting.css",

                //权限相关
                "com/fr/bi/web/css/modules/permissionmanage/authoritypackagestree.css",
                "com/fr/bi/web/css/modules/permissionmanage/logininfo/widget.selectfieldmask.logininfo.css",
                "com/fr/bi/web/css/modules/permissionmanage/authorityset/authority.batchset.pane.css",
                "com/fr/bi/web/css/modules/permissionmanage/authorityset/authority.singleset.pane.css",
                "com/fr/bi/web/css/modules/permissionmanage/addrole/searcher.singleaddrole.css",
                "com/fr/bi/web/css/modules/permissionmanage/addrole/searcher.batchaddrole.css",
                "com/fr/bi/web/css/modules/permissionmanage/addrole/authority.singleaddrole.pane.css",
                "com/fr/bi/web/css/modules/permissionmanage/addrole/authority.batchaddrole.pane.css",

                //excelview设置
                "com/fr/bi/web/css/extend/excelviewsetting/tree/items/header.excelviewsetting.css",
                "com/fr/bi/web/css/extend/excelviewsetting/tree/items/item.excelviewsetting.css",
                "com/fr/bi/web/css/extend/excelviewsetting/tree/table.excelviewsetting.css",
                "com/fr/bi/web/css/extend/excelviewsetting/tree/expander.excelviewsetting.css",
                "com/fr/bi/web/css/extend/excelviewsetting/excel/excelviewsetting.cell.css",
                "com/fr/bi/web/css/extend/excelviewsetting/excel/excel.excelviewsetting.css",
                "com/fr/bi/web/css/extend/excelviewsetting/excelviewsetting.css",

                //更新设置
                "com/fr/bi/web/css/extend/update/update.tabledata.css",
                "com/fr/bi/web/css/extend/update/singletable/update.singletable.setting.css",
                "com/fr/bi/web/css/extend/update/singletable/iconchangetext.button.css",
                "com/fr/bi/web/css/extend/update/singletable/preview/update.previewpane.css",

                //全局更新
                "com/fr/bi/web/css/modules/globalupdate/widget.globalupdate.setting.css",

                //业务包字段
                "com/fr/bi/web/css/modules/tablefield/widget.tablefield.css",
                "com/fr/bi/web/css/modules/tablefield/widget.tablefield.searchresult.pane.css",
        };
    }

    public static String[] getConfCss() {
        String[] conf = getConfCssModule();
        return (String[]) ArrayUtils.addAll(conf, new String[]{

                //view model
                "com/fr/bi/web/css/conf/conf.view.css",
                "com/fr/bi/web/css/conf/businesspackages/conf.package.css",
                "com/fr/bi/web/css/conf/businesspackages/onepackage/conf.onePack.css",
                "com/fr/bi/web/css/conf/cube/conf.updatecube.css",
                "com/fr/bi/web/css/conf/datalink/conf.datalink.css",
                "com/fr/bi/web/css/conf/datalink/conf.datalink.add.css",
                "com/fr/bi/web/css/conf/businesspackages/onepackage/etl/conf.etl.css",
                "com/fr/bi/web/css/conf/businesspackages/onepackage/etl/addfield/conf.addfield.css",
                "com/fr/bi/web/css/conf/businesspackages/onepackage/etl/addfield/conf.addformulafieldfloatbox.css",
                "com/fr/bi/web/css/conf/businesspackages/onepackage/etl/addgroupfield/conf.addgroupfield.css",
                "com/fr/bi/web/css/conf/businesspackages/onepackage/etl/addgroupfield/conf.addgroupfieldfloatbox.css",
                "com/fr/bi/web/css/conf/businesspackages/onepackage/etl/partfield/conf.selectpartfield.css",
                "com/fr/bi/web/css/conf/businesspackages/onepackage/etl/filter/conf.filter.css",
                "com/fr/bi/web/css/conf/businesspackages/onepackage/etl/statistic/conf.groupandstatistic.css",
                "com/fr/bi/web/css/conf/businesspackages/onepackage/etl/union/conf.union.css",
                "com/fr/bi/web/css/conf/businesspackages/onepackage/etl/union/widget.union.previewtable.css",
                "com/fr/bi/web/css/conf/businesspackages/onepackage/etl/union/widget.union.previewtable.headercell.css",
                "com/fr/bi/web/css/conf/businesspackages/onepackage/etl/convert/conf.convert.css",
                "com/fr/bi/web/css/conf/businesspackages/onepackage/etl/join/conf.join.css",
                "com/fr/bi/web/css/conf/businesspackages/onepackage/etl/join/widget.join.previewtable.css",
                "com/fr/bi/web/css/conf/businesspackages/onepackage/etl/join/widget.join.previewtable.headercell.css",
                "com/fr/bi/web/css/conf/businesspackages/onepackage/etl/circle/conf.circle.css",
                "com/fr/bi/web/css/conf/multirelation/conf.multirelation.css",
                "com/fr/bi/web/css/conf/businesspackages/group/conf.packages.group.css",
                "com/fr/bi/web/css/conf/permissionmanage/conf.permission.manage.css",


        });
    }

    private static String[] getConfJsModule() {
        return new String[]{
                "com/fr/bi/web/js/modules/utils4conf.js",
                "com/fr/bi/web/js/fragments/cube/button.hoursetting.js",

                "com/fr/bi/web/js/modules/tablefield/button.relationtables.js",

                "com/fr/bi/web/js/modules/base/buttons/button.databasetable.js",


                //statistic
                "com/fr/bi/web/js/modules/base/combos/statistic/widget.statisticnumber.combo.js",
                "com/fr/bi/web/js/modules/base/combos/statistic/widget.statisticdate.combo.js",
                "com/fr/bi/web/js/modules/base/combos/statistic/widget.statisticstring.combo.js",


                //group
                "com/fr/bi/web/js/modules/base/combos/group/widget.groupnumber.combo.js",
                "com/fr/bi/web/js/modules/base/combos/group/widget.groupdate.combo.js",
                "com/fr/bi/web/js/modules/base/combos/group/widget.groupstring.combo.js",

                //表字段
                "com/fr/bi/web/js/modules/tablefield/widget.tablefield.js",
                "com/fr/bi/web/js/modules/tablefield/widget.tablefield.searchresult.pane.js",
                "com/fr/bi/web/js/modules/tablefield/widget.tablefieldwithsearch.pane.js",

                //选择单个表
                "com/fr/bi/web/js/modules/selectonetable/widget.selectonetable.pane.js",
                //选择表
                "com/fr/bi/web/js/modules/selecttable/widget.selecttable.pane.js",
                "com/fr/bi/web/js/modules/selecttable/linknames/widget.datalinkgroup.js",
                "com/fr/bi/web/js/modules/selecttable/widget.datalinktab.js",
                "com/fr/bi/web/js/modules/selecttable/sourcetable/widget.databasetables.mainpane.js",
                "com/fr/bi/web/js/modules/selecttable/sourcetable/widget.databasetables.pane.js",
                "com/fr/bi/web/js/modules/selecttable/sourcetable/widget.databasetables.pager.js",
                "com/fr/bi/web/js/modules/selecttable/sourcetable/widget.databasetables.searchresult.pane.js",
                "com/fr/bi/web/js/modules/selecttable/packagetable/widget.packagetables.mainpane.js",
                "com/fr/bi/web/js/modules/selecttable/packagetable/widget.packagetables.pane.js",
                "com/fr/bi/web/js/modules/selecttable/packagetable/widget.packagetables.searchresult.pane.js",
                "com/fr/bi/web/js/modules/selecttable/etltable/widget.etltables.pane.js",
                "com/fr/bi/web/js/modules/selecttable/etltable/widget.etlflowchart.button.js",

                //业务包分组
                "com/fr/bi/web/js/modules/businesspackagegroup/buttons/button.businesspackage.mange.js",
                "com/fr/bi/web/js/modules/businesspackagegroup/buttons/button.businesspackage.add.js",
                "com/fr/bi/web/js/modules/businesspackagegroup/expander.businesspackage.group.js",
                "com/fr/bi/web/js/modules/businesspackagegroup/pane.businesspackage.js",
                "com/fr/bi/web/js/modules/businesspackagegroup/pane.ungroup.and.group.businesspackage.js",
                "com/fr/bi/web/js/modules/businesspackagegroup/widget.group.businesspackage.js",
                "com/fr/bi/web/js/modules/businesspackagegroup/widget.manage.businesspackage.js",


                //etl
                //业务包面板
                "com/fr/bi/web/js/modules/onepackage/onepackagetablespane/widget.packagetables.pane.js",
                "com/fr/bi/web/js/modules/onepackage/onepackagetablespane/widget.packagesearcher.resultpane.js",

                //表关联视图
                "com/fr/bi/web/js/modules/onepackage/onepackagerelationspane/tablerelations.pane.js",
                "com/fr/bi/web/js/modules/onepackage/onepackagerelationspane/tablerelations.pane.model.js",

                //etl新增分组列
                "com/fr/bi/web/js/modules/onepackage/etl/addgroupfield/addgroupfield/widget.button.field.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/addgroupfield/addgroupfield/widget.combo.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/addgroupfield/addgroupfield/widget.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/addgroupfield/addgroupfield/widget.expander.group.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/addgroupfield/addgroupfield/widget.group2other.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/addgroupfield/addgroupfield/widget.node.arrow.delete.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/addgroupfield/addgroupfield/widget.pane.allfields.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/addgroupfield/addgroupfield/widget.pane.group.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/addgroupfield/addgroupfield/widget.search.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/addgroupfield/addgroupfield/widget.pane.searcher.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/addgroupfield/addgroupfield/widget.view.searcher.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/addgroupfield/widget.addgroupfield.js",
                "com/fr/bi/web/js/modules/onepackage/etl/addgroupfield/widget.addgroupfield.model.js",
                "com/fr/bi/web/js/modules/onepackage/etl/addgroupfield/widget.addgroupfield.popover.js",
                "com/fr/bi/web/js/modules/onepackage/etl/addgroupfield/widget.addgroupfield.popover.model.js",

                //etl新增公式列widget
                "com/fr/bi/web/js/modules/onepackage/etl/addformulafield/widget.addformulafield.js",
                "com/fr/bi/web/js/modules/onepackage/etl/addformulafield/widget.addformulafield.model.js",
                "com/fr/bi/web/js/modules/onepackage/etl/addformulafield/widget.addformulafield.popover.js",
                "com/fr/bi/web/js/modules/onepackage/etl/addformulafield/widget.addformulafield.popover.model.js",

                //etl流程图
                "com/fr/bi/web/js/modules/onepackage/etl/flowchart/widget.etltable.combo.js",
                "com/fr/bi/web/js/modules/onepackage/etl/flowchart/widget.etltable.operator.js",
                "com/fr/bi/web/js/modules/onepackage/etl/flowchart/widget.etltables.pane.js",


                //union
                "com/fr/bi/web/js/modules/onepackage/etl/union/union/widget.union.previewtable.js",
                "com/fr/bi/web/js/modules/onepackage/etl/union/union/widget.union.previewtable.headercell.js",
                "com/fr/bi/web/js/modules/onepackage/etl/union/union/widget.addunion.table.js",
                "com/fr/bi/web/js/modules/onepackage/etl/union/union/widget.unionresult.header.js",
                "com/fr/bi/web/js/modules/onepackage/etl/union/widget.union.js",
                "com/fr/bi/web/js/modules/onepackage/etl/union/widget.union.model.js",

                //join
                "com/fr/bi/web/js/modules/onepackage/etl/join/join/widget.jointype.group.js",
                "com/fr/bi/web/js/modules/onepackage/etl/join/join/widget.jointype.button.js",
                "com/fr/bi/web/js/modules/onepackage/etl/join/join/widget.joinresult.header.js",
                "com/fr/bi/web/js/modules/onepackage/etl/join/join/widget.join.previewtable.headercell.js",
                "com/fr/bi/web/js/modules/onepackage/etl/join/join/widget.join.previewtable.js",
                "com/fr/bi/web/js/modules/onepackage/etl/join/widget.join.js",
                "com/fr/bi/web/js/modules/onepackage/etl/join/widget.join.model.js",

                //etl预览
                "com/fr/bi/web/js/modules/onepackage/etl/preview/widget.etltable.preview.js",
                "com/fr/bi/web/js/modules/onepackage/etl/preview/widget.etltable.preview.center.js",

                //etl关联
                "com/fr/bi/web/js/modules/onepackage/etl/relation/pane/widget.relationpane.js",
                "com/fr/bi/web/js/modules/onepackage/etl/relation/pane/widget.relationpane.model.js",
                "com/fr/bi/web/js/modules/onepackage/etl/relation/pane/tools/widget.relation.settingtable.js",
                "com/fr/bi/web/js/modules/onepackage/etl/relation/pane/tools/widget.relationset.group.js",
                "com/fr/bi/web/js/modules/onepackage/etl/relation/pane/tools/button.relationtable.field.js",
                "com/fr/bi/web/js/modules/onepackage/etl/relation/pane/selectdatamask/widget.selectdata.mask.js",
                "com/fr/bi/web/js/modules/onepackage/etl/relation/pane/selectdata4relation/widget.selectsinglefield.relation.js",
                "com/fr/bi/web/js/modules/onepackage/etl/relation/widget.setrelation.pane.js",

                //etl过滤
                "com/fr/bi/web/js/modules/onepackage/etl/filter/filteritem/field/factory.filteritem.field.js",
                "com/fr/bi/web/js/modules/onepackage/etl/filter/filteritem/field/item.datefield.js",
                "com/fr/bi/web/js/modules/onepackage/etl/filter/filteritem/field/item.notypefield.js",
                "com/fr/bi/web/js/modules/onepackage/etl/filter/filteritem/field/item.numberfield.js",
                "com/fr/bi/web/js/modules/onepackage/etl/filter/filteritem/field/item.stringfield.js",
                "com/fr/bi/web/js/modules/onepackage/etl/filter/filteritem/formula/item.emptyformula.js",
                "com/fr/bi/web/js/modules/onepackage/etl/filter/filteritem/formula/item.formula.js",
                "com/fr/bi/web/js/modules/onepackage/etl/filter/filter.expander.js",
                "com/fr/bi/web/js/modules/onepackage/etl/filter/filter.fieldandformula.js",
                "com/fr/bi/web/js/modules/onepackage/etl/filter/filter.selectsinglefield.js",
                "com/fr/bi/web/js/modules/onepackage/etl/filter/filter.multiselect.js",
                "com/fr/bi/web/js/modules/onepackage/etl/filter/widget.filter.js",
                "com/fr/bi/web/js/modules/onepackage/etl/filter/widget.filter.model.js",

                //增加公式列
                "com/fr/bi/web/js/modules/onepackage/etl/confaddfield/widget.button.formulafield.js",
                "com/fr/bi/web/js/modules/onepackage/etl/confaddfield/widget.formulalist.js",

                //行列转化
                "com/fr/bi/web/js/modules/onepackage/etl/convert/convert/convert.selectFieldsDataPane.js",
                "com/fr/bi/web/js/modules/onepackage/etl/convert/convert/convert.initialFields.js",
                "com/fr/bi/web/js/modules/onepackage/etl/convert/convert/convert.genFields.js",
                "com/fr/bi/web/js/modules/onepackage/etl/convert/convert/convert.displaylabel.js",
                "com/fr/bi/web/js/modules/onepackage/etl/convert/convert/convert.listItem.js",
                "com/fr/bi/web/js/modules/onepackage/etl/convert/widget.convert.js",
                "com/fr/bi/web/js/modules/onepackage/etl/convert/widget.convert.model.js",

                //自循环列
                "com/fr/bi/web/js/modules/onepackage/etl/circle/circle/operatorpane/tab/circle.tab.region.js",
                "com/fr/bi/web/js/modules/onepackage/etl/circle/circle/operatorpane/tab/circle.select.field.button.js",
                "com/fr/bi/web/js/modules/onepackage/etl/circle/circle/operatorpane/tab/circle.tab.js",
                "com/fr/bi/web/js/modules/onepackage/etl/circle/circle/operatorpane/tab/circle.two.condition.switch.js",
                "com/fr/bi/web/js/modules/onepackage/etl/circle/circle/operatorpane/tab/circle.one.region.popup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/circle/circle/operatorpane/tab/circle.two.region.popup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/circle/circle/operatorpane/circle.operator.pane.js",
                "com/fr/bi/web/js/modules/onepackage/etl/circle/circle/levelpane/circle.level.pane.js",
                "com/fr/bi/web/js/modules/onepackage/etl/circle/circle/circle.self.js",
                "com/fr/bi/web/js/modules/onepackage/etl/circle/circle/operatorpane/circle.showtextcombo.js",
                "com/fr/bi/web/js/modules/onepackage/etl/circle/circle/levelpane/circle.display.editor.js",
                "com/fr/bi/web/js/modules/onepackage/etl/circle/circle/circle.result.pane.js",
                "com/fr/bi/web/js/modules/onepackage/etl/circle/widget.circle.js",
                "com/fr/bi/web/js/modules/onepackage/etl/circle/widget.circle.model.js",


                //分组
                "com/fr/bi/web/js/modules/onepackage/etl/group/group.select.fields.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/group.select.fields.item.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/group.select.fields.item.group.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/number/widget.customgroup.number.combo.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/number/widget.customgroup.number.group.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/number/widget.customgroup.number.item.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/number/widget.customgroup.number.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/number/widget.customgroup.number.other.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/number/widget.customgroup.number.panel.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/number/widget.customgroup.number.tab.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/customgroup/widget.group2other.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/customgroup/widget.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/customgroup/widget.combo.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/customgroup/widget.search.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/customgroup/widget.node.arrow.delete.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/customgroup/widget.expander.group.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/customgroup/widget.button.field.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/customgroup/widget.pane.group.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/customgroup/widget.pane.allfields.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/customgroup/widget.pane.searcher.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/customgroup/widget.view.searcher.customgroup.js",


                //部分字段
                "com/fr/bi/web/js/modules/onepackage/etl/partfield/partfield/widget.selectpartfieldlist.js",
                "com/fr/bi/web/js/modules/onepackage/etl/partfield/widget.partfield.js",
                "com/fr/bi/web/js/modules/onepackage/etl/partfield/widget.partfield.model.js",

                //分组统计
                "com/fr/bi/web/js/modules/onepackage/etl/statistic/customgroup/customgroup.statistic.js",
                "com/fr/bi/web/js/modules/onepackage/etl/statistic/field/abstract.dimension.js",
                "com/fr/bi/web/js/modules/onepackage/etl/statistic/field/date.group.dimension.js",
                "com/fr/bi/web/js/modules/onepackage/etl/statistic/field/date.statistic.dimension.js",
                "com/fr/bi/web/js/modules/onepackage/etl/statistic/field/number.group.dimension.js",
                "com/fr/bi/web/js/modules/onepackage/etl/statistic/field/number.statistic.dimension.js",
                "com/fr/bi/web/js/modules/onepackage/etl/statistic/field/string.statistic.dimension.js",
                "com/fr/bi/web/js/modules/onepackage/etl/statistic/field/string.group.dimension.js",
                "com/fr/bi/web/js/modules/onepackage/etl/statistic/numbercustomgroup/numbercustomgroup.statistic.js",
                "com/fr/bi/web/js/modules/onepackage/etl/statistic/widget.statistic.js",
                "com/fr/bi/web/js/modules/onepackage/etl/statistic/region/region.string.js",
                "com/fr/bi/web/js/modules/onepackage/etl/statistic/widget.statistic.model.js",

                //etl
                "com/fr/bi/web/js/modules/onepackage/etl/widget.etl.model.js",
                "com/fr/bi/web/js/modules/onepackage/etl/widget.etl.js",

                //one package
                "com/fr/bi/web/js/modules/onepackage/widget.onepackage.js",
                "com/fr/bi/web/js/modules/onepackage/widget.onepackage.model.js",
                "com/fr/bi/web/js/modules/onepackage/etl/refreshfields/widget.refreshtablefields.js",


                //FineBI Service
                "com/fr/bi/web/js/modules/finebiservice/expander.finebiservice.js",
                "com/fr/bi/web/js/modules/finebiservice/finebiservice.js",

                //excel
                "com/fr/bi/web/js/extend/excel/upload/excel.upload.js",
                "com/fr/bi/web/js/extend/excel/upload/excel.upload.model.js",
                "com/fr/bi/web/js/extend/excel/upload/fieldset/excel.fieldset.js",
                "com/fr/bi/web/js/extend/excel/upload/fieldset/excel.fieldset.table.js",
                "com/fr/bi/web/js/extend/excel/upload/fieldset/combo/combo.fieldset.js",
                "com/fr/bi/web/js/extend/excel/upload/fieldset/combo/trigger.fieldset.js",
                "com/fr/bi/web/js/extend/excel/upload/fieldset/combo/item.excelfieldtype.js",
                "com/fr/bi/web/js/extend/excel/upload/fieldset/combo/popup.fieldset.js",

                "com/fr/bi/web/js/extend/sql/editsql/sql.edit.js",
                "com/fr/bi/web/js/extend/sql/editsql/sql.edit.model.js",
                //etl plugin
                "com/fr/bi/web/js/extend/excel/etl.excel.plugin.js",
                //sql plugin
                "com/fr/bi/web/js/extend/sql/etl.sql.plugin.js",

                //多路径设置
                "com/fr/bi/web/js/fragments/multirelation/expander.multirelation.js",
                "com/fr/bi/web/js/fragments/multirelation/multirelation.js",
                "com/fr/bi/web/js/fragments/multirelation/item.multirelation.js",
                "com/fr/bi/web/js/fragments/multirelation/item.tablefield.multirelation.js",
                "com/fr/bi/web/js/fragments/multirelation/view.searcher.multirelation.js",

                //数据连接
                "com/fr/bi/web/js/modules/datalink/widget.testlink.loading.js",
                "com/fr/bi/web/js/modules/datalink/combo.adddatalink.js",
                "com/fr/bi/web/js/modules/datalink/normal/widget.datalink.add.js",
                "com/fr/bi/web/js/modules/datalink/normal/widget.datalink.add.model.js",
                "com/fr/bi/web/js/modules/datalink/schema/widget.datalink.schema.add.js",
                "com/fr/bi/web/js/modules/datalink/schema/widget.datalink.schema.add.model.js",

                "com/fr/bi/web/js/modules/cubepath/widget.cubepath.js",
                "com/fr/bi/web/js/modules/cubepath/widget.cubepath.confirm.js",

                //cube日志
                "com/fr/bi/web/js/modules/cubelog/items/item.cubelog.js",
                "com/fr/bi/web/js/modules/cubelog/items/title.item.wronginfo.cubelog.js",
                "com/fr/bi/web/js/modules/cubelog/items/item.wronginfo.cubelog.js",
                "com/fr/bi/web/js/modules/cubelog/nodes/node.cubelog.js",
                "com/fr/bi/web/js/modules/cubelog/nodes/node.wronginfo.cubelog.js",
                "com/fr/bi/web/js/modules/cubelog/popup/popup.cubelog.js",
                "com/fr/bi/web/js/modules/cubelog/expander/expander.cubelog.js",
                "com/fr/bi/web/js/modules/cubelog/tree.cubelog.js",
                "com/fr/bi/web/js/modules/cubelog/cubelog.js",


                //全局更新
                "com/fr/bi/web/js/modules/globalupdate/widget.globalupdate.setting.js",
                "com/fr/bi/web/js/modules/globalupdate/item/item.timesetting.js",

                //excelview设置
                "com/fr/bi/web/js/extend/excelviewsetting/tree/items/header.excelviewsetting.js",
                "com/fr/bi/web/js/extend/excelviewsetting/tree/items/item.excelviewsetting.js",
                "com/fr/bi/web/js/extend/excelviewsetting/tree/table.excelviewsetting.js",
                "com/fr/bi/web/js/extend/excelviewsetting/tree/expander.excelviewsetting.js",
                "com/fr/bi/web/js/extend/excelviewsetting/tree/tree.excelviewsetting.js",
                "com/fr/bi/web/js/extend/excelviewsetting/excel/excelviewsetting.cell.js",
                "com/fr/bi/web/js/extend/excelviewsetting/excel/excel.excelviewsetting.js",
                "com/fr/bi/web/js/extend/excelviewsetting/model.excelviewsetting.js",
                "com/fr/bi/web/js/extend/excelviewsetting/excelviewsetting.js",

                //权限控制
                "com/fr/bi/web/js/modules/permissionmanage/authoritypackagestree.js",
                "com/fr/bi/web/js/modules/permissionmanage/authorityset/authority.singleset.pane.js",
                "com/fr/bi/web/js/modules/permissionmanage/authorityset/authority.batchset.pane.js",
                "com/fr/bi/web/js/modules/permissionmanage/addrole/authority.singleaddrole.pane.js",
                "com/fr/bi/web/js/modules/permissionmanage/addrole/authority.batchaddrole.pane.js",
                "com/fr/bi/web/js/modules/permissionmanage/addrole/searcher.batchaddrole.js",
                "com/fr/bi/web/js/modules/permissionmanage/addrole/searcher.singleaddrole.js",
                "com/fr/bi/web/js/modules/permissionmanage/logininfo/widget.selectsinglefield.logininfo.js",
                "com/fr/bi/web/js/modules/permissionmanage/logininfo/widget.selectfieldmask.logininfo.js",

                //表更新
                "com/fr/bi/web/js/extend/update/update.tabledata.js",
                "com/fr/bi/web/js/extend/update/update.tabledata.model.js",
                "com/fr/bi/web/js/extend/update/singletable/item/item.singletable.timesetting.js",
                "com/fr/bi/web/js/extend/update/singletable/button/updatetype.button.js",
                "com/fr/bi/web/js/extend/update/singletable/button/iconchangetext.button.js",
                "com/fr/bi/web/js/extend/update/singletable/update.singletable.setting.js",
                "com/fr/bi/web/js/extend/update/singletable/update.singletable.setting.model.js",
                "com/fr/bi/web/js/extend/update/singletable/preview/update.previewpane.js",
                "com/fr/bi/web/js/extend/update/singletable/preview/update.previewpane.model.js",
        };
    }

    public static String[] getConfJs() {
        String[] conf = getConfJsModule();
        return (String[]) ArrayUtils.addAll(conf, new String[]{

                //view model
                "com/fr/bi/web/js/conf/conf.start.js",
                "com/fr/bi/web/js/conf/model.js",
                "com/fr/bi/web/js/conf/view.js",
                "com/fr/bi/web/js/conf/modules/conf.floatbox.manage.js",
                "com/fr/bi/web/js/conf/modules/conf.model.manage.js",
                "com/fr/bi/web/js/conf/modules/conf.view.manage.js",

                "com/fr/bi/web/js/conf/modules/model/conf.model.js",
                "com/fr/bi/web/js/conf/modules/model/businesspackage/manage/model.packages.group.js",
                "com/fr/bi/web/js/conf/modules/model/businesspackage/model.packages.manage.js",
                "com/fr/bi/web/js/conf/modules/model/permissionmanage/model.permission.manage.js",
                "com/fr/bi/web/js/conf/modules/model/cube/model.updatecube.js",
                "com/fr/bi/web/js/conf/modules/model/cube/model.cubepath.js",
                "com/fr/bi/web/js/conf/modules/model/cube/model.cubelog.js",
                "com/fr/bi/web/js/conf/modules/model/dataLink/model.datalink.js",
                "com/fr/bi/web/js/conf/modules/model/multirelation/model.multirelation.js",
                "com/fr/bi/web/js/conf/modules/model/etl/model.tableset.js",
                "com/fr/bi/web/js/conf/modules/model/dataLink/model.datalink.add.js",
                "com/fr/bi/web/js/conf/modules/view/conf.view.js",
                "com/fr/bi/web/js/conf/modules/view/businesspackage/conf.packages.manage.js",
                "com/fr/bi/web/js/conf/modules/view/businesspackage/group/conf.packages.group.js",
                "com/fr/bi/web/js/conf/modules/view/cube/conf.updatecube.js",
                "com/fr/bi/web/js/conf/modules/view/cube/conf.cubepath.js",
                "com/fr/bi/web/js/conf/modules/view/cube/conf.cubelog.js",
                "com/fr/bi/web/js/conf/modules/view/dataLink/conf.datalink.js",
                "com/fr/bi/web/js/conf/modules/view/multirelation/conf.multirelation.js",
                "com/fr/bi/web/js/conf/modules/view/dataLink/conf.datalink.add.js",
                "com/fr/bi/web/js/conf/modules/view/permissionmanage/conf.permission.manage.js"
        });
    }
}
