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
        "/pane/:id/:type/detail/:region/:dId": "getDimensionOrTarget"
    },

    getDetail: function (id, type) {
        switch (BI.parseInt(type)) {
            case BICst.Widget.TABLE:
            case BICst.Widget.CROSS_TABLE:
            case BICst.Widget.COMPLEX_TABLE:
            case BICst.Widget.BAR:
            case BICst.Widget.ACCUMULATE_BAR:
            case BICst.Widget.PIE:
            case BICst.Widget.DASHBOARD:
            case BICst.Widget.AXIS:
            case BICst.Widget.MAP:
            case BICst.Widget.DOUGHNUT:
            case BICst.Widget.BUBBLE:
            case BICst.Widget.SCATTER:
            case BICst.Widget.RADAR:
                return "BIDezi.DetailView";
            case BICst.Widget.DETAIL:
                return "BIDezi.DetailTableDetailView";
            case BICst.Widget.STRING:
                return "BIDezi.StringDetailView";
            case BICst.Widget.DATE:
                return "BIDezi.DateRangeDetailView";
            case BICst.Widget.NUMBER:
                return "BIDezi.NumberDetailView";
            case BICst.Widget.QUERY:
                break;
            case BICst.Widget.YEAR:
                return "BIDezi.YearDetailView";
            case BICst.Widget.QUARTER:
                return "BIDezi.YearQuarterDetailView";
            case BICst.Widget.MONTH:
                return "BIDezi.YearMonthDetailView";
            case BICst.Widget.YMD:
                return "BIDezi.DateDetailView";
            case BICst.Widget.TREE:
                return "BIDezi.TreeDetailView";
            case BICst.Widget.RESET:
        }
    },

    getWidget: function (id, type) {
        switch (BI.parseInt(type)) {
            case BICst.Widget.TABLE:
            case BICst.Widget.CROSS_TABLE:
            case BICst.Widget.COMPLEX_TABLE:
            case BICst.Widget.BAR:
            case BICst.Widget.ACCUMULATE_BAR:
            case BICst.Widget.PIE:
            case BICst.Widget.DASHBOARD:
            case BICst.Widget.AXIS:
            case BICst.Widget.MAP:
            case BICst.Widget.DOUGHNUT:
            case BICst.Widget.BUBBLE:
            case BICst.Widget.SCATTER:
            case BICst.Widget.RADAR:
                return "BIDezi.WidgetView";
            case BICst.Widget.Content:
                return "BIDezi.ContentWidgetView";
            case BICst.Widget.IMAGE:
                return "BIDezi.ImageWidgetView";
            case BICst.Widget.WEB:
                return "BIDezi.WebWidgetView";
            case BICst.Widget.DETAIL:
                return "BIDezi.DetailTableView";
            case BICst.Widget.STRING:
                return "BIDezi.StringWidgetView";
            case BICst.Widget.NUMBER:
                return "BIDezi.NumberWidgetView";
            case BICst.Widget.DATE:
                return "BIDezi.DateRangeView";
            case BICst.Widget.YEAR:
                return "BIDezi.YearWidgetView";
            case BICst.Widget.QUARTER:
                return "BIDezi.YearQuarterWidgetView";
            case BICst.Widget.MONTH:
                return "BIDezi.YearMonthWidgetView";
            case BICst.Widget.YMD:
                return "BIDezi.DateWidgetView";
            case BICst.Widget.TREE:
                return "BIDezi.TreeWidgetView";
            case BICst.Widget.QUERY:
                return "BIDezi.QueryView";
                break;
            case BICst.Widget.RESET:
                return "BIDezi.ResetView";
                break;
        }
    },

    getDimensionOrTarget: function (id, type, region, dId) {
        switch (BI.parseInt(type)) {
            case BICst.Widget.TABLE:
            case BICst.Widget.CROSS_TABLE:
            case BICst.Widget.COMPLEX_TABLE:
            case BICst.Widget.BAR:
            case BICst.Widget.ACCUMULATE_BAR:
            case BICst.Widget.PIE:
            case BICst.Widget.DASHBOARD:
            case BICst.Widget.AXIS:
            case BICst.Widget.MAP:
            case BICst.Widget.DOUGHNUT:
            case BICst.Widget.BUBBLE:
            case BICst.Widget.SCATTER:
            case BICst.Widget.RADAR:
                if (BI.parseInt(region) >= BI.parseInt(BICst.REGION.DIMENSION1) &&
                    BI.parseInt(BICst.REGION.TARGET1) > BI.parseInt(region)) {
                    return "BIDezi.DimensionView";
                }
                return "BIDezi.TargetView";
            case BICst.Widget.DETAIL:
                return "BIDezi.DetailDimensionView";
            case BICst.Widget.STRING:
                return "BIDezi.StringDimensionView";
            case BICst.Widget.NUMBER:
                return "BIDezi.NumberDimensionView";
            case BICst.Widget.DATE:
                return "BIDezi.DateDimensionView";
            case BICst.Widget.YEAR:
            case BICst.Widget.QUARTER:
            case BICst.Widget.MONTH:
            case BICst.Widget.YMD:
                return "BIDezi.DateDimensionView";
            case BICst.Widget.TREE:
                return "BIDezi.TreeDimensionView";
            case BICst.Widget.QUERY:
                break;
            case BICst.Widget.RESET:
                break;
        }
    }
}));