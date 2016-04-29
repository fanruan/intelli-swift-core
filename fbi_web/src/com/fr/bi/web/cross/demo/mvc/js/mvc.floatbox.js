FloatBoxView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(FloatBoxView.superclass._defaultConfig.apply(this, arguments),{
            baseCls: "bi-float-box"
        })
    },

    _init: function(){
        FloatBoxView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        var self = this;
        BI.createWidget({
            type: "bi.center_adapt",
            element: vessel,
            items: [{
                el: {
                    type: "bi.text_button",
                    cls: "float-box-button",
                    text: "点击弹出FloatBox",
                    width: 200,
                    height: 80,
                    handler: function(){
                        FloatBoxes.open("ddd", "test", {}, self);
                    }
                }
            }]
        })
    }
})

FloatBoxModel = BI.inherit(BI.Model, {
    _defaultConfig: function(){
        return BI.extend(FloatBoxModel.superclass._defaultConfig.apply(this, arguments),{
            "test": { "test": "测试数据"}
        })
    },

    _init: function(){
        FloatBoxModel.superclass._init.apply(this, arguments);
    }
})

FloatBoxChildView = BI.inherit(BI.BarFloatSection, {
    _defaultConfig: function(){
        return BI.extend(FloatBoxChildView.superclass._defaultConfig.apply(this, arguments),{
            baseCls: "bi-float-box-child"
        })
    },

    _init: function(){
        FloatBoxChildView.superclass._init.apply(this, arguments);
    },

    rebuildNorth : function(north) {
        BI.createWidget({
            type: "bi.label",
            element: north,
            textAlign: "left",
            text: this.model.get("test") + BI.UUID(),
            height: 50,
            hgap: 20
        })
        return true;
    },

    rebuildCenter : function(center) {
        this.center = center;
    },

//    close: function(){
//
//    },
    end: function(){
        this.model.destroy();
    },

    refresh: function(){
        BI.createWidget({
            type: "bi.label",
            element: this.center,
            text: "点击确定按钮会触发删除事件" + BI.UUID()
        })
    }
})

FloatBoxChildModel = BI.inherit(BI.Model, {
    _defaultConfig: function(){
        return BI.extend(FloatBoxChildModel.superclass._defaultConfig.apply(this, arguments),{

        })
    },

    _init: function(){
        FloatBoxChildModel.superclass._init.apply(this, arguments);
    }
})