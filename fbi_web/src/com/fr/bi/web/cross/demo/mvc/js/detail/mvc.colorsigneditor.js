SignStyleEditorView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(SignStyleEditorView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-sign-style-editor bi-mvc-layout"
        })
    },

    _init: function(){
        SignStyleEditorView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [{
                el: {
                    type: "bi.sign_style_editor",
                    cls: "sign-style-editor-border",
                    tipText: "测试用文本",
                    tipTextCls: "sign-style-editor-mvc",
                    watermark: "此乃水印",
                    height: 30,
                    width: 200,
                    value: "ASD"
                },
                width: 200,
                lgap: 20,
                tgap: 20
            }]
        })
    }
});

SignStyleEditorModel = BI.inherit(BI.Model, {

});