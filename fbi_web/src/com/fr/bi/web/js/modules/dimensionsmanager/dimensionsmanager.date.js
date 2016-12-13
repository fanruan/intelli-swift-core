/**
 * 维度管理器
 *
 * Created by GUY on 2016/3/16.
 * @class BI.DateDimensionsManager
 * @extends BI.Widget
 */
BI.DateDimensionsManager = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.DateDimensionsManager.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-date-dimensions-manager",
            dimensionCreator: BI.emptyFn,
            wId: ""
        });
    },

    _init: function () {
        BI.DateDimensionsManager.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.manager = BI.createWidget({
            type: "bi.date_regions_manager",
            element: this.element,
            dimensionCreator: o.dimensionCreator,
            wId: o.wId
        });
        this.manager.on(BI.RegionsManager.EVENT_CHANGE, function () {
            self.fireEvent(BI.DateDimensionsManager.EVENT_CHANGE, arguments);
        });
    },

    getValue: function () {
        return {view: this.manager.getValue()};
    },

    populate: function () {
        this.manager.populate();
    }
});
BI.DateDimensionsManager.EVENT_CHANGE = "DateDimensionsManager.EVENT_CHANGE";
$.shortcut('bi.date_dimensions_manager', BI.DateDimensionsManager);
