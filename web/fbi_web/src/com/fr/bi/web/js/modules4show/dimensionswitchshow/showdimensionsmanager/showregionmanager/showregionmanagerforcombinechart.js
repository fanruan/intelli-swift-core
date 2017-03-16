/**
 * Created by zcf on 2016/12/27.
 */
BI.ShowRegionManagerForCombineChart = BI.inherit(BI.ShowRegionManager, {
    _defaultConfig: function () {
        return BI.extend(BI.ShowRegionManagerForCombineChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-show-region-manager",
            wId: "",
            dimensionCreator: BI.emptyFn
        })
    },

    _init: function () {
        BI.ShowRegionManagerForCombineChart.superclass._init.apply(this, arguments);
        this.scopes = {};
    },

    _createCard: function (viewType, regionType) {
        var self = this, o = this.options;

        var region;
        switch (regionType) {
            case BICst.REGION_TYPE.REGION_DIMENSION:
                region = this._createRegionDimension(viewType);
                break;
            case BICst.REGION_TYPE.REGION_WRAPPER_TARGET_SETTING:
                region = this._createRegionWrapperTargetSetting(viewType);
                break;
        }
        region.on(BI.ShowAbstractRegion.EVENT_CHANGE, function () {
            self.fireEvent(BI.ShowRegionManager.EVENT_CHANGE);
        });
        region.on(BI.ShowAbstractRegion.EVENT_START, function () {
            //设置拖动的维度type
            self.dragType = region.getViewType();
            self._dragStart();
        });
        region.on(BI.ShowAbstractRegion.EVENT_STOP, function () {
            self._dragStop();
        });
        return region;
    },

    _createRegionWrapperTargetSetting: function (viewType) {
        var self = this, o = this.options;
        var region = BI.createWidget({
            type: "bi.show_target_region_setting_wrapper",
            width: "100%",
            dimensionCreator: o.dimensionCreator,
            scopeCreator: BI.bind(this._createTargetScope, this),
            wId: o.wId,
            viewType: viewType
        });
        region.on(BI.ShowRegionWrapper.EVENT_CHANGE, function () {
            self.fireEvent(BI.ShowRegionManager.EVENT_CHANGE);
        });
        region.on(BI.ShowRegionWrapper.EVENT_START, function () {
            //设置拖动的维度type
            self.dragType = region.getViewType();
            self._dragStart();
        });
        region.on(BI.ShowRegionWrapper.EVENT_STOP, function () {
            self._dragStop();
        });
        return region;
    },

    _createTargetScope: function (regionType) {
        var self = this, o = this.options;
        var accumulation = BI.Utils.getSeriesAccumulationByWidgetID(o.wId);
        if (!this.scopes[regionType]) {
            this.scopes[regionType] = BI.createWidget({
                type: "bi.show_combine_chart_target_scope",
                regionType: regionType,
                wId: o.wId
            });
            this.scopes[regionType].on(BI.ShowCombineChartTargetScope.EVENT_CHANGE, function () {
                self.fireEvent(BI.ShowRegionManager.EVENT_CHANGE, arguments);
            });
        }
        this.scopes[regionType].setEnable(accumulation.type !== BICst.SERIES_ACCUMULATION.EXIST);
        return this.scopes[regionType];
    },

    getValue: function () {
        var val = BI.ShowRegionManagerForCombineChart.superclass.getValue.apply(this, arguments);
        var scopes = {};
        BI.each(this.scopes, function (regionType, scope) {
            scopes[regionType] = scope.getValue();
        });
        val.scopes = scopes;
        return val;
    },

    populate: function () {
        BI.ShowRegionManagerForCombineChart.superclass.populate.apply(this, arguments);
        var self = this, o = this.options;
        var view = BI.Utils.getWidgetViewByID(o.wId);
        var scopes = BI.Utils.getWidgetScopeByID(o.wId);
        BI.each(view, function (regionType) {
            if (BI.Utils.isTargetRegionByRegionType(regionType)) {
                var scope = self._createTargetScope(regionType);
                scope.setValue(scopes[regionType]);
            }
        });
        BI.remove(this.scopes, function (regionType) {
            return !view[regionType] || view[regionType].length === 0
        });
    }
});
$.shortcut("bi.show_region_manager_for_combine_chart", BI.ShowRegionManagerForCombineChart);