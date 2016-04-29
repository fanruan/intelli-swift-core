SignEditorView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(SignEditorView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-sign-editor bi-mvc-layout"
        })
    },

    _init: function(){
        SignEditorView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        var editor = BI.createWidget({
            type: "bi.sign_editor",
            cls: "mvc-border",
            validationChecker: function(v){
                return v != "a";
            },
            watermark: "可以设置标记的输入框",
            text: "这是一个标记，点击它即可进行输入"
        })
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            hgap: 30,
            vgap: 20,
            items: [editor]
        })
    }
});

SignEditorModel = BI.inherit(BI.Model, {

});