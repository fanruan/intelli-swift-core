AddConditionView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(AddConditionView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-add-condition bi-mvc-layout"
        })
    },

    _init: function () {
        AddConditionView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var self = this, o = this.options;

        this.condition = BI.createWidget({
            type: "bi.chart_add_condition",
            height: 180
        });

        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: this.condition,
                left: 30,
                right: 30,
                top: 30,
                bottom:300
            }]
        });
    }

});

AddConditionModel = BI.inherit(BI.Model, {});
