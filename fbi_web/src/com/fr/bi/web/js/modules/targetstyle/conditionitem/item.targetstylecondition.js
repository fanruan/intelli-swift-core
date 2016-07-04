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

        this.numberRange.on(BI.NumericalInterval.EVENT_CHANGE , function () {
            self.fireEvent(BI.TargetStyleConditionItem.EVENT_CHANGE);
        });

        this.colorChooser = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.colorChooser.setValue(color);

        this.delete = BI.createWidget({
            type: "bi.icon_button",
            cls: "data-link-remove-font",
            width: 30,
            height: 30,
            handler: function () {
                o.onRemoveCondition(o.cid);
            }
        });

        this.delete.setVisible(false);

        this.element.hover(function () {
           self.delete.setVisible(true);
        }, function () {
            self.delete.setVisible(false);
        });

        BI.createWidget({
            type: "bi.left",
            element: this.element,
            items: [this.numberRange, this.colorChooser, this.delete],
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
    },

    setSmallIntervalEnable: function (v) {
        this.numberRange.setMinEnable(v);
        this.numberRange.setCloseMinEnable(v);
    }

});

BI.TargetStyleConditionItem.EVENT_CHANGE = "EVENT_CHANGE";

$.shortcut("bi.target_style_condition_item", BI.TargetStyleConditionItem);