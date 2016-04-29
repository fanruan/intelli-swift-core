PartTreeView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(PartTreeView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-part-tree bi-mvc-layout"
        })
    },

    _init: function () {
        PartTreeView.superclass._init.apply(this, arguments);
    },

    _createSyncTree: function () {
        this.syncTree = BI.createWidget({
            type: "bi.part_tree",
            paras: {},
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
                    default :
                        BI.requestAsync("bi_demo", "get_tree_node", data, callback);
                        break;
                }
            }
        });
        return this.syncTree;
    },

    _render: function (vessel) {
        var self = this;
        BI.createWidget({
            type: "bi.vtape",
            element: vessel,
            items: [
                {
                    el: this._createSyncTree()
                },
                {
                    el: {
                        type: "bi.text_button",
                        cls: "mvc-button layout-bg2",
                        text: "getValue",
                        height: 30,
                        handler: function () {
                            BI.Msg.alert("", JSON.stringify(self.syncTree.getValue()));
                        }
                    },
                    height: 30
                }
            ]
        });
    },

    refresh: function () {
        this.syncTree.stroke({
            keyword: "1",
            selected_values: {"_0": {}, "_5": {"_5_0": {}, "_5_1": {"_5,_5_1_1": {}}}}
        })
    }
});

PartTreeModel = BI.inherit(BI.Model, {
    _init: function () {
        PartTreeModel.superclass._init.apply(this, arguments);
    }
});