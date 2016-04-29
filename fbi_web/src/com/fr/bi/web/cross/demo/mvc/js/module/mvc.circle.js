CircleView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(CircleView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "mvc-etl-circle-view"
        })
    },

    _init: function () {
        CircleView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {

        var operatorPane = BI.createWidget({
            type: "bi.circle_self",
            cls: "mvc-circle-self",
            height: 400,
            width: 490
        });

        BI.createWidget({
            type: "bi.center_adapt",
            element: vessel,
            items: [operatorPane]
        });

        var table = BI.firstObject(Pool.tables);
        operatorPane.populate({
            fields: table.fields,
            table: table
        });
    }
});

CircleModel = BI.inherit(BI.Model, {

});