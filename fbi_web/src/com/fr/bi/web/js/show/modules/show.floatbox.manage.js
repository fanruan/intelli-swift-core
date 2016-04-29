/**
 * @class BIShow.FloatBoxes
 * @extends BI.FloatBoxRouter
 */
BIShow.FloatBoxes = new (BI.inherit(BI.FloatBoxRouter, {
    routes: {
        "/pane/:id/:type/detail/:region/:id/dimensionFilter": "BIShow.DimensionFilterView",
        "/pane/:id/:type/detail/:region/:id/targetFilter": "BIShow.TargetFilterView",
        "/pane/:id/:type/detail/:region/:id/detailTargetFilter": "BIShow.TargetFilterView",
        "/pane/:id/:type/detail/:region/:id/numberCustomGroup": "BIShow.NumberCustomGroupView",
        "/pane/:id/:type/detail/:region/:id/customGroup": "BIShow.CustomGroupView",
        "/pane/:id/:type/detail/:region/:id/customSort": "BIShow.CustomSortView",
        "/pane/:id/:type/detail": "getDetail"

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
                return "BIShow.DetailView";
            case BICst.Widget.DETAIL:
                return "BIShow.DetailTableDetailView";
            case BICst.Widget.STRING:
                return "BIShow.StringDetailView";
            case BICst.Widget.DATE:
                return "BIShow.DateRangeDetailView";
            case BICst.Widget.NUMBER:
                return "BIShow.NumberDetailView";
            case BICst.Widget.QUERY:
                break;
            case BICst.Widget.YEAR:
                return "BIShow.YearDetailView";
            case BICst.Widget.QUARTER:
                return "BIShow.YearQuarterDetailView";
            case BICst.Widget.MONTH:
                return "BIShow.YearMonthDetailView";
            case BICst.Widget.YMD:
                return "BIShow.DateDetailView";
            case BICst.Widget.TREE:
                return "BIShow.TreeDetailView";
            case BICst.Widget.RESET:
        }
    }
}));
