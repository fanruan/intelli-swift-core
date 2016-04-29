FitView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(FitView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "mvc-fit-view"
        })
    },

    _init: function () {
        FitView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var fit = BI.createWidget({
            type: "bi.fit",
            widgetCreator: function (id, type) {
                return BI.createWidget({
                    type: "bi.button",
                    text: "id:" + id + ",type:" + type,
                    level: "ignore",
                    disabled: true
                })
            },
            element: vessel
        });
        fit.populate();
    }
});

FitModel = BI.inherit(BI.Model, {});