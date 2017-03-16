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
    }
}));
