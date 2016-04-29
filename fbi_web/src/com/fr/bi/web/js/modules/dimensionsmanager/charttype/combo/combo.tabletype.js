/**
 * @class BI.TableTypeCombo
 * @extend BI.Widget
 *
 */
BI.TableTypeCombo = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.TableTypeCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-table-type-combo",
            invalid: false,
            items: []
        })
    },

    _init: function () {
        BI.TableTypeCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.trigger = BI.createWidget({
            type: "bi.icon_change_button",
            cls: "chart-type-icon",
            disableSelected: true,
            height: 34,
            width: 40
        });
        this.trigger.setIcon("detail-chart-summary-table-font");
        this.combo = BI.createWidget({
            type: "bi.down_list_combo",
            element: this.element,
            el: this.trigger,
            items: o.items
        });
        this.combo.setValue();
        this.combo.on(BI.DownListCombo.EVENT_CHANGE, function (v) {
            switch (v) {
                case BICst.Widget.TABLE:
                    break;
                case BICst.Widget.CROSS_TABLE:
                    break;
                case BICst.Widget.COMPLEX_TABLE:
                    break;
            }
            self.fireEvent(BI.TableTypeCombo.EVENT_CHANGE, arguments);
        });
        this.combo.on(BI.DownListCombo.EVENT_BEFORE_POPUPVIEW, function () {
            if (this.getValue().length > 0) {
                self.trigger.setSelected(true);
            } else {
                self.trigger.setSelected(false);
            }
        });
    },

    populate: function (items) {
        this.combo.populate(items);
    },

    setValue: function (v) {
        if (v === BICst.Widget.TABLE ||
            v === BICst.Widget.CROSS_TABLE ||
            v === BICst.Widget.COMPLEX_TABLE) {
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
BI.TableTypeCombo.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.table_type_combo", BI.TableTypeCombo);
