CodeEditorView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(CodeEditorView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-code-editor bi-mvc-layout"
        })
    },

    _init: function () {
        CodeEditorView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var editor = BI.createWidget({
            type: "bi.code_editor",
            cls: "mvc-border",
            width: 600,
            height: 400
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

CodeEditorModel = BI.inherit(BI.Model, {});