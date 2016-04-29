/**
 * Created by Young's on 2016/3/23.
 */
BI.TargetStyleConditionItem = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.TargetStyleConditionItem.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-target-style-condition-item"
        })
    },

    _init: function(){
        BI.TargetStyleConditionItem.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var range = o.range, color = o.color;
        this.numberRange = BI.createWidget({
            type: "bi.numerical_interval",
            width: 350,
            min: range.min,
            max: range.max,
            closemin: range.closemin,
            closemax: range.closemax
        });

        this.colorChooser = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });
        this.colorChooser.setValue(color);

        BI.createWidget({
            type: "bi.left",
            element: this.element,
            items: [this.numberRange, this.colorChooser, {
                type: "bi.icon_button",
                cls: "data-link-remove-font",
                width: 30,
                height: 30,
                handler: function(){
                    o.onRemoveCondition(o.cid);
                }
            }],
            hgap: 5
        })
    },

    getValue: function(){
        return {
            range: this.numberRange.getValue(),
            color: this.colorChooser.getValue(),
            cid: this.options.cid
        }
    },

    setValue: function(v){
        this.numberRange.setValue(v.range);
        this.colorChooser.setValue(v.color);
    }
});
$.shortcut("bi.target_style_condition_item", BI.TargetStyleConditionItem);