/**
 * Created by Young's on 2016/3/23.
 */
BI.TargetStyleConditionItem = BI.inherit(BI.Widget, {

    _constant: {
        BUTTON_WIDTH: 25,
        BUTTON_HEIGHT: 25
    },

    _defaultConfig: function () {
        return BI.extend(BI.TargetStyleConditionItem.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-target-style-condition-item"
        })
    },

    _init: function () {
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

        this.numberRange.on(BI.NumericalInterval.EVENT_CHANGE, function () {
            self.fireEvent(BI.TargetStyleConditionItem.EVENT_CHANGE);
        });
        this.setNumLevel(o.numLevel);

        this.colorChooser = BI.createWidget({
            type: "bi.color_chooser",
            width: this._constant.BUTTON_WIDTH,
            height: this._constant.BUTTON_HEIGHT
        });

        this.colorChooser.setValue(color);

        this.deleteIcon = BI.createWidget({
            type: "bi.icon_button",
            cls: "data-link-remove-font",
            width: this._constant.BUTTON_WIDTH,
            height: this._constant.BUTTON_HEIGHT,
            title: BI.i18nText("BI-Remove"),
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
            hgap: 10
        })
    },

    _getNumLevelText: function (numLevel) {
        var numTip = "";
        BI.some(BICst.TARGET_STYLE_LEVEL, function (i, level) {
            if (level.value === numLevel) {
                numTip = level.text;
                return true;
            }
        });
        return numTip;
    },

    setNumLevel: function (numLevel) {
        if (numLevel === BICst.TARGET_STYLE.NUM_LEVEL.NORMAL) {
            this.numberRange.hideNumTip();
            return;
        }
        this.numberRange.setNumTip(this._getNumLevelText(numLevel));
        this.numberRange.showNumTip();
    },

    getValue: function () {
        return {
            range: this.numberRange.getValue(),
            color: this.colorChooser.getValue(),
            cid: this.options.cid
        }
    },

    setValue: function (v) {
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