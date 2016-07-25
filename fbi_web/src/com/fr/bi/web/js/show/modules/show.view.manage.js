/**
 * @class BIConf.Views
 * @extends BI.WRouter
 */
BIShow.Views = new (BI.inherit(BI.WRouter, {
    routes: {
        "/": "BIShow.View",
        "/pane": "BIShow.PaneView",
        "/pane/:id/:type": "getWidget",
        "/pane/:id/:type/detail/:region/:dId": "getDimensionOrTarget",
    },

    getWidget: function (id, type) {
        var view = "";
        switch (BI.parseInt(type)) {
            case BICst.WIDGET.TABLE:
            case BICst.WIDGET.CROSS_TABLE:
            case BICst.WIDGET.COMPLEX_TABLE:
            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.ACCUMULATE_AXIS:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
            case BICst.WIDGET.COMPARE_AXIS:
            case BICst.WIDGET.FALL_AXIS:
            case BICst.WIDGET.BAR:
            case BICst.WIDGET.ACCUMULATE_BAR:
            case BICst.WIDGET.COMPARE_BAR:
            case BICst.WIDGET.LINE:
            case BICst.WIDGET.AREA:
            case BICst.WIDGET.ACCUMULATE_AREA:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
            case BICst.WIDGET.COMPARE_AREA:
            case BICst.WIDGET.RANGE_AREA:
            case BICst.WIDGET.COMBINE_CHART:
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
            case BICst.WIDGET.PIE:
            case BICst.WIDGET.DONUT:
            case BICst.WIDGET.MAP:
            case BICst.WIDGET.GIS_MAP:
            case BICst.WIDGET.DASHBOARD:
            case BICst.WIDGET.BUBBLE:
            case BICst.WIDGET.FORCE_BUBBLE:
            case BICst.WIDGET.SCATTER:
            case BICst.WIDGET.RADAR:
            case BICst.WIDGET.ACCUMULATE_RADAR:
            case BICst.WIDGET.FUNNEL:
                view = "BIShow.WidgetView";
                break;
            case BICst.WIDGET.CONTENT:
                view = "BIShow.ContentWidgetView";
                break;
            case BICst.WIDGET.IMAGE:
                view = "BIShow.ImageWidgetView";
                break;
            case BICst.WIDGET.WEB:
                view = "BIShow.WebWidgetView";
                break;
            case BICst.WIDGET.DETAIL:
                view = "BIShow.DetailTableView";
                break;
            case BICst.WIDGET.STRING:
                view = "BIShow.StringWidgetView";
                break;
            case BICst.WIDGET.NUMBER:
                view = "BIShow.NumberWidgetView";
                break;
            case BICst.WIDGET.DATE:
                view = "BIShow.DateRangeView";
                break;
            case BICst.WIDGET.YEAR:
                view = "BIShow.YearWidgetView";
                break;
            case BICst.WIDGET.QUARTER:
                view = "BIShow.YearQuarterWidgetView";
                break;
            case BICst.WIDGET.MONTH:
                view = "BIShow.YearMonthWidgetView";
                break;
            case BICst.WIDGET.YMD:
                view = "BIShow.DateWidgetView";
                break;
            case BICst.WIDGET.TREE:
                view = "BIShow.TreeWidgetView";
                break;
            case BICst.WIDGET.GENERAL_QUERY:
                view = "BIShow.GeneralQueryView";
                break;
            case BICst.WIDGET.QUERY:
                view = "BIShow.QueryView";
                break;
            case BICst.WIDGET.RESET:
                view = "BIShow.ResetView";
                break;
            default:
                view = "BIShow.WidgetView";
                break;
        }
        return view;
    },

    getDimensionOrTarget: function (id, type, region, dId) {
        var view = "";
        switch (BI.parseInt(type)) {
            case BICst.WIDGET.TABLE:
            case BICst.WIDGET.CROSS_TABLE:
            case BICst.WIDGET.COMPLEX_TABLE:
            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.ACCUMULATE_AXIS:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
            case BICst.WIDGET.COMPARE_AXIS:
            case BICst.WIDGET.FALL_AXIS:
            case BICst.WIDGET.BAR:
            case BICst.WIDGET.ACCUMULATE_BAR:
            case BICst.WIDGET.COMPARE_BAR:
            case BICst.WIDGET.LINE:
            case BICst.WIDGET.AREA:
            case BICst.WIDGET.ACCUMULATE_AREA:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
            case BICst.WIDGET.COMPARE_AREA:
            case BICst.WIDGET.RANGE_AREA:
            case BICst.WIDGET.COMBINE_CHART:
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
            case BICst.WIDGET.PIE:
            case BICst.WIDGET.DONUT:
            case BICst.WIDGET.MAP:
            case BICst.WIDGET.GIS_MAP:
            case BICst.WIDGET.DASHBOARD:
            case BICst.WIDGET.BUBBLE:
            case BICst.WIDGET.FORCE_BUBBLE:
            case BICst.WIDGET.SCATTER:
            case BICst.WIDGET.RADAR:
            case BICst.WIDGET.ACCUMULATE_RADAR:
            case BICst.WIDGET.FUNNEL:
                if (BI.parseInt(region) >= BI.parseInt(BICst.REGION.DIMENSION1) &&
                    BI.parseInt(BICst.REGION.TARGET1) > BI.parseInt(region)) {
                    view = "BIShow.DimensionView";
                    break;
                }
                view = "BIShow.TargetView";
                break;
            case BICst.WIDGET.DETAIL:
                view = "BIShow.DetailDimensionView";
                break;
            case BICst.WIDGET.STRING:
                view = "BIShow.StringDimensionView";
                break;
            case BICst.WIDGET.NUMBER:
                view = "BIShow.NumberDimensionView";
                break;
            case BICst.WIDGET.DATE:
                view = "BIShow.DateDimensionView";
                break;
            case BICst.WIDGET.YEAR:
            case BICst.WIDGET.QUARTER:
            case BICst.WIDGET.MONTH:
            case BICst.WIDGET.YMD:
                view = "BIShow.DateDimensionView";
                break;
            case BICst.WIDGET.TREE:
                view = "BIShow.TreeDimensionView";
                break;
            case BICst.WIDGET.QUERY:
                view = "";
                break;
            case BICst.WIDGET.RESET:
                view = "";
                break;
        }
        return view;
    }
}))
;
