BI.AnalysisETLMergePreviewTableHeaderButton = BI.inherit(BI.BasicButton, {
    
    _defaultConfig : function () {
        return BI.extend(BI.AnalysisETLMergePreviewTableHeaderButton.superclass._defaultConfig.apply(this, arguments), {
            text: "",
            height:40,
            merge:[]
        });
    },
    
    
    _init : function () {
        BI.AnalysisETLMergePreviewTableHeaderButton.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        o.value = o.text;
        var isMerge = o.merge.length > 0;
        var labelItems = [];
        this.label = BI.createWidget({
            type: "bi.label",
            hgap:10,
            textAlign: "center",
            height: isMerge === true ? o.height / 2 : o.height,
            text: o.text,
            title:o.value,
            value: o.value
        })
        labelItems.push(this.label);
        if(isMerge === true) {
            var v = "(" + o.merge.join("/") +")"
            var mergeLabel = BI.createWidget({
                type: "bi.label",
                hgap:10,
                cls:"merge-info",
                textAlign: "center",
                height: o.height /2,
                text: v,
                title:v,
                value: v
            })
            labelItems.push(mergeLabel);
        }
        BI.createWidget({
            type:"bi.vertical",
            element:this.element,
            items:labelItems
        })

    },

    setText: function (text) {
        BI.AnalysisETLMergePreviewTableHeaderButton.superclass.setText.apply(this, arguments);
        this.label.setText(text);
        this.label.setTitle(text);
    },
});
$.shortcut("bi.analysis_etl_merge_p_t_h_button", BI.AnalysisETLMergePreviewTableHeaderButton);