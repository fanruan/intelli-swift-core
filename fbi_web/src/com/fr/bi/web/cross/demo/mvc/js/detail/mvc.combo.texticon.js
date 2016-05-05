TextIconComboView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(TextIconComboView.superclass._defaultConfig.apply(this, arguments), {})
    },

    _init: function () {
        TextIconComboView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var combo = BI.createWidget({
            type: "bi.text_value_combo",
            cls: "bi-mvc-text-icon-combo",
            width: 200,
            height: 30,
            items: [{
                text: "MVC-1",
                value: 1
            }, {
                text: "MVC-2",
                value: 2
            }, {
                text: "MVC-3",
                value: 3
            }]
        });
        combo.setValue(2);

        var combo2 = BI.createWidget({
            type: "bi.text_value_check_combo",
            cls: "bi-mvc-text-icon-combo",
            width: 200,
            height: 30,
            items: [{
                text: "MVC-1",
                value: 1
            }, {
                text: "MVC-2",
                value: 2
            }, {
                text: "MVC-3",
                value: 3
            }]
        });
        combo2.setValue(2);
        var combo3 = BI.createWidget({
            type: "bi.editor_icon_check_combo",
            cls: "bi-mvc-text-icon-combo",
            width: 200,
            height: 30,
            items: [{
                //text: "MVC-1",
                value: "1"
            }, {
                //text: "MVC-2",
                value: "2"
            }, {
                //text: "MVC-3",
                value: "3"
            }]
        });
        combo3.setValue("1");
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            hgap: 20,
            vgap: 30,
            items: [combo, combo2, combo3]
        })
    }
});

TextIconComboModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(TextIconComboModel.superclass._defaultConfig.apply(this, arguments), {})
    },

    _init: function () {
        TextIconComboModel.superclass._init.apply(this, arguments);
    }
});