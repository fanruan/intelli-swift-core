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
                return "BIShow.DetailView";
            case BICst.WIDGET.DETAIL:
                return "BIShow.DetailTableDetailView";
            case BICst.WIDGET.STRING:
                return "BIShow.StringDetailView";
            case BICst.WIDGET.DATE:
                return "BIShow.DateRangeDetailView";
            case BICst.WIDGET.NUMBER:
                return "BIShow.NumberDetailView";
            case BICst.WIDGET.QUERY:
                break;
            case BICst.WIDGET.YEAR:
                return "BIShow.YearDetailView";
            case BICst.WIDGET.QUARTER:
                return "BIShow.YearQuarterDetailView";
            case BICst.WIDGET.MONTH:
                return "BIShow.YearMonthDetailView";
            case BICst.WIDGET.YMD:
                return "BIShow.DateDetailView";
            case BICst.WIDGET.TREE:
                return "BIShow.TreeDetailView";
            case BICst.WIDGET.RESET:
                break;
        }
    }
}));
