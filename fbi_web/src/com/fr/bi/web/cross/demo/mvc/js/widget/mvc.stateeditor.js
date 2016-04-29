StateEditorView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(StateEditorView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-state-editor bi-mvc-layout"
        })
    },

    _init: function(){
        StateEditorView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            hgap: 30,
            vgap: 20,
            items: [{
                el: {
                    type: "bi.state_editor",
                    cls: "mvc-border",
                    watermark: "搜索",
                    errorText: "字段不能为空",
                    height: 30
                }
            }]
        })
    }
});

StateEditorModel = BI.inherit(BI.Model, {

});