DisplayTreeView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(DisplayTreeView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-display-tree bi-mvc-layout"
        })
    },

    _init: function(){
        DisplayTreeView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        var tree = BI.createWidget({
            type: "bi.display_tree",
            element: vessel
        });

        tree.initTree([{
            id: 1,
            text: "第一项",
            open: true
        }, {
            id: 2,
            text: "第二项"
        }, {
            id: 11,
            pId: 1,
            text: "子项1(共2个)",
            open: true
        }, {
            id: 111,
            pId: 11,
            text: "子子项1"
        }, {
            id: 112,
            pId: 11,
            text: "子子项2"
        }, {
            id: 12,
            pId: 1,
            text: "子项2"
        }, {
            id: 13,
            pId: 1,
            text: "子项3"
        }]);
    }
});

DisplayTreeModel = BI.inherit(BI.Model, {
    _init: function(){
        DisplayTreeModel.superclass._init.apply(this, arguments);
    }
});