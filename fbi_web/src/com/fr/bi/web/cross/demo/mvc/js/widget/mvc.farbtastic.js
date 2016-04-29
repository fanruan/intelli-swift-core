FarbtasticView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(FarbtasticView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-farbtastic bi-mvc-layout"
        })
    },

    _init: function () {
        FarbtasticView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var farbtastic = BI.createWidget({
            type: "bi.farbtastic"
        });

        farbtastic.on(BI.Farbtastic.EVENT_CHANGE, function () {
            BI.Msg.toast(this.getValue());
        });

        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: farbtastic,
                left: 100,
                top: 50
            }]
        })
    }
});

FarbtasticModel = BI.inherit(BI.Model, {});