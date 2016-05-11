SwitchTreeView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(SwitchTreeView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-switch-tree bi-mvc-layout"
        })
    },

    _init: function () {
        SwitchTreeView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var self = this;
        var tree = BI.createWidget({
            type: "bi.switch_tree",
            items: BI.deepClone(TREE)
        });

        BI.createWidget({
            type: "bi.vtape",
            element: vessel,
            items: [{
                el: tree
            }, {
                el: {
                    type: "bi.button",
                    height: 25,
                    text: "点击切换",
                    handler: function () {
                        tree.switchSelect();
                    }
                },
                height: 25
            }, {
                el: {
                    type: "bi.button",
                    height: 25,
                    text: "getValue",
                    handler: function () {
                        BI.Msg.alert("", JSON.stringify(tree.getValue()));
                    }
                },
                height: 25
            }, {
                el: {
                    type: "bi.button",
                    height: 25,
                    text: "setValue",
                    handler: function () {
                        tree.setValue(["第二级文件1"]);
                    }
                },
                height: 25
            }]
        })
    }
});

SwitchTreeModel = BI.inherit(BI.Model, {});
