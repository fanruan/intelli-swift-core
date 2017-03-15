ColorPickerEditorView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(ColorPickerEditorView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-color-picker-editor bi-mvc-layout"
        })
    },

    _init: function () {
        ColorPickerEditorView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var editor = BI.createWidget({
            type: "bi.color_picker_editor",
            width: 190,
            height: 50
        });

        var label = BI.createWidget({
            type: "bi.label",
            width: 200,
            height: 50
        });

        editor.on(BI.ColorPickerEditor.EVENT_CHANGE, function () {
            label.setText(editor.getValue());
        });

        editor.setValue("#888888");
        label.setValue("#888888");

        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: editor,
                left: 100,
                top: 50
            }, {
                el: label,
                left: 100,
                top: 100
            }]
        })
    }
});

ColorPickerEditorModel = BI.inherit(BI.Model, {});