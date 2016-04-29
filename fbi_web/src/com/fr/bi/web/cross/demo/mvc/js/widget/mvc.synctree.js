SyncTreeView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(SyncTreeView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-sync-tree bi-mvc-layout"
        })
    },

    _init: function () {
        SyncTreeView.superclass._init.apply(this, arguments);
    },

    _createSyncTree: function () {
        this.syncTree = BI.createWidget({
            type: "bi.sync_tree",
            paras: {
                selected_values: {"_0": {}, "_5": {"_5_0": {}}}
            },
            itemsCreator: function (op, callback) {
                var data = BI.extend({
                    floors: 4
                }, op);
                BI.requestAsync("bi_demo", "get_tree_node", data, callback)
            }
        });
        this.syncTree.stroke();
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
    }
});

SyncTreeModel = BI.inherit(BI.Model, {
    _init: function () {
        SyncTreeModel.superclass._init.apply(this, arguments);
    }
});