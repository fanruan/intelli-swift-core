/**
 * Created by zcf on 2016/12/27.
 */
BI.ShowDimensionsManager = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.ShowDimensionsManager.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-show-dimension-manager",
            wId: "",
            dimensionCreator: BI.emptyFn
        });
    },
    _init: function () {
        BI.ShowDimensionsManager.superclass._init.apply(this, arguments);

        var self = this, o = this.options;

        var dimensionManagerType = this._getDimensionManagerType(BI.Utils.getWidgetTypeByID(o.wId));
        this.regionManager = BI.createWidget({
            type: dimensionManagerType,
            element: this.element,
            wId: o.wId,
            dimensionCreator: o.dimensionCreator
        });
        this.regionManager.on(BI.ShowRegionManager.EVENT_CHANGE, function () {
            self.fireEvent(BI.ShowDimensionsManager.EVENT_CHANGE)
        });
    },

    _getDimensionManagerType: function (widgetType) {
        var type = "bi.show_region_manager";
        switch (widgetType) {
            case BICst.WIDGET.COMBINE_CHART:
                type = "bi.show_region_manager_for_combine_chart";
                break;
            case BICst.WIDGET.DETAIL:
            case BICst.WIDGET.TABLE:
            case BICst.WIDGET.CROSS_TABLE:
            case BICst.WIDGET.COMPLEX_TABLE:
            case BICst.WIDGET.COLUMN:
            case BICst.WIDGET.ACCUMULATE_COLUMN:
            case BICst.WIDGET.PERCENT_ACCUMULATE_COLUMN:
            case BICst.WIDGET.COMPARE_COLUMN:
            case BICst.WIDGET.FALL_COLUMN:
            case BICst.WIDGET.BAR:
            case BICst.WIDGET.ACCUMULATE_BAR:
            case BICst.WIDGET.COMPARE_BAR:
            case BICst.WIDGET.PIE:
            case BICst.WIDGET.MULTI_PIE:
            case BICst.WIDGET.MAP:
            case BICst.WIDGET.GIS_MAP:
            case BICst.WIDGET.TREE_MAP:
            case BICst.WIDGET.GAUGE:
            case BICst.WIDGET.DONUT:
            case BICst.WIDGET.BUBBLE:
            case BICst.WIDGET.FORCE_BUBBLE:
            case BICst.WIDGET.SCATTER:
            case BICst.WIDGET.RADAR:
            case BICst.WIDGET.ACCUMULATE_RADAR:
            case BICst.WIDGET.LINE:
            case BICst.WIDGET.AREA:
            case BICst.WIDGET.ACCUMULATE_AREA:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
            case BICst.WIDGET.COMPARE_AREA:
            case BICst.WIDGET.RANGE_AREA:
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
            case BICst.WIDGET.FUNNEL:
            case BICst.WIDGET.PARETO:
            case BICst.WIDGET.HEAT_MAP:
                type = "bi.show_region_manager";
                break;
        }
        return type;
    },

    getValue: function () {
        return this.regionManager.getValue();
    },

    populate: function () {
        this.regionManager.populate();
    }
});
BI.ShowDimensionsManager.EVENT_CHANGE = "BI.ShowDimensionsManager.EVENT_CHANGE";
$.shortcut("bi.show_dimension_manager", BI.ShowDimensionsManager);