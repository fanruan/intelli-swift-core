/**
 * Created by roy on 16/4/10.
 */
/**
 * Created by roy on 16/4/7.
 */
BI.CalculateTarget2HighLightLabel = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.CalculateTarget2HighLightLabel.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-calculate-target-2highlight-label"
        })
    },

    _init: function () {
        BI.CalculateTarget2HighLightLabel.superclass._init.apply(this, arguments);
        var o = this.options;
        this.leftLabel = BI.createWidget({
            type: "bi.label",
            textHeight: o.textHeight,
            textAlign: o.textAlign,
            lgap: 5
        });

        this.highLightLabelLeft = BI.createWidget({
            type: "bi.label",
            cls: "highlight-label",
            textHeight: o.textHeight,
            textAlign: o.textAlign
        });
        this.highLightLabelLeft.doHighLight();

        this.middleLabel = BI.createWidget({
            type: "bi.label",
            textHeight: o.textHeight,
            textAlign: o.textAlign,
            rgap: 5
        });

        this.highLightLabelRight = BI.createWidget({
            type: "bi.label",
            cls: "highlight-label",
            textHeight: o.textHeight,
            textAlign: o.textAlign
        });
        this.highLightLabelRight.doHighLight();
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
                this.highLightLabelLeft,
                this.middleLabel,
                this.highLightLabelRight,
                this.rightLabel]
        })
    },

    setValue: function (value) {
        this.leftLabel.setValue(value.leftValue || "");
        this.highLightLabelLeft.setValue(value.highLightValueLeft || "");
        this.middleLabel.setValue(value.middleValue || "");
        this.highLightLabelRight.setValue(value.highLightValueRight || "");
        this.rightLabel.setValue(value.rightValue);
    }

});
$.shortcut("bi.calculate_target_2highlight_label", BI.CalculateTarget2HighLightLabel);