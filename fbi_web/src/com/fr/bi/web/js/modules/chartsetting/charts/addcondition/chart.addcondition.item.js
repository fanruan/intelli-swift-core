/**
 * Created by GameJian on 2016/7/4.
 */
BI.ChartAddConditionItem = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.ChartAddConditionItem.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-chart-add-condition-item"
        })
    },

    _init: function(){
        BI.ChartAddConditionItem.superclass._init.apply(this, arguments);
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
            self.fireEvent(BI.ChartAddConditionItem.EVENT_CHANGE);
        });

        this.colorChooser = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        //this.colorChooser.setValue(color);

        this.colorChooser.on(BI.ColorChooser.EVENT_CHANGE , function () {
            self.fireEvent(BI.ChartAddConditionItem.EVENT_CHANGE)
        });

        this.deleteIcon = BI.createWidget({
            type: "bi.icon_button",
            cls: "data-link-remove-font",
            width: 25,
            height: 25,
            handler: function () {
                o.onRemoveCondition(o.cid);
            }
        });

        this.deleteIcon.setVisible(false);

        this.element.hover(function () {
            self.deleteIcon.setVisible(true);
        }, function () {
            self.deleteIcon.setVisible(false);
        });

        BI.createWidget({
            type: "bi.left",
            element: this.element,
            items: [this.numberRange, this.colorChooser, this.deleteIcon],
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

BI.ChartAddConditionItem.EVENT_CHANGE = "EVENT_CHANGE";

$.shortcut("bi.chart_add_condition_item", BI.ChartAddConditionItem);