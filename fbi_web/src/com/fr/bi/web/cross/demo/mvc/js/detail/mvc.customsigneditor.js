CustomSignEditorView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(CustomSignEditorView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-adapt-editor bi-mvc-layout"
        })
    },

    _init: function(){
        CustomSignEditorView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [{
                el: {
                    type: "bi.sign_initial_editor",
                    cls: "adapt-editor-mvc mvc-border",
                    watermark: "此乃水印",
                    height: 30,
                    text: "ASD"
                },
                lgap: 20,
                tgap: 20
            }]
        })
    }
});

CustomSignEditorModel = BI.inherit(BI.Model, {

});