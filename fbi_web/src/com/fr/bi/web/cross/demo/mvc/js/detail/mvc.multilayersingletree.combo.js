MultiLayerSingleTreeComboView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(MultiLayerSingleTreeComboView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-multilayer-single-tree bi-mvc-layout"
        })
    },

    _init: function () {
        MultiLayerSingleTreeComboView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var items = BI.deepClone(TREE);
        var combo = BI.createWidget({
            type: "bi.multilayer_single_tree_combo",
            items: items
        });

        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [combo],
            vgap: 100
        })
    }
});

MultiLayerSingleTreeComboModel = BI.inherit(BI.Model, {});