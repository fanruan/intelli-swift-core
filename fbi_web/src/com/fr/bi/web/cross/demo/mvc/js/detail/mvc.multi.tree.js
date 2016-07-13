MultiTreeView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(MultiTreeView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-multi-tree bi-mvc-layout"
        })
    },

    _init: function () {
        MultiTreeView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {


        var combo = BI.createWidget({
            type: "bi.multi_tree_combo",
            itemsCreator: function (op, callback) {
                var data = BI.extend({
                    floors: 4
                }, op);
                switch (op.type) {
                    case BI.TreeView.REQ_TYPE_INIT_DATA:
                        BI.requestAsync("bi_demo", "get_search_tree_node", data, callback);
                        break;
                    case BI.TreeView.REQ_TYPE_ADJUST_DATA:
                        callback(BI.requestSync("bi_demo", "adjust_tree_data_structure", data));
                        break;
                    case BI.TreeView.REQ_TYPE_CALCULATE_SELECT_DATA:
                        BI.requestAsync("bi_demo", "get_part_tree_selected_node", data, callback);
                        break;
                    case BI.TreeView.REQ_TYPE_SELECTED_DATA:
                        BI.requestAsync("bi_demo", "get_display_tree_node", data, callback);
                        break;
                    default :
                        BI.requestAsync("bi_demo", "get_tree_node", data, callback);
                        break;
                }
            }
        });

        var label = BI.createWidget({
            type: "bi.label",
            text: "这里显示的是树的选择值",
            whiteSpace: "normal"
        });

        combo.on(BI.MultiTreeCombo.EVENT_CONFIRM, function () {
            label.setText(JSON.stringify(combo.getValue()));
        });

        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [{
                el: combo,
                width: 200
            }, {
                el: label,
                tgap: 200
            }, {
                el: {
                    type: "bi.button",
                    width: 200,
                    height: 30,
                    handler: function(){
                        combo.setValue();
                    }
                }
            }],
            vgap: 20,
            hgap: 50
        })
    }
});

MultiTreeModel = BI.inherit(BI.Model, {

    _defaultConfig: function () {
        return BI.extend(MultiTreeModel.superclass._defaultConfig.apply(this, arguments), {})
    },

    _init: function () {
        MultiTreeModel.superclass._init.apply(this, arguments);
    }
});