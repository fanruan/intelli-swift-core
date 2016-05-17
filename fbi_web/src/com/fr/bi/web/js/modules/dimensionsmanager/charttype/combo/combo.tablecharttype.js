/**
 * @class BI.TableChartCombo
 * @extend BI.Widget
 *
 */
BI.TableChartCombo = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.TableChartCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-table-chart-combo",
            invalid: false,
            items: []
        })
    },

    _init: function () {
        BI.TableChartCombo.superclass._init.apply(this, arguments);
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
            items: [o.items]
        });

        this.values = BI.pluck(o.items, "value");

        this.combo.on(BI.DownListCombo.EVENT_CHANGE, function (v) {
            self._switchIcon(v);
            self.fireEvent(BI.TableChartCombo.EVENT_CHANGE, arguments);
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
        var o = this.options;
        var iconClass = o.items[0].cls || "";
        v = BI.isArray(v) ? v[0] : v;
        BI.any(o.items, function (i, item) {
            if (v === item.value) {
                iconClass = item.cls;
                return true;
            }
        });
        this.trigger.setIcon(iconClass);
    },

    populate: function (items) {
        this.combo.populate([items]);
        this.options.item = items;
        this.values = BI.pluck(items, "value");
    },

    setValue: function (v) {
        if (BI.contains(this.values, v)) {
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
BI.TableChartCombo.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.table_chart_combo", BI.TableChartCombo);