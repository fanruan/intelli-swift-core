FineBIServiceView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(FineBIServiceView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-finebi-service bi-mvc-layout"
        })
    },

    _init: function () {
        FineBIServiceView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        BI.createWidget({
            type: "bi.finebi_service",
            element: vessel
        })
    }
});

FineBIServiceModel = BI.inherit(BI.Model, {});