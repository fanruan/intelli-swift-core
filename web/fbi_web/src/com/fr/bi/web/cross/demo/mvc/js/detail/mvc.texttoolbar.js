TextToolbarView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(TextToolbarView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-text-toolbar bi-mvc-layout"
        })
    },

    _init: function () {
        TextToolbarView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var pane = BI.createWidget({
            type: "bi.layout",
            width: 200,
            height: 100
        });
        pane.element.text("这是一个测试数据");
        var toolbar = BI.createWidget({
            type: "bi.text_toolbar"
        });
        toolbar.on(BI.TextToolbar.EVENT_CHANGE, function () {
            pane.element.css(this.getValue());
            BI.Msg.toast(JSON.stringify(this.getValue()));
        });
        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: toolbar,
                left: 100,
                top: 250
            }, {
                el: pane,
                left: 100,
                top: 50
            }]
        })
    }
});

TextToolbarModel = BI.inherit(BI.Model, {});