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
                return "BIShow.WidgetView";
            case BICst.WIDGET.CONTENT:
                return "BIShow.ContentWidgetView";
            case BICst.WIDGET.IMAGE:
                return "BIShow.ImageWidgetView";
            case BICst.WIDGET.WEB:
                return "BIShow.WebWidgetView";
            case BICst.WIDGET.DETAIL:
                return "BIShow.DetailTableView";
            case BICst.WIDGET.STRING:
                return "BIShow.StringWidgetView";
            case BICst.WIDGET.NUMBER:
                return "BIShow.NumberWidgetView";
            case BICst.WIDGET.DATE:
                return "BIShow.DateRangeView";
            case BICst.WIDGET.YEAR:
                return "BIShow.YearWidgetView";
            case BICst.WIDGET.QUARTER:
                return "BIShow.YearQuarterWidgetView";
            case BICst.WIDGET.MONTH:
                return "BIShow.YearMonthWidgetView";
            case BICst.WIDGET.YMD:
                return "BIShow.DateWidgetView";
            case BICst.WIDGET.TREE:
                return "BIShow.TreeWidgetView";
            case BICst.WIDGET.GENERAL_QUERY:
                return "BIShow.GeneralQueryView";
            case BICst.WIDGET.QUERY:
                return "BIShow.QueryView";
                break;
            case BICst.WIDGET.RESET:
                return "BIShow.ResetView";
                break;
        }
    },

    getDimensionOrTarget: function (id, type, region, dId) {
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
                    return "BIShow.DimensionView";
                }
                return "BIShow.TargetView";
            case BICst.WIDGET.DETAIL:
                return "BIShow.DetailDimensionView";
            case BICst.WIDGET.STRING:
                return "BIShow.StringDimensionView";
            case BICst.WIDGET.NUMBER:
                return "BIShow.NumberDimensionView";
            case BICst.WIDGET.DATE:
                return "BIShow.DateDimensionView";
            case BICst.WIDGET.YEAR:
            case BICst.WIDGET.QUARTER:
            case BICst.WIDGET.MONTH:
            case BICst.WIDGET.YMD:
                return "BIShow.DateDimensionView";
            case BICst.WIDGET.TREE:
                return "BIShow.TreeDimensionView";
            case BICst.WIDGET.QUERY:
                break;
            case BICst.WIDGET.RESET:
                break;
        }
    }
}));
