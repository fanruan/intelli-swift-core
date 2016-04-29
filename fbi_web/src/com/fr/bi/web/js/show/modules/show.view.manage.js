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
                return "BIShow.WidgetView";
            case BICst.Widget.Content:
                return "BIShow.ContentWidgetView";
            case BICst.Widget.IMAGE:
                return "BIShow.ImageWidgetView";
            case BICst.Widget.WEB:
                return "BIShow.WebWidgetView";
            case BICst.Widget.DETAIL:
                return "BIShow.DetailTableView";
            case BICst.Widget.STRING:
                return "BIShow.StringWidgetView";
            case BICst.Widget.NUMBER:
                return "BIShow.NumberWidgetView";
            case BICst.Widget.DATE:
                return "BIShow.DateRangeView";
            case BICst.Widget.YEAR:
                return "BIShow.YearWidgetView";
            case BICst.Widget.QUARTER:
                return "BIShow.YearQuarterWidgetView";
            case BICst.Widget.MONTH:
                return "BIShow.YearMonthWidgetView";
            case BICst.Widget.YMD:
                return "BIShow.DateWidgetView";
            case BICst.Widget.TREE:
                return "BIShow.TreeDetailView";
            case BICst.Widget.QUERY:
                break;
            case BICst.Widget.RESET:
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
                    return "BIShow.DimensionView";
                }
                return "BIShow.DimensionView";
            case BICst.Widget.DETAIL:
                return "BIShow.DetailDimensionView";
            case BICst.Widget.STRING:
                return "BIShow.StringDimensionView";
            case BICst.Widget.NUMBER:
                return "BIShow.NumberDimensionView";
            case BICst.Widget.DATE:
                return "BIShow.DateDimensionView";
            case BICst.Widget.YEAR:
            case BICst.Widget.QUARTER:
            case BICst.Widget.MONTH:
            case BICst.Widget.YMD:
                return "BIShow.DateDimensionView";
            case BICst.Widget.TREE:
                return "BIShow.TreeDimensionView";
            case BICst.Widget.QUERY:
                break;
            case BICst.Widget.RESET:
                break;
        }
    }
}));
