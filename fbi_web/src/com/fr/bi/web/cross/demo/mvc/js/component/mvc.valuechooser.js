ValueChooserView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(ValueChooserView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-pane-modules bi-mvc-layout"
        })
    },

    _init: function () {
        ValueChooserView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var widget = BI.createWidget({
            type: "bi.value_chooser_combo",
            itemsCreator: function (op, callback) {
                callback(BI.deepClone(ITEMS));
            }
        });
        BI.createWidget({
            type: "bi.vertical",
            hgap: 200,
            vgap: 10,
            element: vessel,
            items: [widget]
        })
    }
});

ValueChooserModel = BI.inherit(BI.Model, {});