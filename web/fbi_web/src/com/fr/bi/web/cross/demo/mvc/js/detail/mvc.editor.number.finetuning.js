/**
 * Created by windy on 2017/3/13.
 */
FinetuningNumberEditorView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(FinetuningNumberEditorView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "mvc-month-combo bi-mvc-layout"
        })
    },

    _init: function(){
        FinetuningNumberEditorView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){

        var editor = BI.createWidget({
            type: "bi.fine_tuning_number_editor",
            width: 148,
            height: 28
        });

        editor.on(BI.FineTuningNumberEditor.EVENT_CONFIRM, function(){
            label.setText(editor.getValue());
        });

        var label = BI.createWidget({
            type: "bi.label"
        });

        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            hgap: 30,
            vgap: 20,
            items: [editor, label]
        })
    }
});

FinetuningNumberEditorModel = BI.inherit(BI.Model, {

});