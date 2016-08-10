/**
 * @class BIConf.Views
 * @extends BI.WRouter
 */
BIDezi.Views = new (BI.inherit(BI.WRouter, {
    routes: {
        "/": "BIDezi.View",
        "/pane": "BIDezi.PaneView",
        "/pane/:id/:type": "getWidget",
        "/pane/:id/:type/detail": "getDetail",
        "/pane/:id/:type/detail/:region/:dId": "getDimensionOrTarget",


        "/detailtablepopup": "BI.DetailTablePopupView",
        "/detailtablepopup/:dId": "BI.DetailTablePopupDetailDimensionView"
    },

    getDetail: function (id, type) {
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
                view = "BIDezi.DetailView";
                break;
            case BICst.WIDGET.DETAIL:
                view = "BIDezi.DetailTableDetailView";
                break;
            case BICst.WIDGET.STRING:
                view = "BIDezi.StringDetailView";
                break;
            case BICst.WIDGET.DATE:
                view = "BIDezi.DateRangeDetailView";
                break;
            case BICst.WIDGET.NUMBER:
                view = "BIDezi.NumberDetailView";
                break;
            case BICst.WIDGET.QUERY:
            case BICst.WIDGET.YEAR:
                view = "BIDezi.YearDetailView";
                break;
            case BICst.WIDGET.QUARTER:
                view = "BIDezi.YearQuarterDetailView";
                break;
            case BICst.WIDGET.MONTH:
                view = "BIDezi.YearMonthDetailView";
                break;
            case BICst.WIDGET.YMD:
                view = "BIDezi.DateDetailView";
                break;
            case BICst.WIDGET.TREE:
                view = "BIDezi.TreeDetailView";
                break;
            default:
                view = "BIDezi.DetailView";
                break;
        }
        return view;
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
                view = "BIDezi.WidgetView";
                break;
            case BICst.WIDGET.CONTENT:
                view = "BIDezi.ContentWidgetView";
                break;
            case BICst.WIDGET.IMAGE:
                view = "BIDezi.ImageWidgetView";
                break;
            case BICst.WIDGET.WEB:
                view = "BIDezi.WebWidgetView";
                break;
            case BICst.WIDGET.DETAIL:
                view = "BIDezi.DetailTableView";
                break;
            case BICst.WIDGET.STRING:
                view = "BIDezi.StringWidgetView";
                break;
            case BICst.WIDGET.NUMBER:
                view = "BIDezi.NumberWidgetView";
                break;
            case BICst.WIDGET.DATE:
                view = "BIDezi.DateRangeView";
                break;
            case BICst.WIDGET.YEAR:
                view = "BIDezi.YearWidgetView";
                break;
            case BICst.WIDGET.QUARTER:
                view = "BIDezi.YearQuarterWidgetView";
                break;
            case BICst.WIDGET.MONTH:
                view = "BIDezi.YearMonthWidgetView";
                break;
            case BICst.WIDGET.YMD:
                view = "BIDezi.DateWidgetView";
                break;
            case BICst.WIDGET.TREE:
                view = "BIDezi.TreeWidgetView";
                break;
            case BICst.WIDGET.GENERAL_QUERY:
                view = "BIDezi.GeneralQueryView";
                break;
            case BICst.WIDGET.QUERY:
                view = "BIDezi.QueryView";
                break;
            case BICst.WIDGET.RESET:
                view = "BIDezi.ResetView";
                break;
            default:
                view = "BIDezi.WidgetView";
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
                    view = "BIDezi.DimensionView";
                    break;
                }
                view = "BIDezi.TargetView";
                break;
            case BICst.WIDGET.DETAIL:
                view = "BIDezi.DetailDimensionView";
                break;
            case BICst.WIDGET.STRING:
                view = "BIDezi.StringDimensionView";
                break;
            case BICst.WIDGET.NUMBER:
                view = "BIDezi.NumberDimensionView";
                break;
            case BICst.WIDGET.DATE:
                view = "BIDezi.DateDimensionView";
                break;
            case BICst.WIDGET.YEAR:
            case BICst.WIDGET.QUARTER:
            case BICst.WIDGET.MONTH:
            case BICst.WIDGET.YMD:
                view = "BIDezi.DateDimensionView";
                break;
            case BICst.WIDGET.TREE:
                view = "BIDezi.TreeDimensionView";
                break;
            case BICst.WIDGET.QUERY:
                view = "";
                break;
            case BICst.WIDGET.RESET:
                view = "";
                break;
            default:
                view = "BIDezi.DimensionView";
                break;
        }
        return view;
    }
}));