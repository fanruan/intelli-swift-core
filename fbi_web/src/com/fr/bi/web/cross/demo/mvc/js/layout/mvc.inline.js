//inline布局
InlineView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(FlowView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-inline bi-mvc-layout"
        })
    },

    _init: function(){
        InlineView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        BI.createWidget({
            type: "bi.inline",
            element: vessel,
            items: [{
                type: "bi.label",
                height: 30,
                text: "Left-1",
                cls: "layout-bg1",
                hgap: 200
            }, {
                type: "bi.label",
                height: 30,
                text: "Left-2",
                cls: "layout-bg2",
                hgap: 200
            }, {
                type: "bi.label",
                height: 30,
                text: "Left-3",
                cls: "layout-bg3",
                hgap: 200
            }, {
                type: "bi.label",
                height: 30,
                text: "Left-4",
                cls: "layout-bg4",
                hgap: 200
            }, {
                type: "bi.label",
                height: 30,
                text: "Left-5",
                cls: "layout-bg5",
                hgap: 200
            }],
            hgap: 20,
            vgap: 20
        })
    }
});

InlineModel = BI.inherit(BI.Model, {
    _init: function(){
        InlineModel.superclass._init.apply(this, arguments);
    }
});