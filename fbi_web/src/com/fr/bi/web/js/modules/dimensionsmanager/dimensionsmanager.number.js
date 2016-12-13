/**
 * 维度管理器
 *
 * Created by GUY on 2016/3/16.
 * @class BI.NumberDimensionsManager
 * @extends BI.Widget
 */
BI.NumberDimensionsManager = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.NumberDimensionsManager.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-number-dimensions-manager",
            dimensionCreator: BI.emptyFn,
            wId: ""
        });
    },

    _init: function () {
        BI.NumberDimensionsManager.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.manager = BI.createWidget({
            type: "bi.number_regions_manager",
            element: this.element,
            dimensionCreator: o.dimensionCreator,
            wId: o.wId
        });
        this.manager.on(BI.RegionsManager.EVENT_CHANGE, function () {
            self.fireEvent(BI.NumberDimensionsManager.EVENT_CHANGE, arguments);
        });
    },

    getValue: function () {
        return {view: this.manager.getValue()};
    },

    populate: function () {
        this.manager.populate();
    }
});
BI.NumberDimensionsManager.EVENT_CHANGE = "NumberDimensionsManager.EVENT_CHANGE";
$.shortcut('bi.number_dimensions_manager', BI.NumberDimensionsManager);
