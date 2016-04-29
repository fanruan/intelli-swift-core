MaskerView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(MaskerView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-masker bi-mvc-layout"
        })
    },

    _init: function () {
        MaskerView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {

        var btn = BI.createWidget({
            type: "bi.text_button",
            cls: "layout-bg5",
            text: "在此弹出masker层",
            width: 400,
            height: 300,
            handler: function () {
                var masker = BI.Maskers.create("masker", btn);
                BI.Maskers.show("masker");
                masker.element.css("background", "#000000");
            }
        });

        BI.createWidget({
            type: "bi.center_adapt",
            element: vessel,
            items: [btn]
        })
    }
});

MaskerModel = BI.inherit(BI.Model, {

    _init: function () {
        MaskerModel.superclass._init.apply(this, arguments);
    }
});