TestDetailTablePopupView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(TestDetailTablePopupView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "mvc-detail-table-popup"
        })
    },

    _init: function () {
        TestDetailTablePopupView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var wIds = BI.Utils.getAllWidgetIDs();
        var dIds = BI.Utils.getAllTargetDimensionIDs(wIds[0]);
        var popup = BI.createWidget({
            type: "bi.detail_table_popup",
            dId: dIds[0]
        });

        BI.createWidget({
            type: "bi.center",
            element: vessel,
            hgap: 20,
            vgap: 20,
            items: [{
                type: "bi.grid",
                items: [[popup]]
            }]
        });
    }
});

TestDetailTablePopupModel = BI.inherit(BI.Model, {});