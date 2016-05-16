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
        switch (BI.parseInt(type)) {
            case BICst.WIDGET.TABLE:
            case BICst.WIDGET.CROSS_TABLE:
            case BICst.WIDGET.COMPLEX_TABLE:
            case BICst.WIDGET.BAR:
            case BICst.WIDGET.ACCUMULATE_BAR:
            case BICst.WIDGET.PIE:
            case BICst.WIDGET.DASHBOARD:
            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.MAP:
            case BICst.WIDGET.DONUT:
            case BICst.WIDGET.BUBBLE:
            case BICst.WIDGET.SCATTER:
            case BICst.WIDGET.RADAR:
                return "BIDezi.DetailView";
            case BICst.WIDGET.DETAIL:
                return "BIDezi.DetailTableDetailView";
            case BICst.WIDGET.STRING:
                return "BIDezi.StringDetailView";
            case BICst.WIDGET.DATE:
                return "BIDezi.DateRangeDetailView";
            case BICst.WIDGET.NUMBER:
                return "BIDezi.NumberDetailView";
            case BICst.WIDGET.QUERY:
                break;
            case BICst.WIDGET.YEAR:
                return "BIDezi.YearDetailView";
            case BICst.WIDGET.QUARTER:
                return "BIDezi.YearQuarterDetailView";
            case BICst.WIDGET.MONTH:
                return "BIDezi.YearMonthDetailView";
            case BICst.WIDGET.YMD:
                return "BIDezi.DateDetailView";
            case BICst.WIDGET.TREE:
                return "BIDezi.TreeDetailView";
            case BICst.WIDGET.RESET:
        }
    },

    getWidget: function (id, type) {
        switch (BI.parseInt(type)) {
            case BICst.WIDGET.TABLE:
            case BICst.WIDGET.CROSS_TABLE:
            case BICst.WIDGET.COMPLEX_TABLE:
            case BICst.WIDGET.BAR:
            case BICst.WIDGET.ACCUMULATE_BAR:
            case BICst.WIDGET.PIE:
            case BICst.WIDGET.DASHBOARD:
            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.MAP:
            case BICst.WIDGET.DONUT:
            case BICst.WIDGET.BUBBLE:
            case BICst.WIDGET.SCATTER:
            case BICst.WIDGET.RADAR:
                return "BIDezi.WidgetView";
            case BICst.WIDGET.CONTENT:
                return "BIDezi.ContentWidgetView";
            case BICst.WIDGET.IMAGE:
                return "BIDezi.ImageWidgetView";
            case BICst.WIDGET.WEB:
                return "BIDezi.WebWidgetView";
            case BICst.WIDGET.DETAIL:
                return "BIDezi.DetailTableView";
            case BICst.WIDGET.STRING:
                return "BIDezi.StringWidgetView";
            case BICst.WIDGET.NUMBER:
                return "BIDezi.NumberWidgetView";
            case BICst.WIDGET.DATE:
                return "BIDezi.DateRangeView";
            case BICst.WIDGET.YEAR:
                return "BIDezi.YearWidgetView";
            case BICst.WIDGET.QUARTER:
                return "BIDezi.YearQuarterWidgetView";
            case BICst.WIDGET.MONTH:
                return "BIDezi.YearMonthWidgetView";
            case BICst.WIDGET.YMD:
                return "BIDezi.DateWidgetView";
            case BICst.WIDGET.TREE:
                return "BIDezi.TreeWidgetView";
            case BICst.WIDGET.GENERAL_QUERY:
                return "BIDezi.GeneralQueryView";
            case BICst.WIDGET.QUERY:
                return "BIDezi.QueryView";
                break;
            case BICst.WIDGET.RESET:
                return "BIDezi.ResetView";
                break;
        }
    },

    getDimensionOrTarget: function (id, type, region, dId) {
        switch (BI.parseInt(type)) {
            case BICst.WIDGET.TABLE:
            case BICst.WIDGET.CROSS_TABLE:
            case BICst.WIDGET.COMPLEX_TABLE:
            case BICst.WIDGET.BAR:
            case BICst.WIDGET.ACCUMULATE_BAR:
            case BICst.WIDGET.PIE:
            case BICst.WIDGET.DASHBOARD:
            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.MAP:
            case BICst.WIDGET.DONUT:
            case BICst.WIDGET.BUBBLE:
            case BICst.WIDGET.SCATTER:
            case BICst.WIDGET.RADAR:
                if (BI.parseInt(region) >= BI.parseInt(BICst.REGION.DIMENSION1) &&
                    BI.parseInt(BICst.REGION.TARGET1) > BI.parseInt(region)) {
                    return "BIDezi.DimensionView";
                }
                return "BIDezi.TargetView";
            case BICst.WIDGET.DETAIL:
                return "BIDezi.DetailDimensionView";
            case BICst.WIDGET.STRING:
                return "BIDezi.StringDimensionView";
            case BICst.WIDGET.NUMBER:
                return "BIDezi.NumberDimensionView";
            case BICst.WIDGET.DATE:
                return "BIDezi.DateDimensionView";
            case BICst.WIDGET.YEAR:
            case BICst.WIDGET.QUARTER:
            case BICst.WIDGET.MONTH:
            case BICst.WIDGET.YMD:
                return "BIDezi.DateDimensionView";
            case BICst.WIDGET.TREE:
                return "BIDezi.TreeDimensionView";
            case BICst.WIDGET.QUERY:
                break;
            case BICst.WIDGET.RESET:
                break;
        }
    }
}));