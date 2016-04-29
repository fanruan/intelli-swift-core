RecordEditorView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(RecordEditorView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-record-editor bi-mvc-layout"
        })
    },

    _init: function(){
        RecordEditorView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        var editor = BI.createWidget({
            type: "bi.record_editor",
            cls: "mvc-border",
            validationChecker: function(v){
                return v != "a";
            },
            watermark: "记录输入内容",
            errorText: "字段不可重名!"
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

RecordEditorModel = BI.inherit(BI.Model, {

});