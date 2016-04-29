BI.AnalysisOperatorTitle = FR.extend(BI.Widget, {

    _defaultConfig: function() {
        var conf = BI.AnalysisOperatorTitle.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: "bi-analysis-etl-operator-title",
            height:40,
            buttonHeight:30
        })
    },

    _init : function() {
        BI.AnalysisOperatorTitle.superclass._init.apply(this, arguments);
        var o = this.options;

        this.operator = BI.createWidget({
            type:"bi.analysis_operator_button",
            height: o.buttonHeight
        })
        var self = this;
        this.operator.on(BI.AnalysisOperatorButton.EVENT_OPERATOR_CHANGE, function(v){
            self.fireEvent(BI.AnalysisOperatorTitle.EVENT_OPERATOR_CHANGE, arguments)
        })

        this.saveButton = BI.createWidget({
            el: {
                type: "bi.button",
                level: 'ignore',
                height: o.buttonHeight,
                text: BI.i18nText('BI-Save_To_Package'),
                title: BI.i18nText('BI-Save_To_Package'),
                handler: function (v) {
                    self.fireEvent(BI.AnalysisOperatorTitle.EVENT_SAVE, arguments)
                }
            }
        })
        BI.createWidget({
            type:"bi.left_right_vertical_adapt",
            element: this.element,
            height: o.height,
            items: {
                left: [{
                    el: this.operator
                }],
                right: [this.saveButton]
            }
        })
    },

    setEnable : function(v){
        this.operator.setEnable(v)
        this.setSaveButtonEnabled(v)
        this.fireEvent(BI.AnalysisOperatorTitle.EVENT_STATE_CHANGE,v)
    },

    isEnabled : function(){
        return this.operator.isEnabled();
    },

    clearAllSelected : function () {
        this.operator.setValue()
    },

    getValue : function () {
        return this.operator.getValue();
    },

    getElementByValue : function(v) {
        return this.operator.getElementByValue(v)
    },

    setSaveButtonEnabled : function (v) {
        this.saveButton.setEnable(v)
    }
})
BI.AnalysisOperatorTitle.EVENT_OPERATOR_CHANGE = "EVENT_OPERATOR_CHANGE";
BI.AnalysisOperatorTitle.EVENT_STATE_CHANGE = "EVENT_STATE_CHANGE";
BI.AnalysisOperatorTitle.EVENT_SAVE = "ANALYSIS_EVENT_SAVE";
$.shortcut("bi.analysis_operator_title", BI.AnalysisOperatorTitle)