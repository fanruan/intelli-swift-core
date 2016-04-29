AdaptiveEditorView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(AdaptiveEditorView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-adapt-editor bi-mvc-layout"
        })
    },

    _init: function(){
        AdaptiveEditorView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [{
                el: {
                    type: "bi.adapt_editor",
                    cls: "adapt-editor-mvc mvc-border",
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

AdaptiveEditorModel = BI.inherit(BI.Model, {

});