/**
 * 维度管理器
 *
 * Created by GUY on 2016/3/16.
 * @class BI.StringDimensionsManager
 * @extends BI.Widget
 */
BI.StringDimensionsManager = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.StringDimensionsManager.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-string-dimensions-manager",
            dimensionCreator: BI.emptyFn,
            wId: ""
        });
    },

    _init: function () {
        BI.StringDimensionsManager.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.manager = BI.createWidget({
            type: "bi.string_regions_manager",
            element: this.element,
            dimensionCreator: o.dimensionCreator,
            wId: o.wId
        });
        this.manager.on(BI.RegionsManager.EVENT_CHANGE, function () {
            self.fireEvent(BI.StringDimensionsManager.EVENT_CHANGE, arguments);
        });
    },

    getValue: function () {
        return {view: this.manager.getValue()};
    },

    populate: function () {
        this.manager.populate();
    }
});
BI.StringDimensionsManager.EVENT_CHANGE = "StringDimensionsManager.EVENT_CHANGE";
$.shortcut('bi.string_dimensions_manager', BI.StringDimensionsManager);
