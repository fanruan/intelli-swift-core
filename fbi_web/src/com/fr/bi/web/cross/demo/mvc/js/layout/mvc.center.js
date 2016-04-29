//center布局
CenterView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(CenterView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-center bi-mvc-layout"
        })
    },

    _init: function(){
        CenterView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        BI.createWidget({
            type: "bi.center",
            element: vessel,
            items: [{
                type: "bi.label",
                text: "Center 1，这里虽然设置label的高度30，但是最终影响高度的是center布局",
                cls: "layout-bg1",
                whiteSpace: "normal"
            },{
                type: "bi.label",
                text: "Center 2，为了演示label是占满整个的，用了一个whiteSpace:normal",
                cls: "layout-bg2",
                whiteSpace: "normal"
            },{
                type: "bi.label",
                text: "Center 3",
                cls: "layout-bg3"
            },{
                type: "bi.label",
                text: "Center 4",
                cls: "layout-bg5"
            }],
            hgap: 20,
            vgap: 20
        })
    }
});

CenterModel = BI.inherit(BI.Model, {
    _init: function(){
        CenterModel.superclass._init.apply(this, arguments);
    }
});