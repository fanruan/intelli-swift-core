//vertical布局
VerticalView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(VerticalView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-vertical bi-mvc-layout"
        })
    },

    _init: function(){
        VerticalView.superclass._init.apply(this, arguments);
    },

    _createSubContainer: function(){
        return BI.createWidget({
            type: "bi.vertical",
            cls: "border",
            height: 300,
            items: [{
                type: "bi.text_button",
                cls: "layout-bg" + BI.random(1, 8),
                text: "这里设置了lgap(左边距)，rgap(右边距)，tgap(上边距)，bgap(下边距)",
                height: 30
            },{
                type: "bi.text_button",
                cls: "layout-bg" + BI.random(1, 8),
                text: "这里设置了lgap(左边距)，rgap(右边距)，tgap(上边距)，bgap(下边距)",
                height: 30
            },{
                type: "bi.text_button",
                cls: "layout-bg" + BI.random(1, 8),
                text: "这里设置了lgap(左边距)，rgap(右边距)，tgap(上边距)，bgap(下边距)",
                height: 30
            },{
                type: "bi.text_button",
                cls: "layout-bg" + BI.random(1, 8),
                text: "这里设置了lgap(左边距)，rgap(右边距)，tgap(上边距)，bgap(下边距)",
                height: 30
            },{
                type: "bi.text_button",
                cls: "layout-bg" + BI.random(1, 8),
                text: "这里设置了lgap(左边距)，rgap(右边距)，tgap(上边距)，bgap(下边距)",
                height: 30
            },{
                type: "bi.text_button",
                cls: "layout-bg" + BI.random(1, 8),
                text: "这里设置了lgap(左边距)，rgap(右边距)，tgap(上边距)，bgap(下边距)",
                height: 30
            },{
                type: "bi.text_button",
                cls: "layout-bg" + BI.random(1, 8),
                text: "这里设置了lgap(左边距)，rgap(右边距)，tgap(上边距)，bgap(下边距)",
                height: 30
            }],
            lgap: 20,
            rgap: 80,
            tgap: 80,
            bgap: 50
        })
    },

    _render: function(vessel){
        this.container = BI.createWidget({
            element: vessel,
            type: "bi.vertical",
            items: BI.createItems(this.model.get("items"), {
                type: "bi.text_button"
            }),
            hgap: 100,
            vgap: 20
        })
    },

    refresh: function(){
        this.container.addItem(this._createSubContainer());
    }
});

VerticalModel = BI.inherit(BI.Model, {
    _defaultConfig: function(){
        return BI.extend(VerticalModel.superclass._defaultConfig.apply(this, arguments), {
            items: [{
                cls: "layout-bg" + BI.random(1, 8),
                text: "这里设置了hgap(水平间距)，vgap(垂直间距)",
                height: 30
            }, {
                cls: "layout-bg" + BI.random(1, 8),
                text: "这里设置了hgap(水平间距)，vgap(垂直间距)",
                height: 30
            }]
        })
    },

    _init: function(){
        VerticalModel.superclass._init.apply(this, arguments);
    }
});