/**
 * @class BI.AbstractTypeCombo
 * @extend BI.Widget
 *
 */
BI.AbstractTypeCombo = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.AbstractTypeCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-abstract-type-combo",
            invalid: false,
            items: []
        })
    },

    _init: function () {
        BI.AbstractTypeCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.trigger = BI.createWidget({
            type: "bi.icon_change_button",
            cls: "chart-type-icon",
            disableSelected: true,
            width: 40
        });
        this._switchIcon();
        this.combo = BI.createWidget({
            type: "bi.down_list_combo",
            element: this.element,
            el: this.trigger,
            items: o.items
        });
        this.combo.setValue();
        this.combo.on(BI.DownListCombo.EVENT_CHANGE, function (v) {
            self._switchIcon(v);
            self.fireEvent(BI.AbstractTypeCombo.EVENT_CHANGE, arguments);
        });
        this.combo.on(BI.DownListCombo.EVENT_BEFORE_POPUPVIEW, function () {
            if (this.getValue().length > 0) {
                self.trigger.setSelected(true);
            } else {
                self.trigger.setSelected(false);
            }
        });
    },

    _switchIcon: function(v){

    },

    populate: function (items) {
        this.combo.populate(items);
    },

    setValue: function (v) {
        if (v === BICst.WIDGET.TABLE ||
            v === BICst.WIDGET.CROSS_TABLE ||
            v === BICst.WIDGET.COMPLEX_TABLE) {
            this.setSelected(true);
        } else {
            this.setSelected(false);
        }
        this.combo.setValue([{value: v}]);
    },

    getValue: function () {
        return this.combo.getValue()[0].value;
    },

    setSelected: function (v) {
        this.trigger.setSelected(v);
    },

    isSelected: function () {
        return this.trigger.isSelected();
    }
});
BI.AbstractTypeCombo.EVENT_CHANGE = "EVENT_CHANGE";