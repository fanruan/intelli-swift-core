SelectDataServiceView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(SelectDataServiceView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-select-data-service bi-mvc-layout"
        })
    },

    _init: function () {
        SelectDataServiceView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var service = BI.createWidget({

        })
    }
});

SelectDataServiceModel = BI.inherit(BI.Model, {});