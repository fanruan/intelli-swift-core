LayerView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(LayerView.superclass._defaultConfig.apply(this, arguments),{
            baseCls: "bi-layer"
        })
    },

    _init: function(){
        LayerView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        var self = this;
        var btn = BI.createWidget({
            type: "bi.text_button",
            cls: "layer-button",
            text: "点击弹出Layer",
            width: 200,
            height: 80,
            handler: function(){
                BI.createWidget({
                    type: "bi.text_button",
                    element: BI.Layers.create("layer",self.text),
                    whiteSpace: "normal",
                    cls: "mvc-layer",
                    text: "点击此处关掉layer",
                    handler: function(){
                        BI.Layers.hide("layer");
                    }
                })
                BI.Layers.show("layer")
            }
        })

        BI.createWidget({
            type: "bi.division",
            element: vessel,
            columns: 2,
            rows: 1,
            items: [
                {
                    column: 0,
                    row: 0,
                    width: 0.4,
                    height: 1,
                    el: {
                        type: "bi.center_adapt",
                        items: [{
                            el: btn
                        }]
                    }
                }, {
                    column: 1,
                    row: 0,
                    width: 0.6,
                    height: 1,
                    el: (this.text = BI.createWidget({
                        type: "bi.label",
                        cls: "layer-background",
                        whiteSpace: "normal",
                        text: "这个文字上方会弹出layer层"
                    }))
                }
            ]
        })
    }
})

LayerModel = BI.inherit(BI.Model, {
    _defaultConfig: function(){
        return BI.extend(LayerModel.superclass._defaultConfig.apply(this, arguments),{

        })
    },

    _init: function(){
        LayerModel.superclass._init.apply(this, arguments);
    }
})