ColorChooserView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(ColorChooserView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-color-chooser bi-mvc-layout"
        })
    },

    _init: function () {
        ColorChooserView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: {
                    type: "bi.color_chooser",
                    width: 30,
                    height: 30
                },
                left: 100,
                top: 250
            }]
        })
    }
});

ColorChooserModel = BI.inherit(BI.Model, {});