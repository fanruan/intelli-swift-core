TreeView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(TreeView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-tree bi-mvc-layout"
        })
    },

    _init: function(){
        TreeView.superclass._init.apply(this, arguments);
    },

    _createDefaultTree: function(){
        var tree = BI.createWidget({
            type: "bi.tree"
        });
        tree.initTree([
            {"id":1, "pId":0, "text":"test1", open:true},
            {"id":11, "pId":1, "text":"test11"},
            {"id":12, "pId":1, "text":"test12"},
            {"id":111, "pId":11, "text":"test111"},
            {"id":2, "pId":0, "text":"test2", open:true},
            {"id":21, "pId":2, "text":"test21"},
            {"id":22, "pId":2, "text":"test22"}
        ])
        return tree;
    },

    _createAsyncTree: function(){
        this.asyncTree = BI.createWidget({
            type: "bi.tree",
            paras: {
                floors: 4,
                count: 20,
                selected_values: {"_0": {},"_5": {"_5_0":{}}}
            },
            op: "bi_demo",
            cmd: "get_tree_node"
        });
        this.asyncTree.stroke()
        return this.asyncTree;
    },

    _render: function(vessel){
        var self = this;
        BI.createWidget({
            type: "bi.grid",
            columns: 2,
            rows: 1,
            element: vessel,
            items: [{
                column: 0,
                row: 0,
                el: {
                    type: "bi.vtape",
                    items: [
                        {
                            el: this._createDefaultTree()
                        },
                        {
                            el: {
                                type: "bi.label",
                                text: 'tree.initTree([{"id":1, "pId":0, "text":"test1", open:true},{"id":11, "pId":1, "text":"test11"},{"id":12, "pId":1, "text":"test12"},{"id":111, "pId":11, "text":"test111"}])',
                                whiteSpace: "normal"
                            },
                            height: 50
                        }
                    ]
                }
            }, {
                column: 1,
                row: 0,
                el: {
                    type: "bi.vtape",
                    items: [
                        {
                            el: this._createAsyncTree()
                        },
                        {
                            el: {
                                type: "bi.text_button",
                                cls: "mvc-button layout-bg2",
                                text: "getValue",
                                height: 30,
                                handler: function () {
                                    BI.Msg.alert("",JSON.stringify(self.asyncTree.getValue()));
                                }
                            },
                            height: 30
                        }
                    ]
                }
            }]

        })
    }
});

TreeModel = BI.inherit(BI.Model, {
    _defaultConfig: function(){
        return BI.extend(TreeModel.superclass._defaultConfig.apply(this, arguments), {
        })
    },

    _init: function(){
        TreeModel.superclass._init.apply(this, arguments);
    }
});