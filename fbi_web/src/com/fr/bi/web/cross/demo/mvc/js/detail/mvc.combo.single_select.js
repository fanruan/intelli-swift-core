SingleSelectComboView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(SingleSelectComboView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "mvc-single-select-combo-view bi-mvc-layout"
        })
    },

    _init: function () {
        SingleSelectComboView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var combo = BI.createWidget({
            type: "bi.static_combo",
            text: "这个数字不会变",
            items: BI.deepClone(ITEMS),
            width: 200,
            height: 30
        });
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            hgap: 20,
            vgap: 30,
            items: [combo]
        })
    }
});

SingleSelectComboModel = BI.inherit(BI.Model, {});