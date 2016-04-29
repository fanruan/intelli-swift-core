TreeValueChooserView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(TreeValueChooserView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-tree-value-chooser-combo bi-mvc-layout"
        })
    },

    _init: function () {
        TreeValueChooserView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var tree = [];
        for (var i = 0; i < 21; i++) {
            tree.push({
                value: i + "",
                text: i + "",
                id: i + "",
                pId: null
            });
            for (var j = 0; j < 9; j++) {
                tree.push({
                    value: i + "-" + j,
                    text: j + "",
                    id: i + "-" + j,
                    pId: i + ""
                })
            }
        }
        var widget = BI.createWidget({
            type: "bi.tree_value_chooser_combo",
            itemsCreator: function (op, callback) {
                callback(tree);
            }
        });
        BI.createWidget({
            type: "bi.vertical",
            hgap: 200,
            vgap: 10,
            element: vessel,
            items: [widget]
        })
    }
});

TreeValueChooserModel = BI.inherit(BI.Model, {});