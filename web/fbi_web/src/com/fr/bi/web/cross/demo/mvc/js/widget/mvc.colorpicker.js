ColorPickerView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(ColorPickerView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-color-picker bi-mvc-layout"
        })
    },

    _init: function () {
        ColorPickerView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var colorpicker = BI.createWidget({
            type: "bi.color_picker",
            width: 200,
            height: 50
        });

        colorpicker.on(BI.ColorPicker.EVENT_CHANGE, function () {
            BI.Msg.toast(this.getValue());
        });

        var colorpicker2 = BI.createWidget({
            type: "bi.color_picker",
            items: [[{
                value: "#d12d02"
            }, {
                value: "#db6700"
            }, {
                value: "#ee9106"
            }, {
                value: "#f7ed02"
            }, {
                value: "#92b801"
            }, {
                value: "",
                disabled: true
            }, {
                value: "",
                disabled: true
            }, {
                value: "",
                disabled: true
            }]],
            width: 200,
            height: 30
        });

        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: colorpicker,
                left: 100,
                top: 50
            }, {
                el: colorpicker2,
                left: 100,
                top: 150
            }]
        })
    }
});

ColorPickerModel = BI.inherit(BI.Model, {});