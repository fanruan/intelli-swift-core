ShelterEditorView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(ShelterEditorView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-shelter-editor bi-mvc-layout"
        })
    },

    _init: function () {
        ShelterEditorView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var editor = BI.createWidget({
            type: "bi.shelter_editor",
            cls: "mvc-border",
            validationChecker: function (v) {
                return v != "a";
            },
            watermark: "可以设置标记的输入框",
            text: "这是一个遮罩"
        })
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            hgap: 30,
            vgap: 20,
            bgap: 50,
            items: [editor]
        })
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: {
                    type: "bi.button",
                    text: "focus",
                    height: 25,
                    handler: function () {
                        editor.focus();
                    }
                },
                right: 10,
                left: 10,
                bottom: 10
            }]
        })
    }
});

ShelterEditorModel = BI.inherit(BI.Model, {});