TextAreaEditorView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(TextAreaEditorView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-textarea-editor bi-mvc-layout"
        })
    },

    _init: function () {
        TextAreaEditorView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var editor = BI.createWidget({
            type: "bi.textarea_editor",
            cls: "mvc-border",
            width: 600,
            height: 400
        });
        editor.on(BI.TextAreaEditor.EVENT_FOCUS, function () {
            BI.Msg.toast("Focus");
        });
        editor.on(BI.TextAreaEditor.EVENT_BLUR, function () {
            BI.Msg.toast("Blur");
        });
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            hgap: 30,
            vgap: 20,
            items: [editor, {
                type: "bi.button",
                text: "getValue",
                handler: function () {
                    BI.Msg.toast(JSON.stringify(editor.getValue()));
                }
            }, {
                type: "bi.button",
                text: "setValue",
                handler: function () {
                    editor.setValue("测试数据");
                }
            }]
        })
    }
});

TextAreaEditorModel = BI.inherit(BI.Model, {});