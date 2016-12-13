/**
 * 维度管理器
 *
 * Created by GUY on 2016/3/16.
 * @class BI.DetailTableDimensionsManager
 * @extends BI.Widget
 */
BI.DetailTableDimensionsManager = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.DetailTableDimensionsManager.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detail-dimensions-manager",
            dimensionCreator: BI.emptyFn,
            wId: ""
        });
    },

    _init: function () {
        BI.DetailTableDimensionsManager.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.manager = BI.createWidget({
            type: "bi.detail_table_regions_manager",
            element: this.element,
            dimensionCreator: o.dimensionCreator,
            wId: o.wId
        });
        this.manager.on(BI.RegionsManager.EVENT_CHANGE, function () {
            self.fireEvent(BI.DetailTableDimensionsManager.EVENT_CHANGE, arguments);
        });
    },

    getValue: function () {
        return {view: this.manager.getValue()};
    },

    populate: function () {
        this.manager.populate();
    }
});
BI.DetailTableDimensionsManager.EVENT_CHANGE = "DetailTableDimensionsManager.EVENT_CHANGE";
$.shortcut('bi.detail_table_dimensions_manager', BI.DetailTableDimensionsManager);
