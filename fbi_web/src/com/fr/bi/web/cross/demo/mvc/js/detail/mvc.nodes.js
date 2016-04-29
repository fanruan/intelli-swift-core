NodesView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(NodesView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-nodes bi-mvc-layout"
        })
    },

    _init: function () {
        NodesView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [{
                type: "bi.label",
                height: 30,
                text: "十字形的节点"
            }, {
                type: "bi.plus_group_node",
                text: "十字形的节点"
            }, {
                type: "bi.label",
                height: 30,
                text: "三角形的节点"
            }, {
                type: "bi.triangle_group_node",
                text: "三角形的节点"
            }, {
                type: "bi.label",
                height: 30,
                text: "箭头节点"
            }, {
                type: "bi.arrow_group_node",
                text: "箭头节点"
            }]
        })
    }
});

NodesModel = BI.inherit(BI.Model, {});