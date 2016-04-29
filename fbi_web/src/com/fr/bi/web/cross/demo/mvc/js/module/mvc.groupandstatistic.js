GroupStatisticView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(GroupStatisticView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "mvc-etl-group-statistic-view"
        })
    },

    _init: function () {
        GroupStatisticView.superclass._init.apply(this, arguments);
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

GroupStatisticModel = BI.inherit(BI.Model, {

});