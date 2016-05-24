IconComboView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(IconComboView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "mvc-icon-combo-view bi-mvc-layout"
        })
    },

    _init: function () {
        IconComboView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var combo = BI.createWidget({
            type: "bi.icon_combo",
            iconClass: "rename-font",
            items: [{
                value: "第一项",
                iconClass: "delete-font"
            }, {
                value: "第二项",
                iconClass: "rename-font"
            }, {
                value: "第三项",
                iconClass: "move-font"
            }]
        });
        BI.defer(function () {
            combo.showView();
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

IconComboModel = BI.inherit(BI.Model, {});