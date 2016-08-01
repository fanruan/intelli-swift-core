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

        new BI.Tree();

        combo.populate(items);

        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [combo, {
                type: "bi.button",
                width: 100,
                text: "getValue",
                handler: function () {
                    BI.Msg.alert("", JSON.stringify(combo.getValue()));
                }
            }],
            vgap: 100
        })
    }
});

MultiLayerSelectTreeComboModel = BI.inherit(BI.Model, {});