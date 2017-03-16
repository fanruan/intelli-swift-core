//center adapt布局
InlineCenterAdaptView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(InlineCenterAdaptView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-inline-center-adapt bi-mvc-layout"
        })
    },

    _init: function(){
        InlineCenterAdaptView.superclass._init.apply(this, arguments);
    },

    _createNoWidth: function(){
        return BI.createWidget({
            type: "bi.inline_center_adapt",
            items: [{
                type: "bi.label",
                text: "Inline Center Adapt 1",
                cls: "layout-bg1",
                width: 300,
                height: 30
            }]
        })
    },

    _createBottom: function(){
        return BI.createWidget({
            type: "bi.inline_center_adapt",
            items: [{
                type: "bi.text_button",
                text: "这个是有宽度和高度的按钮-1",
                height: 100,
                width: 160,
                cls: "layout-bg1"
            }, {
                type: "bi.text_button",
                text: "这个是有宽度和高度的按钮-2",
                height: 30,
                width: 160,
                cls: "layout-bg2"
            }, {
                type: "bi.text_button",
                text: "这个是有宽度和高度的按钮-3",
                height: 30,
                width: 160,
                cls: "layout-bg3"
            }, {
                type: "bi.text_button",
                text: "这个是有宽度和高度的按钮-4",
                height: 30,
                width: 160,
                cls: "layout-bg5"
            }]
        })
    },

    _render: function(vessel){
        BI.createWidget({
            type: "bi.grid",
            element: vessel,
            columns: 1,
            rows: 2,
            items: [{
                column: 0,
                row: 0,
                el: this._createNoWidth()
            }, {
                column: 0,
                row: 1,
                el: this._createBottom()
            }]
        })
    }
});

InlineCenterAdaptModel = BI.inherit(BI.Model, {
    _init: function(){
        InlineCenterAdaptModel.superclass._init.apply(this, arguments);
    }
});