/**
 * 维度管理器
 *
 * Created by GUY on 2016/3/16.
 * @class BI.TreeDimensionsManager
 * @extends BI.Widget
 */
BI.TreeDimensionsManager = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.TreeDimensionsManager.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-tree-dimensions-manager",
            dimensionCreator: BI.emptyFn,
            wId: ""
        });
    },

    _init: function () {
        BI.TreeDimensionsManager.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.manager = BI.createWidget({
            type: "bi.tree_regions_manager",
            element: this.element,
            dimensionCreator: o.dimensionCreator,
            wId: o.wId
        });
        this.manager.on(BI.RegionsManager.EVENT_CHANGE, function () {
            self.fireEvent(BI.TreeDimensionsManager.EVENT_CHANGE, arguments);
        });
    },

    getValue: function () {
        return {view: this.manager.getValue()};
    },

    populate: function () {
        this.manager.populate();
    }
});
BI.TreeDimensionsManager.EVENT_CHANGE = "TreeDimensionsManager.EVENT_CHANGE";
$.shortcut('bi.tree_dimensions_manager', BI.TreeDimensionsManager);
