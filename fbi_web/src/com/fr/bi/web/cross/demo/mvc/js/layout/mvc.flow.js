//left right布局
FlowView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(FlowView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-flow bi-mvc-layout"
        })
    },

    _init: function(){
        FlowView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        BI.createWidget({
            type: "bi.center_adapt",
            element: vessel,
            items: [{
                type: "bi.left",
                items: [{
                    type: "bi.label",
                    height: 30,
                    text: "Left-1",
                    cls: "layout-bg1",
                    hgap: 20
                }, {
                    type: "bi.label",
                    height: 30,
                    text: "Left-2",
                    cls: "layout-bg2",
                    hgap: 20
                }, {
                    type: "bi.label",
                    height: 30,
                    text: "Left-3",
                    cls: "layout-bg3",
                    hgap: 20
                }, {
                    type: "bi.label",
                    height: 30,
                    text: "Left-4",
                    cls: "layout-bg4",
                    hgap: 20
                }, {
                    type: "bi.label",
                    height: 30,
                    text: "Left-5",
                    cls: "layout-bg5",
                    hgap: 20
                }],
                hgap: 20,
                vgap: 20
            }, {
                type: "bi.right",
                items: [{
                    type: "bi.label",
                    height: 30,
                    text: "Right-1",
                    cls: "layout-bg1",
                    hgap: 20
                }, {
                    type: "bi.label",
                    height: 30,
                    text: "Right-2",
                    cls: "layout-bg2",
                    hgap: 20
                }, {
                    type: "bi.label",
                    height: 30,
                    text: "Right-3",
                    cls: "layout-bg3",
                    hgap: 20
                }, {
                    type: "bi.label",
                    height: 30,
                    text: "Right-4",
                    cls: "layout-bg4",
                    hgap: 20
                }, {
                    type: "bi.label",
                    height: 30,
                    text: "Right-5",
                    cls: "layout-bg5",
                    hgap: 20
                }],
                hgap: 20,
                vgap: 20
            }]
        })
    }
});

FlowModel = BI.inherit(BI.Model, {
    _init: function(){
        FlowModel.superclass._init.apply(this, arguments);
    }
});