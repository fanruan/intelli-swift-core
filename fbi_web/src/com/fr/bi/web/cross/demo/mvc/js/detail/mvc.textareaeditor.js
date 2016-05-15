/**
 * Created by GameJian on 2016/1/21.
 */
TextAreaEditorView = FR.extend(BI.View,{
    _defaultConfig: function () {
        return BI.extend(TextAreaEditorView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-text-area bi-mvc-layout"
        })
    },

    _init: function () {
        TextAreaEditorView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {

        var self = this, o = this.options;

        var textarea = BI.createWidget({
            type: "bi.text_area",
            cls: "mvc-border",
            width: 300,
            height: 60
        });

        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: textarea,
                left: 100,
                top: 250
            }]
        })
    }
});

TextAreaEditorModel = FR.extend(BI.Model, {});