package com.fr.bi.resource;

import com.fr.stable.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wang on 2017/1/18.
 */
public class ShowResourceHelper {
    private static String[] getShowCssModule() {
        return new String[]{
                //实时报表进度条
                "com/fr/bi/web/css/modules4show/dimensionsmanager4show/cubeprogressbar/cubeprogressbar.css",

                //详细设置相关模块(预览)
                "com/fr/bi/web/css/modules4show/dimensionsmanager4show/charttype/combo/combo.tabletype.css",
                "com/fr/bi/web/css/modules4show/dimensionsmanager4show/charttype/charttype.css",
                "com/fr/bi/web/css/modules4show/dimensionsmanager4show/regions/region.dimension.css",
                "com/fr/bi/web/css/modules4show/dimensionsmanager4show/regions/region.target.css",
                "com/fr/bi/web/css/modules4show/dimensionsmanager4show/regionsmanager.css",
                "com/fr/bi/web/css/modules4show/dimensionsmanager4show/dimensionsmanager.css",

                //show面板维度切换
                "com/fr/bi/web/css/modules4show/dimensionswitchshow/dimensionswitch.show.css",
                "com/fr/bi/web/css/modules4show/dimensionswitchshow/showdimensionsmanger/button/dimensiontagbutton.css",
                "com/fr/bi/web/css/modules4show/dimensionswitchshow/showdimensionsmanger/region/show.region.dimension.css",
                "com/fr/bi/web/css/modules4show/dimensionswitchshow/showdimensionsmanger/region/show.region.target.css",
                "com/fr/bi/web/css/modules4show/dimensionswitchshow/showdimensionsmanger/region/show.regionswrapper.dimension.css",
                "com/fr/bi/web/css/modules4show/dimensionswitchshow/showdimensionsmanger/region/show.regionswrapper.target.css",
                "com/fr/bi/web/css/modules4show/dimensionswitchshow/showdimensionsmanger/region/show.regionswrapper.target.setting.css",
                "com/fr/bi/web/css/modules4show/dimensionswitchshow/showdimensionsmanger/region/specialregion/show.region.target.setting.css",
                "com/fr/bi/web/css/modules4show/dimensionswitchshow/showdimensionsmanger/scope/scope.target.combine.chart.css",
                "com/fr/bi/web/css/modules4show/dimensionswitchshow/showdimensionsmanger/showregionmanager/showregionmanager.css",

                //最大化
                "com/fr/bi/web/css/modules4show/maximization/widget.maximization.chartpane.css",
                "com/fr/bi/web/css/modules4show/maximization/widget.maximization.css",
        };
    }

    public static String[] getShowCss() {
        String[] dezi = DeziResourceHelper.getDeziCssModule();
        String[] show = getShowCssModule();
        String[] module = ArrayUtils.addAll(dezi, show);
        return (String[]) ArrayUtils.addAll(module, new String[]{

                "com/fr/bi/web/css/show/show.view.css",
                "com/fr/bi/web/css/show/pane/show.pane.css",
                "com/fr/bi/web/css/show/pane/widgets/show.widgets.css",
                "com/fr/bi/web/css/show/pane/widgets/detail/show.detail.css",
                "com/fr/bi/web/css/show/pane/widgets/detail/region/show.region.css",
                "com/fr/bi/web/css/show/pane/widgets/detail/region/field/show.dimension.css",
                "com/fr/bi/web/css/show/pane/widgets/detail/region/field/show.target.css"

        });
    }

    private static String[] getShowJsModule() {
        return new String[]{
                //自适应布局
                "com/fr/bi/web/js/modules4show/fit/fit.js",

                //实时报表进度条
                "com/fr/bi/web/js/modules4show/cubeprogressbar/cubeprogressbar.js",
                //实时报表指示器
                "com/fr/bi/web/js/modules4show/cubeprogressindicator/cubeprogressindicator.js",

                //dimension show
                "com/fr/bi/web/js/modules4show/dimension4show/abstract.dimensiontarget.combo.show.js",
                "com/fr/bi/web/js/modules4show/dimension4show/dimension/abstract.dimension.combo.show.js",
                "com/fr/bi/web/js/modules4show/dimension4show/dimension/widget.numberdimension.combo.show.js",
                "com/fr/bi/web/js/modules4show/dimension4show/dimension/widget.datedimension.combo.show.js",
                "com/fr/bi/web/js/modules4show/dimension4show/dimension/widget.stringdimension.combo.show.js",
                "com/fr/bi/web/js/modules4show/dimension4show/target/widget.target.combo.show.js",
                "com/fr/bi/web/js/modules4show/dimension4show/target/widget.count.target.combo.show.js",
                "com/fr/bi/web/js/modules4show/dimension4show/target/widget.calculate.target.combo.show.js",
                "com/fr/bi/web/js/modules4show/dimension4show/detail/widget.detaildate.combo.show.js",
                "com/fr/bi/web/js/modules4show/dimension4show/detail/widget.detailformula.combo.show.js",
                "com/fr/bi/web/js/modules4show/dimension4show/detail/widget.detailnumber.combo.show.js",
                "com/fr/bi/web/js/modules4show/dimension4show/detail/widget.detailstring.combo.show.js",

                //show面板维度切换
                "com/fr/bi/web/js/modules4show/dimensionswitchshow/showdimensionsmanager/region/abstractregion/show.abstract.region.js",
                "com/fr/bi/web/js/modules4show/dimensionswitchshow/showdimensionsmanager/region/abstractregion/show.regionwrapper.js",
                "com/fr/bi/web/js/modules4show/dimensionswitchshow/showdimensionsmanager/scopes/scope.target.combine.chart.js",
                "com/fr/bi/web/js/modules4show/dimensionswitchshow/dimensionswitch.show.js",
                "com/fr/bi/web/js/modules4show/dimensionswitchshow/dimensionswitchpopup.show.js",
                "com/fr/bi/web/js/modules4show/dimensionswitchshow/showdimensionsmanager/card/dimensionregionmanager.show.js",
                "com/fr/bi/web/js/modules4show/dimensionswitchshow/showdimensionsmanager/button/dimensiontagbutton.js",
                "com/fr/bi/web/js/modules4show/dimensionswitchshow/showdimensionsmanager/showdimensionsmanger.js",
                "com/fr/bi/web/js/modules4show/dimensionswitchshow/showdimensionsmanager/showregionmanager/showregionmanager.js",
                "com/fr/bi/web/js/modules4show/dimensionswitchshow/showdimensionsmanager/showregionmanager/showregionmanagerforcombinechart.js",
                "com/fr/bi/web/js/modules4show/dimensionswitchshow/showdimensionsmanager/showregionmanagermodel/model.showregionmanager.js",
                "com/fr/bi/web/js/modules4show/dimensionswitchshow/showdimensionsmanager/region/show.region.dimension.js",
                "com/fr/bi/web/js/modules4show/dimensionswitchshow/showdimensionsmanager/region/show.region.target.js",
                "com/fr/bi/web/js/modules4show/dimensionswitchshow/showdimensionsmanager/region/show.regionswrapper.dimension.js",
                "com/fr/bi/web/js/modules4show/dimensionswitchshow/showdimensionsmanager/region/show.regionswrapper.target.js",
                "com/fr/bi/web/js/modules4show/dimensionswitchshow/showdimensionsmanager/region/show.regionswrapper.target.setting.js",
                "com/fr/bi/web/js/modules4show/dimensionswitchshow/showdimensionsmanager/region/emptyregion/show.region.empty.dimension.js",
                "com/fr/bi/web/js/modules4show/dimensionswitchshow/showdimensionsmanager/region/emptyregion/show.region.empty.target.js",
                "com/fr/bi/web/js/modules4show/dimensionswitchshow/showdimensionsmanager/region/specialregion/show.region.target.setting.js",

                //最大化
                "com/fr/bi/web/js/modules4show/maximization/widget.maximization.chartpane.js",
                "com/fr/bi/web/js/modules4show/maximization/widget.maximization.js",


                "com/fr/bi/web/js/modules4show/constant.js",
                "com/fr/bi/web/js/modules4show/cache.js",
                "com/fr/bi/web/js/modules4show/broadcast.js",
                "com/fr/bi/web/js/modules4show/config.js",
                "com/fr/bi/web/js/modules4show/utils.js",
        };
    }

    private static String[] getShowModelJs() {
        return new String[]{
                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.widget.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.detailtable.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.string.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.stringlist.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.query.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.reset.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.date.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.datepane.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.daterange.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.number.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.singleslider.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.intervalslider.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.tree.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.treelist.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.listlabel.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.treelabel.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.year.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.yearmonth.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.yearquarter.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.content.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.image.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.web.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/detail/model.detail.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/detail/model.detailtable.detail.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/detail/field/model.dimension.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/detail/field/model.detail.dimension.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/detail/field/model.target.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/detail/field/filter/model.dimensionfilter.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/detail/field/filter/model.targetfilter.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/detail/field/customgroup/model.customgroup.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/detail/field/customsort/model.customsort.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/detail/field/numbercustomgroup/model.number.custom.group.js",
        };
    }

    private static String[] getShowViewJs() {
        return new String[]{
                "com/fr/bi/web/js/show/modules/view/show.view.js",
                "com/fr/bi/web/js/show/modules/view/pane/show.pane.js",

                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.widget.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.detailtable.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.string.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.stringlist.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.tree.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.treelist.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.listlabel.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.treelabel.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.date.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.datepane.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.year.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.yearmonth.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.yearquarter.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.daterange.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.number.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.singleslider.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.intervalslider.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.widgets.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.content.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.image.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.generalquery.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.reset.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.query.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.web.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/detail/show.detail.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/detail/show.detailtable.detail.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/detail/field/show.dimension.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/detail/field/show.detail.dimension.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/detail/field/show.target.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/detail/field/customgroup/show.customgroup.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/detail/field/customsort/show.customsort.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/detail/field/numbercustomgroup/show.number.custom.group.js"
        };
    }

    public static String[] getShowJs() {

        List<String[]> jsList = new ArrayList<String[]>();
        jsList.add(DeziResourceHelper.getDeziAndShowJsModule());
        jsList.add(getShowJsModule());
        jsList.add(new String[]{
                "com/fr/bi/web/js/show/show.start.js",
                "com/fr/bi/web/js/show/model.js",
                "com/fr/bi/web/js/show/view.js",
                "com/fr/bi/web/js/show/modules/show.floatbox.manage.js",
                "com/fr/bi/web/js/show/modules/show.model.manage.js",
                "com/fr/bi/web/js/show/modules/show.view.manage.js",
                "com/fr/bi/web/js/show/modules/model/show.model.js",
                "com/fr/bi/web/js/show/modules/model/pane/model.pane.js",
        });
        jsList.add(getShowModelJs());
        jsList.add(getShowViewJs());
        String[] result = new String[]{};
        for (String[] js : jsList) {
            result = ArrayUtils.addAll(result, js);
        }
        return result;
    }
}
