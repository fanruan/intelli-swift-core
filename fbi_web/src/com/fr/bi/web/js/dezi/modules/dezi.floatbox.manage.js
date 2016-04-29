/**
 * @class BIDezi.FloatBoxes
 * @extends BI.FloatBoxRouter
 */
BIDezi.FloatBoxes = new (BI.inherit( BI.FloatBoxRouter, {
    routes: {
        "/pane/:id/:type/detail/:region/:id/dimensionFilter" : "BIDezi.DimensionFilterView",
        "/pane/:id/:type/detail/:region/:id/targetFilter" : "BIDezi.TargetFilterView",
        "/pane/:id/:type/detail/:region/:id/detailTargetFilter" : "BIDezi.TargetFilterView",
        "/pane/:id/:type/detail/:region/:id/numberCustomGroup" : "BIDezi.NumberCustomGroupView",
        "/pane/:id/:type/detail/:region/:id/customGroup":"BIDezi.CustomGroupView",
        "/pane/:id/:type/detail/:region/:id/customSort":"BIDezi.CustomSortView"
    }
}));