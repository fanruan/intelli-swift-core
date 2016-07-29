MultiLayerSelectTreeComboView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(MultiLayerSelectTreeComboView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-multilayer-select-tree bi-mvc-layout"
        })
    },

    _init: function () {
        MultiLayerSelectTreeComboView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var items = BI.deepClone(TREEWITHCHILDREN);
        var combo = BI.createWidget({
            type: "bi.multilayer_select_tree_combo"
        });

        combo.populate(items);

        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [combo],
            vgap: 100
        })
    }
});

MultiLayerSelectTreeComboModel = BI.inherit(BI.Model, {});