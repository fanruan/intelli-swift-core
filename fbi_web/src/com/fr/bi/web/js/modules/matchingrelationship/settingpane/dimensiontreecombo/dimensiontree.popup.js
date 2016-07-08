/**
 * @class BI.DimensionTreePopup
 * @extends BI.Pane
 */

BI.DimensionTreePopup = BI.inherit(BI.Pane, {

    _defaultConfig: function () {
        return BI.extend(BI.DimensionTreePopup.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dimension-tree-popup",
            tipText: BI.i18nText("BI-No_Selected_Item"),
            items: []
        });
    },

    _init: function () {
        BI.DimensionTreePopup.superclass._init.apply(this, arguments);

        var self = this, o = this.options;

        this.tree = BI.createWidget({
            type: 'bi.multi_layer_single_empty_level_tree',
            expander: {
                isDefaultInit: true
            },
            items: o.items,
            chooseType: BI.Selection.Single
        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [this.tree]
        });

        this.tree.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });

        this.tree.on(BI.MultiLayerSelectLevelTree.EVENT_CHANGE, function () {
            self.fireEvent(BI.DimensionTreePopup.EVENT_CHANGE);
        });

        this.check();
    },

    getValue: function () {
        return this.tree.getValue();
    },

    setValue: function (v) {
        v = BI.isArray(v) ? v : [v];
        this.tree.setValue(v);
    },

    populate: function (items) {
        BI.DimensionTreePopup.superclass.populate.apply(this, arguments);
        this.tree.populate(items);
    }
});

BI.DimensionTreePopup.EVENT_CHANGE = "DimensionTreePopup.EVENT_CHANGE";
$.shortcut("bi.dimension_tree_popup", BI.DimensionTreePopup);
