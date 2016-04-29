TextEditorView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(TextEditorView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-text-editor bi-mvc-layout"
        })
    },

    _init: function () {
        TextEditorView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [{
                el: {
                    type: "bi.text_editor",
                    errorText: "字段不可重名!!",
                    watermark: "此乃水印",
                    width: 200,
                    height: 30
                },
                lgap: 20,
                tgap: 20
            }, {
                el: {
                    type: "bi.small_text_editor",
                    errorText: "字段不可重名!!",
                    watermark: "此乃水印",
                    width: 200,
                    height: 30
                },
                lgap: 20,
                tgap: 20
            }]
        })
    }
});

TextEditorModel = BI.inherit(BI.Model, {});