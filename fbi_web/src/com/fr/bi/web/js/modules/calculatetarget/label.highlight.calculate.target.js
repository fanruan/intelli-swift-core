/**
 * Created by roy on 16/4/7.
 */
BI.CalculateTargetHighLightLabel = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.CalculateTargetHighLightLabel.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-calculate-target-highlight-label"
        })
    },

    _init: function () {
        BI.CalculateTargetHighLightLabel.superclass._init.apply(this, arguments);
        var o = this.options;
        this.leftLabel = BI.createWidget({
            type: "bi.label",
            textHeight: o.textHeight,
            textAlign: o.textAlign,
            lgap: 5
        });

        this.highLightLabel = BI.createWidget({
            type: "bi.label",
            cls: "highlight-label",
            textHeight: o.textHeight,
            textAlign: o.textAlign
        });
        this.highLightLabel.doHighLight();
        this.rightLabel = BI.createWidget({
            type: "bi.label",
            textHeight: o.textHeight,
            textAlign: o.textAlign,
            rgap: 5
        });

        BI.createWidget({
            type: "bi.horizontal",
            element: this.element,
            items: [
                this.leftLabel,
                this.highLightLabel,
                this.rightLabel]
        })
    },

    setValue: function (leftValue, highlightValue, rightValue) {
        this.leftLabel.setValue(leftValue);
        this.highLightLabel.setValue(highlightValue);
        this.rightLabel.setValue(rightValue);
    }

});
$.shortcut("bi.calculate_target_highlight_label", BI.CalculateTargetHighLightLabel);