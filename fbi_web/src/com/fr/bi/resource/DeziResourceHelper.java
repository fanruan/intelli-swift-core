package com.fr.bi.resource;

import com.fr.stable.ArrayUtils;

/**
 * Created by Wang on 2017/1/18.
 */
public class DeziResourceHelper {
    public static String[] getDeziCssModule() {
        return new String[]{
                //驾驶舱相关模块
                "com/fr/bi/web/css/modules/base/combos/widget.combo.css",

                //明细表字段关联设置
                "com/fr/bi/web/css/modules/fieldrelationsetting/item.commontable.set.detailtable.css",
                "com/fr/bi/web/css/modules/fieldrelationsetting/tab.path.setting.detailtable.css",
                "com/fr/bi/web/css/modules/fieldrelationsetting/fieldrelationsettingpopup.css",

                //下拉树控件字段关联设置
                "com/fr/bi/web/css/modules/fieldrelationsettingwithpreviewpopup/fieldrelationsettingwithpreviewpopup.css",

                //业务人员上传excel
                "com/fr/bi/web/css/modules/updateexcel/updateexcelcombo.css",
                "com/fr/bi/web/css/modules/updateexcel/updateexcelpopup.css",
                "com/fr/bi/web/css/modules/updateexcel/button/stateiconbutton.css",
                "com/fr/bi/web/css/modules/updateexcel/excelfieldstable/updateexcelfieldtable.css",
                "com/fr/bi/web/css/modules/updateexcel/messagepane/failpane.css",
                "com/fr/bi/web/css/modules/updateexcel/messagepane/successpane.css",

                //选择字段
                "com/fr/bi/web/css/modules/selectdata/tab.selectdata.css",
                "com/fr/bi/web/css/modules/selectdata/preview/pane.preview.selectdata.css",
                "com/fr/bi/web/css/modules/selectdata/treeitem4reusedimension/calctarget.item.level0.css",

                "com/fr/bi/web/css/modules/selectdata4filter/node/node.level0.dimension.css",

                //通用查询选字段
                "com/fr/bi/web/css/modules/selectdata4generalquery/widget.generalquery.usedfields.pane.css",
                //选择文本
                "com/fr/bi/web/css/modules/selectstring/tab.selectstring.css",

                //选择日期
                "com/fr/bi/web/css/modules/selectdate/tab.selectdate.css",

                //详细设置相关模块
                "com/fr/bi/web/css/modules/dimensionsmanager/charttype/combo/combo.tabletype.css",
                "com/fr/bi/web/css/modules/dimensionsmanager/charttype/charttype.css",
                "com/fr/bi/web/css/modules/dimensionsmanager/regionsmanager/header/region.header.css",
                "com/fr/bi/web/css/modules/dimensionsmanager/regionsmanager/wrapper/dimensionregions/region.empty.dimension.css",
                "com/fr/bi/web/css/modules/dimensionsmanager/regionsmanager/wrapper/dimensionregions/region.dimension.css",
                "com/fr/bi/web/css/modules/dimensionsmanager/regionsmanager/wrapper/targetregions/region.empty.target.css",
                "com/fr/bi/web/css/modules/dimensionsmanager/regionsmanager/wrapper/targetregions/region.target.css",
                "com/fr/bi/web/css/modules/dimensionsmanager/regionsmanager/wrapper/targetregions/region.target.settings.css",
                "com/fr/bi/web/css/modules/dimensionsmanager/regionsmanager/wrapper/scopes/scope.target.combine.chart.css",
                "com/fr/bi/web/css/modules/dimensionsmanager/regionsmanager/wrapper/regionwrapper.dimension.css",
                "com/fr/bi/web/css/modules/dimensionsmanager/regionsmanager/wrapper/regionwrapper.target.css",
                "com/fr/bi/web/css/modules/dimensionsmanager/regionsmanager/wrapper/regionwrapper.target.settings.css",
                "com/fr/bi/web/css/modules/dimensionsmanager/regionsmanager/regionsmanager.css",
                "com/fr/bi/web/css/modules/dimensionsmanager/dimensionsmanager.css",


                //数值类型自定义分组
                "com/fr/bi/web/css/modules/numberintervalcustomgroup/widget.customgroup.number.group.css",
                "com/fr/bi/web/css/modules/numberintervalcustomgroup/widget.customgroup.number.other.css",
                "com/fr/bi/web/css/modules/numberintervalcustomgroup/widget.customgroup.number.tab.css",
                "com/fr/bi/web/css/modules/numberintervalcustomgroup/widget.customgroup.number.combo.css",
                "com/fr/bi/web/css/modules/numberintervalcustomgroup/widget.customgroup.number.item.css",
                "com/fr/bi/web/css/modules/numberintervalcustomgroup/widget.customgroup.number.panel.css",

                //自定义排序
                "com/fr/bi/web/css/modules/customsort/widget.pane.customsort.css",

                //维度与指标的匹配关系
                "com/fr/bi/web/css/modules/matchingrelationship/infopane/settargetregion.infopane.css",
                "com/fr/bi/web/css/modules/matchingrelationship/infopane/unsettingtargetregion.infopane.css",
                "com/fr/bi/web/css/modules/matchingrelationship/settingpane/dimensiontreecombo/dimensiontree.trigger.css",
                "com/fr/bi/web/css/modules/matchingrelationship/settingpane/dimensiontreecombo/dimensiontree.combo.css",
                "com/fr/bi/web/css/modules/matchingrelationship/settingpane/targetlabelcontrol.settingpane.css",
                "com/fr/bi/web/css/modules/matchingrelationship/settingpane/settingpane.css",
                "com/fr/bi/web/css/modules/matchingrelationship/settingpane/multipathchooser.settingpane.css",
                "com/fr/bi/web/css/modules/matchingrelationship/settingpane/multimatchmulti.settingpane.css",


                //查看真实数据
                "com/fr/bi/web/css/fragments/base/items/widget.realdatacheckbox.css",

                //计算指标
                "com/fr/bi/web/css/modules/calculatetarget/calculatetargetpopup.css",
                "com/fr/bi/web/css/modules/calculatetarget/pane.calculate.target.rank.css",

                //系列堆积
                "com/fr/bi/web/css/modules/seriesaccumulation/container.accumulation.css",
                "com/fr/bi/web/css/modules/seriesaccumulation/group.accumulation.css",
                "com/fr/bi/web/css/modules/seriesaccumulation/series.accumulation.css",
                "com/fr/bi/web/css/modules/seriesaccumulation/type.select.button.css",


                //表格样式
                "com/fr/bi/web/css/modules/chartsetting/grouptable/widget.grouptable.setting.css",
                "com/fr/bi/web/css/modules/chartsetting/crosstable/widget.crosstable.setting.css",

                //图样式
                "com/fr/bi/web/css/modules/chartsetting/charts/customscale/formula/customscale.formula.fieldtree.css",
                "com/fr/bi/web/css/modules/chartsetting/charts/customscale/formula/customscale.formula.css",
                "com/fr/bi/web/css/modules/chartsetting/charts/customscale/combo.customscale.css",
                "com/fr/bi/web/css/modules/chartsetting/charts/customscale/trigger.customscale.css",
                "com/fr/bi/web/css/modules/chartsetting/charts/detailedsetting/detailed.setting.css",
                "com/fr/bi/web/css/modules/chartsetting/charts/detailedsetting/detailed.setting.trigger.css",
                "com/fr/bi/web/css/modules/chartsetting/charts/selectcolorcombo/item.selectcolor.css",
                "com/fr/bi/web/css/modules/chartsetting/charts/selectcolorcombo/trigger.selectcolor.css",
                "com/fr/bi/web/css/modules/chartsetting/charts/charts.setting.css",

                //警戒线
                "com/fr/bi/web/css/modules/cordon/pane/item.cordon.css",
                "com/fr/bi/web/css/modules/cordon/pane/pane.cordon.css",

                //指标样式
                "com/fr/bi/web/css/modules/targetstyle/widget.targetstylesetting.css",
                "com/fr/bi/web/css/modules/targetstyle/widget.targetcondition.stylesetting.css",
                "com/fr/bi/web/css/modules/targetstyle/widget.stylesetting.iconmark.css",

                //数据标签
                "com/fr/bi/web/css/modules/datalabel/datalabel.css",
                "com/fr/bi/web/css/modules/datalabel/condition/datalabel.condition.css",
                "com/fr/bi/web/css/modules/datalabel/condition/datalabel.conditionitem.css",
                "com/fr/bi/web/css/modules/datalabel/tab/datalabel.imageset.css",
                "com/fr/bi/web/css/modules/datalabel/tab/datalabel.texttoolbar.css",
                "com/fr/bi/web/css/modules/datalabel/tab/datalabel.barchart.css",

                //组件过滤面板
                "com/fr/bi/web/css/modules/widgetfilter/widget.widgetfilter.css",
                "com/fr/bi/web/css/modules/widgetfilter/item.linkagefilter.css",
                "com/fr/bi/web/css/modules/widgetfilter/item.targetfilter.css",
                "com/fr/bi/web/css/modules/widgetfilter/item.dimensionfilter.css",
                "com/fr/bi/web/css/modules/widgetfilter/item.controlfilter.css",
                "com/fr/bi/web/css/modules/widgetfilter/item.drillfilter.css",

                //维度下拉
                "com/fr/bi/web/css/modules/base/combos/dimension/dimension/abstract.dimension.combo.css",

                //带参数的日期控件
                "com/fr/bi/web/css/modules/multidatecombowithparam/multidate.parampopup.css",
                "com/fr/bi/web/css/modules/multidatecombowithparam/multidate.parampane.css",
                "com/fr/bi/web/css/modules/multidatecombowithparam/multidate.paramtrigger.css",
                "com/fr/bi/web/css/modules/paramtimeinterval/timeinterval.param.css",

                //表格上的单元格
                "com/fr/bi/web/css/modules/tablechartmanager/datatable/tablecell/normal/headercell.normal.css",
                "com/fr/bi/web/css/modules/tablechartmanager/datatable/tablecell/normal/cell.tarbody.normal.css",
                "com/fr/bi/web/css/modules/tablechartmanager/datatable/tablecell/normal/expandercell.normal.css",
                "com/fr/bi/web/css/modules/tablechartmanager/datatable/widget.summarytable.css",
                "com/fr/bi/web/css/modules/tablechartmanager/datatable/combo/sortfilter.combo.css",
                "com/fr/bi/web/css/modules/tablechartmanager/tablechartmanager.css",
                "com/fr/bi/web/css/modules/tablechartmanager/errorpane/tablechart.errorpane.css",

                //自适应布局
                "com/fr/bi/web/css/modules/fit/widgetchooser/widget.dragicongroup.css",
                "com/fr/bi/web/css/modules/fit/fit.widget.css",
                "com/fr/bi/web/css/modules/fit/fit.css",

                //复用面板
                "com/fr/bi/web/css/modules/fit/widgetchooser/reuse/pane.reuse.css",


                //明细表超级链接
                "com/fr/bi/web/css/modules/hyperlink/hyperlink.insert.css",

                //明细表表格
                "com/fr/bi/web/css/modules/detailtable/widget.detailtable.css",
                "com/fr/bi/web/css/modules/detailtable/cell/header.detailtable.css",
                "com/fr/bi/web/css/modules/detailtable/cell/cell.detailtable.css",


                //联动
                "com/fr/bi/web/css/modules/linkage/linkage.target.css",
                "com/fr/bi/web/css/modules/linkage/linkage.css",

                //另存为
                "com/fr/bi/web/css/modules/saveas/popup.saveas.css",

                //图表钻取
                "com/fr/bi/web/css/modules/chartdrill/cell.chartdrill.css",
                "com/fr/bi/web/css/modules/chartdrill/button.pushdrill.css",
                "com/fr/bi/web/css/modules/chartdrill/widget.chartdrill.css",

                //excelview
                "com/fr/bi/web/css/extend/excelview/excelview.cell.css",
                "com/fr/bi/web/css/extend/excelview/excelview.css",

                //最大化
                "com/fr/bi/web/css/modules/maximization/widget.maximization.chartpane.css",
                "com/fr/bi/web/css/modules/maximization/widget.maximization.css",

                //日期面板
                "com/fr/bi/web/css/modules/datepane/datepane.css",
        };
    }

    public static String[] getDeziCss() {
        String[] dezi = getDeziCssModule();
        return (String[]) ArrayUtils.addAll(dezi, new String[]{
                "com/fr/bi/web/css/dezi/dezi.view.css",
                "com/fr/bi/web/css/dezi/pane/dezi.pane.css",
                "com/fr/bi/web/css/dezi/pane/widgets/dezi.widgets.css",
                "com/fr/bi/web/css/dezi/pane/widgets/detail/dezi.detail.css",
                "com/fr/bi/web/css/dezi/pane/widgets/detail/region/dezi.region.css",
                "com/fr/bi/web/css/dezi/pane/widgets/detail/region/field/dezi.dimension.css",
                "com/fr/bi/web/css/dezi/pane/widgets/detail/region/field/dezi.target.css"
        });
    }

    public static String[] getDeziJsModule() {
        return new String[]{
                "com/fr/bi/web/js/fragments/base/tabs/widget.datastyletab.js",
                "com/fr/bi/web/js/fragments/base/items/widget.realdatacheckbox.js",

                "com/fr/bi/web/js/modules/base/combos/widget.combo.js",
                "com/fr/bi/web/js/modules/base/combos/dimension/abstract.dimensiontarget.combo.js",
                "com/fr/bi/web/js/modules/base/combos/dimension/dimension/abstract.dimension.combo.js",
                "com/fr/bi/web/js/modules/base/combos/widget.controldimension.combo.js",
                "com/fr/bi/web/js/modules/base/combos/dimension/dimension/widget.stringdimension.combo.js",

                "com/fr/bi/web/js/modules/base/combos/dimension/dimension/widget.numberdimension.combo.js",
                "com/fr/bi/web/js/modules/base/combos/dimension/dimension/widget.datedimension.combo.js",
                "com/fr/bi/web/js/modules/base/combos/dimension/target/widget.target.combo.js",
                "com/fr/bi/web/js/modules/base/combos/dimension/target/widget.count.target.combo.js",
                "com/fr/bi/web/js/modules/base/combos/dimension/target/widget.calculate.target.combo.js",
                "com/fr/bi/web/js/modules/base/combos/detail/widget.detailstring.combo.js",
                "com/fr/bi/web/js/modules/base/combos/detail/widget.detailnumber.combo.js",
                "com/fr/bi/web/js/modules/base/combos/detail/widget.detaildate.combo.js",
                "com/fr/bi/web/js/modules/base/combos/detail/widget.detailformula.combo.js",

                //数值区间自定义分组forDezi
                "com/fr/bi/web/js/modules/numberintervalcustomgroup/widget.customgroup.number.combo.js",
                "com/fr/bi/web/js/modules/numberintervalcustomgroup/widget.customgroup.number.group.js",
                "com/fr/bi/web/js/modules/numberintervalcustomgroup/widget.customgroup.number.item.js",
                "com/fr/bi/web/js/modules/numberintervalcustomgroup/widget.customgroup.number.js",
                "com/fr/bi/web/js/modules/numberintervalcustomgroup/widget.customgroup.number.other.js",
                "com/fr/bi/web/js/modules/numberintervalcustomgroup/widget.customgroup.number.tab.js",
                "com/fr/bi/web/js/modules/numberintervalcustomgroup/widget.customgroup.number.panel.js",

                //详细设置相关模块
                "com/fr/bi/web/js/modules/dimensionsmanager/charttype/combo/combo.tablecharttype.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/charttype/combo/maptypecombo/combo.maptype.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/charttype/combo/maptypecombo/popup.maptype.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/charttype/charttype.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/header/region.header.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/header/region.header.calculatetarget.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/header/region.header.detail.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/header/region.header.tree.js",

                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/scopes/scope.target.combine.chart.js",

                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/wrapper/dimensionregions/abstract.region.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/wrapper/dimensionregions/region.dimension.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/wrapper/dimensionregions/region.empty.dimension.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/wrapper/targetregions/region.target.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/wrapper/targetregions/region.empty.target.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/wrapper/targetregions/region.target.settings.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/wrapper/region.wrapper.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/wrapper/regionwrapper.dimension.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/wrapper/regionwrapper.target.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/wrapper/regionwrapper.target.settings.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.table.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.crosstable.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.complextable.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.table.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.accumulate.area.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.accumulate.axis.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.accumulate.bar.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.accumulate.radar.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.area.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.axis.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.bar.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.bubble.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.combine.chart.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.compare.area.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.compare.axis.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.compare.bar.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.dashboard.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.donut.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.fall.axis.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.force.bubble.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.funnel.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.pareto.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.heat.map.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.gis.map.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.line.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.map.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.multi.axis.combine.chart.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.multi.pie.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.percent.accumulate.area.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.percent.accumulate.axis.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.pie.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.radar.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.range.area.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.rect.tree.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.scatter.js",

                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.string.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.number.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.date.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.tree.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager/regionsmanager.detailtable.js",

                "com/fr/bi/web/js/modules/dimensionsmanager/model.dimensionsmanager.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/dimensionsmanager.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/dimensionsmanager.string.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/dimensionsmanager.number.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/dimensionsmanager.date.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/dimensionsmanager.detailtable.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/dimensionsmanager.tree.js",

                //业务人员上传excel
                "com/fr/bi/web/js/modules/updateexcel/model.updateexcel.js",
                "com/fr/bi/web/js/modules/updateexcel/updateexcelcombo.js",
                "com/fr/bi/web/js/modules/updateexcel/updateexcelpopup.js",
                "com/fr/bi/web/js/modules/updateexcel/updateexceltrigger.js",
                "com/fr/bi/web/js/modules/updateexcel/button/stateiconbutton.js",
                "com/fr/bi/web/js/modules/updateexcel/excelfieldtable/updateexcelfieldtable.js",
                "com/fr/bi/web/js/modules/updateexcel/messagepane/failpane.js",
                "com/fr/bi/web/js/modules/updateexcel/messagepane/successpane.js",

                //自定义排序
                "com/fr/bi/web/js/modules/customsort/widget.pane.customsort.js",

                //详细设置中的选择字段
                "com/fr/bi/web/js/modules/selectdata/preview/pane.preview.selectdata.js",
                "com/fr/bi/web/js/modules/selectdata/preview/section.preview.selectdata.js",
                "com/fr/bi/web/js/modules/selectdata/treeitem/item.level.js",
                "com/fr/bi/web/js/modules/selectdata/treeitem/node.level0.excel.js",

                "com/fr/bi/web/js/modules/selectdata/treeitem4reusedimension/calctarget.item.level0.js",
                "com/fr/bi/web/js/modules/selectdata/treeitem4reusedimension/calctarget.button.level0.js",

                "com/fr/bi/web/js/modules/selectdata/treeitem4reusedimension/item.level0.js",
                "com/fr/bi/web/js/modules/selectdata/widget.selectdatapane.js",
                "com/fr/bi/web/js/modules/selectdata/widget.selectdimensionpane.js",
                "com/fr/bi/web/js/modules/selectdata/tab.selectdata.js",

                //明细表选字段
                "com/fr/bi/web/js/modules/selectdata4detail/treenode/abstract.node.level.js",
                "com/fr/bi/web/js/modules/selectdata4detail/treenode/node.level0.excel.js",
                "com/fr/bi/web/js/modules/selectdata4detail/treenode/node.level0.js",
                "com/fr/bi/web/js/modules/selectdata4detail/treenode/node.level1.js",
                "com/fr/bi/web/js/modules/selectdata4detail/widget.selectdatapane.detail.js",
                "com/fr/bi/web/js/modules/selectdata4detail/treeitem/item.match.search.js",

                //树控件选字段
                "com/fr/bi/web/js/modules/selectdata4tree/treenode/abstract.node.level.js",
                "com/fr/bi/web/js/modules/selectdata4tree/treenode/node.level0.excel.js",
                "com/fr/bi/web/js/modules/selectdata4tree/treenode/node.level0.js",
                "com/fr/bi/web/js/modules/selectdata4tree/treenode/node.level1.js",
                "com/fr/bi/web/js/modules/selectdata4tree/widget.selectdatapane.tree.js",


                //过滤界面的选择字段
                "com/fr/bi/web/js/modules/selectdata4filter/widget.dimensionselectfield.js",
                "com/fr/bi/web/js/modules/selectdata4filter/widget.targetselectfield.js",
                "com/fr/bi/web/js/modules/selectdata4filter/node/node.level0.dimension.js",

                //通用查询选字段
                "com/fr/bi/web/js/modules/selectdata4generalquery/widget.generalquery.selectdata.tab.js",
                "com/fr/bi/web/js/modules/selectdata4generalquery/widget.generalquery.selectdata.pane.js",
                "com/fr/bi/web/js/modules/selectdata4generalquery/widget.generalquery.usedfields.pane.js",
                "com/fr/bi/web/js/modules/selectdata4generalquery/item/item.generalquery.selectdata.js",

                //文本控件选字段
                "com/fr/bi/web/js/modules/selectstring/treeitem/item.level0.js",
                "com/fr/bi/web/js/modules/selectstring/pane.selectstring.js",

                //数值控件选字段
                "com/fr/bi/web/js/modules/selectnumber/treeitem/item.level0.js",
                "com/fr/bi/web/js/modules/selectnumber/pane.selectnumber.js",

                //日期控件选字段
                "com/fr/bi/web/js/modules/selectdate/treeitem/item.level0.js",
                "com/fr/bi/web/js/modules/selectdate/pane.selectdate.js",


                //文本控件
                "com/fr/bi/web/js/modules/selectdatacombo/widget.selectdatacombo.js",

                //文本列表
                "com/fr/bi/web/js/modules/selectdatastringlist/widget.selectdatastringlist.js",

                //树控件
                "com/fr/bi/web/js/modules/selecttreedatacombo/selecttreedatacombo.js",

                //树列表
                "com/fr/bi/web/js/modules/selecttreedatalist/selecttreedatalist.js",

                //单值滑块
                "com/fr/bi/web/js/modules/selectdataslider/selectdatasingleslider.js",

                //区间滑块
                "com/fr/bi/web/js/modules/selectdataslider/selectdataintervalslider.js",

                //文本标签控件
                "com/fr/bi/web/js/modules/selectlistlabel/widget.selectlistlabel.js",

                //树标签控件
                "com/fr/bi/web/js/modules/selecttreelabel/widget.selecttreelabel.js",

                //系列堆积设置
                "com/fr/bi/web/js/modules/seriesaccumulation/series.accumulation.js",
                "com/fr/bi/web/js/modules/seriesaccumulation/series.accumulation.popup.js",
                "com/fr/bi/web/js/modules/seriesaccumulation/group/group.accumulation.js",
                "com/fr/bi/web/js/modules/seriesaccumulation/group/container.accumulation.js",
                "com/fr/bi/web/js/modules/seriesaccumulation/group/type.select.button.js",

                "com/fr/bi/web/js/modules/date/interval.date.param.js",
                "com/fr/bi/web/js/modules/datepane/datepane.js",

                //下拉树控件字段关联设置
                "com/fr/bi/web/js/modules/fieldrelationsettingwithpreview/fieldrelationsettingwithpreviewpopup.js",
                "com/fr/bi/web/js/modules/fieldrelationsettingwithpreview/model.fieldrelationsettingwithpreviewpopup.js",

                //明细表字段关联设置
                "com/fr/bi/web/js/modules/fieldrelationsetting/item.path.detailtable.js",
                "com/fr/bi/web/js/modules/fieldrelationsetting/item.commontable.set.detailtable.js",
                "com/fr/bi/web/js/modules/fieldrelationsetting/switch.path.setting.detailtable.js",
                "com/fr/bi/web/js/modules/fieldrelationsetting/tab.path.setting.detailtable.js",
                "com/fr/bi/web/js/modules/fieldrelationsetting/popup.path.setting.detailtable.js",
                "com/fr/bi/web/js/modules/fieldrelationsetting/combo.path.setting.detailtable.js",
                "com/fr/bi/web/js/modules/fieldrelationsetting/fieldrelationsettingpopup.js",
                "com/fr/bi/web/js/modules/fieldrelationsetting/model.fieldrelationsettingpopup.js",

                //数据标签
                "com/fr/bi/web/js/modules/datalabel/datalabel.js",
                "com/fr/bi/web/js/modules/datalabel/tab/tab.datalabel.js",
                "com/fr/bi/web/js/modules/datalabel/tab/texttoolbar.datalabel.js",
                "com/fr/bi/web/js/modules/datalabel/tab/font.chooser.texttoolbar.js",
                "com/fr/bi/web/js/modules/datalabel/tab/select.content.texttoolbar.js",
                "com/fr/bi/web/js/modules/datalabel/tab/imageset.datalabel.js",
                "com/fr/bi/web/js/modules/datalabel/tab/styleset.datalabel.js",
                "com/fr/bi/web/js/modules/datalabel/tab/chart.datalabel.js",
                "com/fr/bi/web/js/modules/datalabel/tab/button.image.datalabel.js",
                "com/fr/bi/web/js/modules/datalabel/condition/item.filter.datalabel.abstract.js",
                "com/fr/bi/web/js/modules/datalabel/condition/group.condition.datalabel.js",
                "com/fr/bi/web/js/modules/datalabel/condition/selectdata4datalabel.js",
                "com/fr/bi/web/js/modules/datalabel/condition/common/item.numberfield.js",
                "com/fr/bi/web/js/modules/datalabel/condition/common/item.stringfield.js",
                "com/fr/bi/web/js/modules/datalabel/condition/common/factory.filteritem.datalabel.js",
                "com/fr/bi/web/js/modules/datalabel/condition/common/item.notypefieldl.js",
                "com/fr/bi/web/js/modules/datalabel/condition/common/widget.datalabelselectfield.js",
                "com/fr/bi/web/js/modules/datalabel/condition/scatter/item.numberfield.js",
                "com/fr/bi/web/js/modules/datalabel/condition/scatter/item.multifield.js",
                "com/fr/bi/web/js/modules/datalabel/condition/scatter/item.stringfield.js",
                "com/fr/bi/web/js/modules/datalabel/condition/scatter/factory.filteritem.datalabel.js",
                "com/fr/bi/web/js/modules/datalabel/condition/scatter/item.notypefieldl.js",
                "com/fr/bi/web/js/modules/datalabel/condition/scatter/widget.datalabelselectfield.js",
                "com/fr/bi/web/js/modules/datalabel/condition/bubble/item.numberfield.js",
                "com/fr/bi/web/js/modules/datalabel/condition/bubble/item.multifield.js",
                "com/fr/bi/web/js/modules/datalabel/condition/bubble/item.stringfield.js",
                "com/fr/bi/web/js/modules/datalabel/condition/bubble/factory.filteritem.datalabel.js",
                "com/fr/bi/web/js/modules/datalabel/condition/bubble/item.notypefieldl.js",
                "com/fr/bi/web/js/modules/datalabel/condition/bubble/widget.datalabelselectfield.js",

                //数据图片
                "com/fr/bi/web/js/modules/dataimage/dataimage.js",
                "com/fr/bi/web/js/modules/dataimage/dataimage.popup.js",
                "com/fr/bi/web/js/modules/dataimage/condition/group.condition.dataimage.js",
                "com/fr/bi/web/js/modules/dataimage/image/styleset.dataimage.js",
                "com/fr/bi/web/js/modules/dataimage/image/image.combo.dataimage.js",
                "com/fr/bi/web/js/modules/dataimage/image/imageset.dataimage.js",
                "com/fr/bi/web/js/modules/dataimage/condition/common/item.notypefield.js",
                "com/fr/bi/web/js/modules/dataimage/condition/common/item.numberfield.js",
                "com/fr/bi/web/js/modules/dataimage/condition/common/item.stringfield.js",
                "com/fr/bi/web/js/modules/dataimage/condition/common/factory.filteritem.dataimage.js",
                "com/fr/bi/web/js/modules/dataimage/condition/common/widget.dataimageselectfield.js",

                //计算指标
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4summary/calculatetargetpopup.summary.js",
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4summary/model.calculatetargetpopup.summary.js",
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4detail/calculatetargetpopup.detail.js",
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4detail/model.calculatetargetpopup.detail.js",
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4summary/pane/pane.calculate.target.abstract.js",
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4summary/pane/pane.calculate.target.group.abstract.js",
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4summary/pane/pane.calculate.target.period.rate.abstract.js",
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4summary/pane/pane.calculate.target.period.value.abstract.js",
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4summary/pane/pane.month.on.month.value.js",
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4summary/pane/pane.month.on.month.rate.js",
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4summary/pane/pane.year.on.year.rate.js",
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4summary/pane/pane.year.on.year.value.js",
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4summary/pane/pane.calculate.target.formula.js",
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4summary/pane/pane.calculate.target.rank.js",
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4summary/pane/pane.calculate.target.rank.group.js",
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4summary/pane/pane.calculate.target.sum.js",
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4summary/pane/pane.calculate.target.sum.group.js",
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4summary/pane/pane.calculate.target.sum.above.group.js",
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4summary/pane/pane.calculate.target.sum.above.js",


                //带参数的复杂日期模块
                "com/fr/bi/web/js/modules/multidatecombowithparam/item/item.level0.js",
                "com/fr/bi/web/js/modules/multidatecombowithparam/item/item.level1.js",
                "com/fr/bi/web/js/modules/multidatecombowithparam/item/item.param.js",
                "com/fr/bi/web/js/modules/multidatecombowithparam/multidate.parampane.js",
                "com/fr/bi/web/js/modules/multidatecombowithparam/multidate.parampopup.js",
                "com/fr/bi/web/js/modules/multidatecombowithparam/multidate.paramtrigger.js",
                "com/fr/bi/web/js/modules/multidatecombowithparam/multidate.paramcombo.js",
                "com/fr/bi/web/js/modules/paramtimeinterval/timeinterval.param.js",

                //表格的单元格
                "com/fr/bi/web/js/modules/tablechartmanager/datatable/tablecell/normal/headercell.normal.js",
                "com/fr/bi/web/js/modules/tablechartmanager/datatable/tablecell/normal/cell.tarbody.normal.js",
                "com/fr/bi/web/js/modules/tablechartmanager/datatable/tablecell/normal/expandercell.normal.js",
                "com/fr/bi/web/js/modules/tablechartmanager/datatable/combo/sortfilter.dimension.combo.js",
                "com/fr/bi/web/js/modules/tablechartmanager/datatable/combo/sortfilter.target.combo.js",
                "com/fr/bi/web/js/modules/tablechartmanager/datatable/combo/sortfilter.detail.combo.js",

                "com/fr/bi/web/js/modules/tablechartmanager/datatable/helper/summarytable.helper.js",
                "com/fr/bi/web/js/modules/tablechartmanager/datatable/tableview/widget.grouptable.js",
                "com/fr/bi/web/js/modules/tablechartmanager/datatable/tableview/widget.crosstable.js",
                "com/fr/bi/web/js/modules/tablechartmanager/datatable/tableview/widget.complextable.js",
                "com/fr/bi/web/js/modules/tablechartmanager/datatable/tablemodel/grouptable.model.js",
                "com/fr/bi/web/js/modules/tablechartmanager/datatable/tablemodel/crosstable.model.js",
                "com/fr/bi/web/js/modules/tablechartmanager/datatable/tablemodel/complextable.model.js",

                "com/fr/bi/web/js/modules/tablechartmanager/chartdisplay/chartdisplay.js",

                "com/fr/bi/web/js/modules/tablechartmanager/tablechartmanager.js",
                "com/fr/bi/web/js/modules/tablechartmanager/errorpane/tablechart.errorpane.js",

                //明细表
                "com/fr/bi/web/js/modules/detailtable/widget.detailtable.js",
                "com/fr/bi/web/js/modules/detailtable/cell/header.detailtable.js",
                "com/fr/bi/web/js/modules/detailtable/cell/cell.detailtable.js",

                //指标和维度的匹配关系
                "com/fr/bi/web/js/modules/matchingrelationship/popup.matchingrelationship.js",
                "com/fr/bi/web/js/modules/matchingrelationship/tab.matchingrelationship.js",
                "com/fr/bi/web/js/modules/matchingrelationship/infopane/unsettingtargetregion.infopane.js",
                "com/fr/bi/web/js/modules/matchingrelationship/infopane/settargetregion.infopane.js",
                "com/fr/bi/web/js/modules/matchingrelationship/infopane/infopane.js",
                "com/fr/bi/web/js/modules/matchingrelationship/settingpane/settingpane.js",
                "com/fr/bi/web/js/modules/matchingrelationship/settingpane/multipathchooser.settingpane.js",
                "com/fr/bi/web/js/modules/matchingrelationship/settingpane/multimatchmulti.settingpane.js",
                "com/fr/bi/web/js/modules/matchingrelationship/settingpane/targetlabelcontrol.settingpane.js",
                "com/fr/bi/web/js/modules/matchingrelationship/settingpane/dimensiontreecombo/dimensiontree.combo.js",
                "com/fr/bi/web/js/modules/matchingrelationship/settingpane/dimensiontreecombo/dimensiontree.popup.js",
                "com/fr/bi/web/js/modules/matchingrelationship/settingpane/dimensiontreecombo/dimensiontree.trigger.js",

                //表格属性设置
                "com/fr/bi/web/js/modules/chartsetting/charts/addcondition/gradientcolor/chart.gradientcolor.combo.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/addcondition/chart.addcondition.item.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/addcondition/chart.addcondition.group.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/addcondition/chart.addgradientcondition.item.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/addcondition/chart.addgradientcondition.group.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/customscale/formula/customscale.formula.field.tree.midleaf.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/customscale/formula/customscale.formula.field.tree.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/customscale/formula/customscale.formula.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/customscale/formula/customscale.formula.pane.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/customscale/combo.customscale.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/customscale/customscale.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/customscale/trigger.customscale.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/selectcolorcombo/combo.selectcolor.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/selectcolorcombo/wrap.item.selectcolor.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/selectcolorcombo/item.selectcolor.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/selectcolorcombo/popup.selectcolor.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/selectcolorcombo/trigger.selectcolor.js",
                "com/fr/bi/web/js/modules/chartsetting/widget.chartsetting.js",
                "com/fr/bi/web/js/modules/chartsetting/grouptable/widget.grouptable.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/block/widget.block.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/abstractchart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/axischart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/bubblechart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/multiaxischart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/percentchart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/lineareachart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/barchart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/scatterchart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/comparecolumnchart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/compareareachart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/dashboardchart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/donutchart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/fallaxischart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/piechartsetting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/multipiechart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/recttreechart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/funnelchart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/heatmapchart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/paretochart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/radarchart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/accumulateradarchart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/rangeareachart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/forcebubble.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/mapchart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/gismapchart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/crosstable/widget.crosstable.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/complextable/widget.complextable.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/detailtable/widget.detailtable.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/detailedsetting/detailed.setting.trigger.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/detailedsetting/angle/angle.detailed.setting.combo.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/detailedsetting/chartlabel/chartlabel.detailed.setting.combo.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/detailedsetting/chartlabel/chartlabel.detailed.setting.popup.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/detailedsetting/datalabel/datalabel.detailed.setting.combo.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/detailedsetting/datalabel/popup/area.datalabel.detailed.setting.popup.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/detailedsetting/datalabel/popup/axis.datalabel.detailed.setting.popup.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/detailedsetting/datalabel/popup/bubble.detailed.setting.popup.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/detailedsetting/datalabel/popup/dashboard.datalabel.detailed.setting.popup.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/detailedsetting/datalabel/popup/map.datalabel.detailed.setting.popup.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/detailedsetting/datalabel/popup/pie.datalabel.detailed.setting.popup.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/detailedsetting/datalabel/popup/scatter.datalabel.detailed.setting.popup.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/detailedsetting/legend/legend.detailed.setting.combo.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/detailedsetting/legend/legend.detailed.setting.popup.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/detailedsetting/showtitle/showtitle.detailed.setting.combo.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/detailedsetting/showtitle/showtitle.detailed.setting.popup.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/detailedsetting/table/table.detailed.setting.combo.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/detailedsetting/table/table.detailed.setting.popup.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/detailedsetting/table/table.detailed.setting.texttoolbar.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/detailedsetting/tooltip/tooltip.detailed.setting.combo.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/detailedsetting/tooltip/tooltip.detailed.setting.popup.js",

                //警戒线
                "com/fr/bi/web/js/modules/cordon/pane/item.cordon.js",
                "com/fr/bi/web/js/modules/cordon/pane/pane.cordon.js",
                "com/fr/bi/web/js/modules/cordon/popup.cordon.js",

                //标签样式面板
                "com/fr/bi/web/js/modules/datalabel/datalabel.popup.js",

                //自适应布局
                //选组件
                "com/fr/bi/web/js/modules/fit/widgetchooser/reuse/pane.reuse.js",
                "com/fr/bi/web/js/modules/fit/widgetchooser/dragiconbutton.js",
                "com/fr/bi/web/js/modules/fit/widgetchooser/dragiconcombo.js",
                "com/fr/bi/web/js/modules/fit/widgetchooser/reuse/dragwidgetitem.js",
                "com/fr/bi/web/js/modules/fit/widgetchooser/widget.dragicongroup.js",
                "com/fr/bi/web/js/modules/fit/fit.widget.js",
                "com/fr/bi/web/js/modules/fit/fit.js",

                //联动
                "com/fr/bi/web/js/modules/linkage/model.linkage.js",
                "com/fr/bi/web/js/modules/linkage/linkage.target.js",
                "com/fr/bi/web/js/modules/linkage/linkage.targets.js",
                "com/fr/bi/web/js/modules/linkage/linkage.js",

                //指标样式设置
                "com/fr/bi/web/js/modules/targetstyle/widget.targetstylesetting.js",
                "com/fr/bi/web/js/modules/targetstyle/widget.targetstylesetting.map.js",
                "com/fr/bi/web/js/modules/targetstyle/widget.targetcondition.stylesetting.js",
                "com/fr/bi/web/js/modules/targetstyle/widget.stylesetting.iconmark.js",
                "com/fr/bi/web/js/modules/targetstyle/combo/combo.iconmark.js",
                "com/fr/bi/web/js/modules/targetstyle/combo/popup.iconmark.js",
                "com/fr/bi/web/js/modules/targetstyle/combo/trigger.iconmark.js",
                "com/fr/bi/web/js/modules/targetstyle/combo/item.iconmark.js",
                "com/fr/bi/web/js/modules/targetstyle/conditionitem/item.targetstylecondition.js",

                //组件上的过滤
                "com/fr/bi/web/js/modules/widgetfilter/widget.widgetfilter.js",
                "com/fr/bi/web/js/modules/widgetfilter/widget.widgetfilter.model.js",
                "com/fr/bi/web/js/modules/widgetfilter/item.linkagefilter.js",
                "com/fr/bi/web/js/modules/widgetfilter/item.targetfilter.js",
                "com/fr/bi/web/js/modules/widgetfilter/item.dimensionfilter.js",
                "com/fr/bi/web/js/modules/widgetfilter/item.controlfilter.js",
                "com/fr/bi/web/js/modules/widgetfilter/item.drillfilter.js",

                //明细表超级链接
                "com/fr/bi/web/js/modules/hyperlink/hyperlink.insert.js",
                "com/fr/bi/web/js/modules/hyperlink/hyperlink.popup.js",

                //另存为
                "com/fr/bi/web/js/modules/saveas/popup.saveas.js",

                //图表钻取
                "com/fr/bi/web/js/modules/chartdrill/button.pushdrill.js",
                "com/fr/bi/web/js/modules/chartdrill/cell.chartdrill.js",
                "com/fr/bi/web/js/modules/chartdrill/widget.chartdrill.js",

                //excelview
                "com/fr/bi/web/js/extend/excelview/excelview.cell.js",
                "com/fr/bi/web/js/extend/excelview/excelview.js",

                //最大化
                "com/fr/bi/web/js/modules/maximization/widget.maximization.chartpane.js",
                "com/fr/bi/web/js/modules/maximization/widget.maximization.js",

                /**
                 * 实时报表
                */

                //实时报表选择字段
                "com/fr/bi/web/js/modules4realtime/selectdata/treenode/abstract.node.level.js",
                "com/fr/bi/web/js/modules4realtime/selectdata/treenode/node.level0.js",
                "com/fr/bi/web/js/modules4realtime/selectdata/treenode/node.level1.js",
                "com/fr/bi/web/js/modules4realtime/selectdata/widget.selectdata.js",
                //实时报表拖拽
                "com/fr/bi/web/js/modules4realtime/drag/widget.dragicongroup.js",
                //实时报表文本选择字段
                "com/fr/bi/web/js/modules4realtime/selectstring/widget.selectstring.js",
                //实时报表数值选择字段
                "com/fr/bi/web/js/modules4realtime/selectnumber/widget.selectnumber.js",
                //实时报表日期选择字段
                "com/fr/bi/web/js/modules4realtime/selectdate/widget.selectdate.js",
                //实时报表日期选择明细表
                "com/fr/bi/web/js/modules4realtime/selectdata4detail/widget.selectdata.js",

                "com/fr/bi/web/js/modules4realtime/constant.js",
                "com/fr/bi/web/js/modules4realtime/config.js",
                "com/fr/bi/web/js/modules4realtime/broadcast.js",
                "com/fr/bi/web/js/modules4realtime/cache.js",
                "com/fr/bi/web/js/modules4realtime/utils.js",
        };
    }

    public static String[] getDeziJs() {
        String[] dezi = getDeziJsModule();
        return (String[]) ArrayUtils.addAll(dezi, new String[]{

                "com/fr/bi/web/js/dezi/dezi.start.js",
                "com/fr/bi/web/js/dezi/model.js",
                "com/fr/bi/web/js/dezi/view.js",
                "com/fr/bi/web/js/dezi/modules/dezi.floatbox.manage.js",
                "com/fr/bi/web/js/dezi/modules/dezi.model.manage.js",
                "com/fr/bi/web/js/dezi/modules/dezi.view.manage.js",
                "com/fr/bi/web/js/dezi/modules/model/dezi.model.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/model.pane.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.widget.js",

                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.detailtable.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.string.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.stringlist.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.query.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.reset.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.date.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.datepane.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.daterange.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.number.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.singleslider.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.intervalslider.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.tree.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.treelist.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.listlabel.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.treelabel.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.year.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.yearmonth.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.yearquarter.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.generalquery.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/model.string.detail.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/model.stringlist.detail.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/model.tree.detail.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/model.treelist.detail.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/model.listlabel.detail.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/model.treelabel.detail.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/model.year.detail.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/model.yearmonth.detail.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/model.yearquarter.detail.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/model.number.detail.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/model.singleslider.detail.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/model.intervalslider.detail.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/model.date.detail.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/model.datepane.detail.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/model.detail.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/model.detailtable.detail.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/model.daterange.detail.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.content.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.image.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.web.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/field/model.dimension.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/field/model.detail.dimension.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/field/model.string.dimension.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/field/model.tree.dimension.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/field/model.listlabel.dimension.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/field/model.treelabel.dimension.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/field/model.number.dimension.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/field/model.date.dimension.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/field/model.tree.dimension.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/field/model.target.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/field/filter/model.dimensionfilter.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/field/filter/model.targetfilter.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/field/customgroup/model.customgroup.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/field/customsort/model.customsort.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/field/numbercustomgroup/model.number.custom.group.js",
                "com/fr/bi/web/js/dezi/modules/view/dezi.view.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/dezi.pane.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.widget.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.detailtable.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.string.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.stringlist.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.query.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.reset.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.date.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.datepane.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.year.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.yearmonth.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.yearquarter.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.daterange.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.number.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.singleslider.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.intervalslider.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.tree.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.treelist.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.listlabel.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.treelabel.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.generalquery.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.widgets.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/dezi.string.detail.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/dezi.stringlist.detail.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/dezi.date.detail.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/dezi.datepane.detail.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/dezi.number.detail.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/dezi.singleslider.detail.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/dezi.intervalslider.detail.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/dezi.tree.detail.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/dezi.treelist.detail.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/dezi.listlabel.detail.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/dezi.treelabel.detail.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/dezi.detail.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/dezi.year.detail.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/dezi.yearmonth.detail.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/dezi.yearquarter.detail.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/dezi.detailtable.detail.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/dezi.daterange.detail.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.content.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.image.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.web.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/field/dezi.dimension.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/field/dezi.detail.dimension.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/field/dezi.string.dimension.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/field/dezi.tree.dimension.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/field/dezi.listlabel.dimension.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/field/dezi.treelabel.dimension.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/field/dezi.number.dimension.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/field/dezi.date.dimension.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/field/dezi.tree.dimension.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/field/dezi.target.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/field/customgroup/dezi.customgroup.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/field/customsort/dezi.customsort.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/field/numbercustomgroup/dezi.number.custom.group.js"
        });
    }
}
