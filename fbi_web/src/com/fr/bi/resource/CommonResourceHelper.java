package com.fr.bi.resource;


/**
 * 读取各种资源的帮助类
 */
public class CommonResourceHelper {
    public static String[] getCommonCss() {
        return new String[]{
                //过滤
                "com/fr/bi/web/css/modules/filter/filteritems/dimension/item.notypefield.css",
                "com/fr/bi/web/css/modules/filter/filteritems/dimension/item.stringfield.css",
                "com/fr/bi/web/css/modules/filter/filteritems/dimension/item.numberfield.css",
                "com/fr/bi/web/css/modules/filter/filteritems/dimension/item.datefield.css",
                "com/fr/bi/web/css/modules/filter/filteritems/target/item.notypefield.css",
                "com/fr/bi/web/css/modules/filter/filteritems/target/item.stringfield.css",
                "com/fr/bi/web/css/modules/filter/filteritems/target/item.numberfield.css",
                "com/fr/bi/web/css/modules/filter/filteritems/target/item.datefield.css",
                "com/fr/bi/web/css/modules/filter/filteritems/formula/item.emptyformula.css",
                "com/fr/bi/web/css/modules/filter/filteritems/formula/item.formula.css",


                //自定义分组
                "com/fr/bi/web/css/modules/customgroup/widget.ungroupedpane.css",
                "com/fr/bi/web/css/modules/customgroup/widget.customgroup.css",
                "com/fr/bi/web/css/modules/customgroup/widget.fieldPane.customgroup.css",
                "com/fr/bi/web/css/modules/customgroup/widget.expander.customgroup.css",
                "com/fr/bi/web/css/modules/customgroup/widget.bottom.customgroup.css",
                "com/fr/bi/web/css/modules/customgroup/widget.view.searcher.customgroup.css",
                "com/fr/bi/web/css/modules/customgroup/widget.pane.searcher.customgroup.css",
                "com/fr/bi/web/css/modules/customgroup/widget.button.field.customgroup.css",

                //全局样式
                "com/fr/bi/web/css/modules/globalstyle/widget.globalstyle.css",
                "com/fr/bi/web/css/modules/globalstyle/widget.globalstyle.setting.css",
                "com/fr/bi/web/css/modules/globalstyle/indexitem/widget.globalstyle.indexcombo.css",
                "com/fr/bi/web/css/modules/globalstyle/indexitem/widget.globalstyle.indextitletoolbar.css",
                "com/fr/bi/web/css/modules/globalstyle/indexitem/widget.globalstyle.indexalignchooser.css",
                "com/fr/bi/web/css/modules/globalstyle/indexitem/predictionstyle/widget.globalstyle.indexpredictionstyle.css",
                "com/fr/bi/web/css/modules/globalstyle/indexitem/predictionstyle/widget.globalstyle.usercustombutton.css",
                "com/fr/bi/web/css/modules/globalstyle/indexitem/predictionstyle/pagination/widget.globalstyle.pagination.icon.css",
                "com/fr/bi/web/css/modules/globalstyle/indexitem/uploadimage/widget.uploadimage.preview.css",

                //选择字段服务
                "com/fr/bi/web/css/services/packageselectdataservice/relationtable/node.relationtables.css",

                //组件加载超时
                "com/fr/bi/web/css/modules/timeouttoast/widget.timeouttoast.css"
        };
    }

    public static String[] getCommonJs() {
        return new String[]{
                "com/fr/bi/web/js/modules/constant.js",
                "com/fr/bi/web/js/modules/cache.js",
                "com/fr/bi/web/js/modules/broadcast.js",
                "com/fr/bi/web/js/modules/config.js",
                "com/fr/bi/web/js/modules/utils.js",


                //自定义分组
                "com/fr/bi/web/js/modules/customgroup/widget.group2other.customgroup.js",
                "com/fr/bi/web/js/modules/customgroup/widget.customgroup.js",
                "com/fr/bi/web/js/modules/customgroup/widget.combo.customgroup.js",
                "com/fr/bi/web/js/modules/customgroup/widget.search.customgroup.js",
                "com/fr/bi/web/js/modules/customgroup/widget.node.arrow.delete.customgroup.js",
                "com/fr/bi/web/js/modules/customgroup/view.customgroup.js",
                "com/fr/bi/web/js/modules/customgroup/widget.expander.group.customgroup.js",
                "com/fr/bi/web/js/modules/customgroup/widget.button.field.customgroup.js",
                "com/fr/bi/web/js/modules/customgroup/widget.pane.group.customgroup.js",
                "com/fr/bi/web/js/modules/customgroup/widget.pane.allfileds.customgroup.js",
                "com/fr/bi/web/js/modules/customgroup/widget.pane.searcher.customgroup.js",
                "com/fr/bi/web/js/modules/customgroup/widget.view.searcher.customgroup.js",

                //全局样式
                "com/fr/bi/web/js/modules/globalstyle/widget.globalstyle.js",
                "com/fr/bi/web/js/modules/globalstyle/widget.globalstyle.setting.js",
                "com/fr/bi/web/js/modules/globalstyle/indexitem/widget.globalstyle.indexcombo.js",
                "com/fr/bi/web/js/modules/globalstyle/indexitem/widget.globalstyle.indextitletoolbar.js",
                "com/fr/bi/web/js/modules/globalstyle/indexitem/widget.globalstyle.indexcharttoolbar.js",
                "com/fr/bi/web/js/modules/globalstyle/indexitem/widget.globalstyle.indexalignchooser.js",
                "com/fr/bi/web/js/modules/globalstyle/indexitem/predictionstyle/widget.globalstyle.indexpredictionstyle.js",
                "com/fr/bi/web/js/modules/globalstyle/indexitem/predictionstyle/widget.globalstyle.usercustombutton.js",
                "com/fr/bi/web/js/modules/globalstyle/indexitem/predictionstyle/widget.globalstyle.stylebutton.js",
                "com/fr/bi/web/js/modules/globalstyle/indexitem/predictionstyle/pagination/widget.globalstyle.pagination.icon.js",
                "com/fr/bi/web/js/modules/globalstyle/indexitem/predictionstyle/pagination/widget.globalstyle.pagination.js",
                "com/fr/bi/web/js/modules/globalstyle/indexitem/uploadimage/widget.uploadimage.preview.js",
                "com/fr/bi/web/js/modules/globalstyle/stylemanager/stylesetmanager.js",

                //过滤条件
                "com/fr/bi/web/js/modules/filter/filterpopup/targetfilterpopup.js",
                "com/fr/bi/web/js/modules/filter/filterpopup/dimensionfilterpopup.js",
                "com/fr/bi/web/js/modules/filter/filterpopup/targetsummaryfilter.popup.js",
                "com/fr/bi/web/js/modules/filter/filterpopup/detailtablefilter.popup.js",
                "com/fr/bi/web/js/modules/filter/filterpopup/authorityfilter.popup.js",
                "com/fr/bi/web/js/modules/filter/filteritems/target/item.stringfield.js",
                "com/fr/bi/web/js/modules/filter/filteritems/target/item.numberfield.js",
                "com/fr/bi/web/js/modules/filter/filteritems/target/item.datefield.js",
                "com/fr/bi/web/js/modules/filter/filteritems/target/target.date.tab.js",
                "com/fr/bi/web/js/modules/filter/filteritems/target/item.notypefield.js",
                "com/fr/bi/web/js/modules/filter/filteritems/target/factory.filteritem.target.js",
                "com/fr/bi/web/js/modules/filter/filteritems/dimension/factory.filteritem.dimension.js",
                "com/fr/bi/web/js/modules/filter/filteritems/dimension/item.stringfield.js",
                "com/fr/bi/web/js/modules/filter/filteritems/dimension/item.datefield.js",
                "com/fr/bi/web/js/modules/filter/filteritems/dimension/widget.selectdata4dimensioncombo.js",
                "com/fr/bi/web/js/modules/filter/filteritems/target/widget.selectdata4targetcombo.js",
                "com/fr/bi/web/js/modules/filter/filteritems/dimension/item.numberfield.js",
                "com/fr/bi/web/js/modules/filter/filteritems/dimension/item.notypefield.js",
                "com/fr/bi/web/js/modules/filter/filteritems/dimension/item.formula.js",
                "com/fr/bi/web/js/modules/filter/filteritems/target/item.formula.js",
                "com/fr/bi/web/js/modules/filter/filteritems/dimension/item.emptyformula.js",
                "com/fr/bi/web/js/modules/filter/filteritems/target/item.emptyformula.js",

                "com/fr/bi/web/js/modules/filter/filteritems/auth/item.notypefield.js",
                "com/fr/bi/web/js/modules/filter/filteritems/auth/item.stringfield.js",
                "com/fr/bi/web/js/modules/filter/filteritems/auth/item.numberfield.js",
                "com/fr/bi/web/js/modules/filter/filteritems/auth/item.datefield.js",
                "com/fr/bi/web/js/modules/filter/filteritems/auth/widget.selectdata4authority.js",
                "com/fr/bi/web/js/modules/filter/filteritems/auth/factory.filteritem.auth.js",
                "com/fr/bi/web/js/modules/filter/filteritems/auth/widget.authority.stringcombo.js",
                "com/fr/bi/web/js/modules/filter/filteritems/auth/widget.logininfo.combo.js",

                "com/fr/bi/web/js/modules/filter/filteritems/generalquery/item.generalquery.notypefilter.js",

                //过滤
                "com/fr/bi/web/js/modules/filter/expander/filter.expander.js",
                "com/fr/bi/web/js/modules/filter/filter.target.js",
                "com/fr/bi/web/js/modules/filter/filter.dimension.js",
                "com/fr/bi/web/js/modules/filter/targetsummary/filter.target.summary.js",
                "com/fr/bi/web/js/modules/filter/targetsummary/item/item.target.js",
                "com/fr/bi/web/js/modules/filter/generalquery/filter.generalquery.js",
                "com/fr/bi/web/js/modules/filter/detailtable/filter.detailtable.js",
                "com/fr/bi/web/js/modules/filter/auth/filter.authority.js",


                //业务包选择字段服务
                "com/fr/bi/web/js/services/packageselectdataservice/treenode/node.level0.js",
                "com/fr/bi/web/js/services/packageselectdataservice/treenode/node.level1.js",
                "com/fr/bi/web/js/services/packageselectdataservice/treenode/node.level2.js",
                "com/fr/bi/web/js/services/packageselectdataservice/treenode/node.level1.date.js",
                "com/fr/bi/web/js/services/packageselectdataservice/relationtable/node.relationtables.js",
                "com/fr/bi/web/js/services/packageselectdataservice/relationtable/node.level2.date.js",
                "com/fr/bi/web/js/services/packageselectdataservice/relationtable/expander.relationtable.js",
                "com/fr/bi/web/js/services/packageselectdataservice/relationtable/expander.relationtables.js",
                "com/fr/bi/web/js/services/packageselectdataservice/packageselectdataservice.js",
                //简单字段选择服务
                "com/fr/bi/web/js/services/simpleselectdataservice/simpleselectdataservice.js",

                //复制链接
                "com/fr/bi/web/js/modules/copylinks/copylink.item.js",
                "com/fr/bi/web/js/modules/copylinks/copylink.iconbutton.js",

                /**
                 * 切片
                */

                //tablechartmanager
                "com/fr/bi/web/js/aspects/tablechartmanager/aspect.tablechartmanager.js",

                //detailtable
                "com/fr/bi/web/js/aspects/detailtable/aspect.detailtable.js",

                "com/fr/bi/web/js/aspects/config.js",


                //工程配置
                "com/fr/bi/web/js/config.js"
        };
    }
}
