/**
 * @class BI.ControlDimensionCombo
 * @extend BI.Widget
 */
BI.ControlDimensionCombo = BI.inherit(BI.Widget, {

    constants: {
        FROM_POSITION: 1
    },

    _defaultConfig: function () {
        return BI.extend(BI.ControlDimensionCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-control-combo"
        })
    },

    defaultItems: function () {
        return [
            [{
                text: BI.i18nText("BI-Remove"),
                value: BICst.CONTROL_COMBO.DELETE,
                cls: "delete-h-font"
            }],
            [{
                text: BI.i18nText("BI-Dimension_From"),
                value: BICst.CONTROL_COMBO.INFO,
                cls: "dimension-from-font",
                disabled: true
            }]
        ]
    },

    _init: function () {
        BI.ControlDimensionCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        var items = this.defaultItems();
        var tableName = BI.Utils.getTableNameByID(BI.Utils.getTableIDByDimensionID(o.dId));
        var fieldName = BI.Utils.getFieldNameByID(BI.Utils.getFieldIDByDimensionID(o.dId));

        items[this.constants.FROM_POSITION][0].text = items[this.constants.FROM_POSITION][0].text + tableName + "." + fieldName;

        this.combo = BI.createWidget({
            type: "bi.down_list_combo",
            element: this.element,
            height: 25,
            iconCls: "detail-dimension-set-font",
            items: items
        });

        this.combo.on(BI.DownListCombo.EVENT_CHANGE, function (v) {
            self.fireEvent(BI.ControlDimensionCombo.EVENT_CHANGE, v);
        });
    },

    setValue: function (v) {
        this.combo.setValue(v);
    },

    getValue: function () {
        return this.combo.getValue();
    }
});

BI.ControlDimensionCombo.EVENT_CHANGE = "EVENT_CHANGE";

$.shortcut("bi.control_dimension_combo", BI.ControlDimensionCombo);