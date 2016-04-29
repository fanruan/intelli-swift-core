SimpleTreeView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(SimpleTreeView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-simple-tree bi-mvc-layout"
        })
    },

    _init: function () {
        SimpleTreeView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {

        //value值一定要是字符串
        var tree = BI.createWidget({
            type: "bi.simple_tree",
            items: []
        });

        tree.populate([{
            id: 1,
            text: "第一项",
            value: "1"
        }, {
            id: 2,
            text: "第二项",
            value: "2"
        }, {
            id: 3,
            text: "第三项",
            value: "3",
            open: true
        }, {
            id: 11,
            pId: 1,
            text: "子项1",
            value: "11"
        }, {
            id: 12,
            pId: 1,
            text: "子项2",
            value: "12"
        }, {
            id: 13,
            pId: 1,
            text: "子项3",
            value: "13"
        }, {
            id: 31,
            pId: 3,
            text: "子项1",
            value: "31"
        }, {
            id: 32,
            pId: 3,
            text: "子项2",
            value: "32"
        }, {
            id: 33,
            pId: 3,
            text: "子项3",
            value: "33"
        }], "z")
        BI.createWidget({
            type: "bi.vtape",
            element: vessel,
            items: [{
                el: tree
            }, {
                height: 30,
                el: {
                    type: "bi.button",
                    height: 30,
                    text: "setValue([12])",
                    handler: function () {
                        tree.setValue([12]);
                    }
                }
            }, {
                height: 30,
                el: {
                    type: "bi.button",
                    height: 30,
                    text: "getValue",
                    handler: function () {
                        BI.Msg.alert("", JSON.stringify(tree.getValue()));
                    }
                }
            }]
        })
    }
});

SimpleTreeModel = BI.inherit(BI.Model, {
    _init: function () {
        SimpleTreeModel.superclass._init.apply(this, arguments);
    }
});