/**
 * trigger of customscale
 * Created by AstronautOO7 on 2016/7/18.
 */
BI.CustomScaleTrigger = BI.inherit(BI.Trigger , {
    _defaultConfig: function() {
        return BI.extend(BI.CustomScaleTrigger.superclass._defaultConfig.apply(this,arguments) , {
            baseCls: "bi-custom-scale-trigger"
        })
    },

    _init: function() {
        BI.CustomScaleTrigger.superclass._init.apply(this , arguments);
        var self = this, o = this.options;

        this.formulaRecord = BI.createWidget({
            type: "bi.text_button",
            cls: "trigger-text-button",
            text: "AstronautOO7",
            textHeight: 30,
            width: 80
        });

        this.formulaRecord.on(BI.TextButton.EVENT_CHANGE , function() {
            self.fireEvent(BI.CustomScaleTrigger.EVENT_CHANGE)
        });

        this.fomulaIcon = BI.createWidget({
            type: "bi.icon_button",
            cls: "calculate-target-font view-button",
            height: 30,
            width: 30
        });

        this.fomulaIcon.on(BI.IconButton.EVENT_CHANGE , function() {
            self.fireEvent(BI.CustomScaleTrigger.EVENT_CHANGE)
        });

        BI.createWidget({
            type: "bi.left",
            element: this.element,
            items: [this.formulaRecord , this.fomulaIcon]
        })
    }

});
BI.CustomScaleTrigger.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.custom_scale_trigger" , BI.CustomScaleTrigger);